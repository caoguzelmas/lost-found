package com.caoguzelmas.lost_found.service;

import com.caoguzelmas.lost_found.exception.ClaimProcessingException;
import com.caoguzelmas.lost_found.exception.NoItemFoundException;
import com.caoguzelmas.lost_found.model.dto.ClaimRequestDTO;
import com.caoguzelmas.lost_found.model.dto.ClaimResponseDTO;
import com.caoguzelmas.lost_found.model.dto.LostItemDTO;
import com.caoguzelmas.lost_found.model.dto.UserDTO;
import com.caoguzelmas.lost_found.model.entity.Claim;
import com.caoguzelmas.lost_found.model.entity.ItemLocationInventory;
import com.caoguzelmas.lost_found.model.entity.User;
import com.caoguzelmas.lost_found.repository.ClaimRepository;
import com.caoguzelmas.lost_found.repository.ItemLocationInventoryRepository;
import com.caoguzelmas.lost_found.repository.UserRepository;
import com.caoguzelmas.lost_found.util.ErrorMessageConstants;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ClaimService.class);
    private final ClaimRepository claimRepository;
    private final ItemLocationInventoryRepository itemLocationInventoryRepository;
    private final UserRepository userRepository;
    private final MockUserService mockUserService;

    public ClaimService(ClaimRepository claimRepository,
                        ItemLocationInventoryRepository itemLocationInventoryRepository,
                        UserRepository userRepository,
                        MockUserService mockUserService) {
        this.claimRepository = claimRepository;
        this.itemLocationInventoryRepository = itemLocationInventoryRepository;
        this.userRepository = userRepository;
        this.mockUserService = mockUserService;
    }

    @Transactional
    public ClaimResponseDTO createClaim(ClaimRequestDTO claimRequest) throws NoItemFoundException, ClaimProcessingException {
        log.info("Attempting to create claim for user ID: {} and item ID: {}", claimRequest.getUserId(), claimRequest.getItemLocationInventoryId());

        // find the requested ItemLocationInventory
        final ItemLocationInventory item = itemLocationInventoryRepository.findById(claimRequest.getItemLocationInventoryId())
                .orElseThrow(() -> {
                    final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_NO_ITEM_FOUND, claimRequest.getItemLocationInventoryId());
                    log.warn(errorMessage);
                    return new NoItemFoundException(errorMessage);
                });

        // validate the requested quantity
        if (claimRequest.getClaimedQuantity() == null || claimRequest.getClaimedQuantity() <= 0) {
            final String errorMessage = String.format(
                    ErrorMessageConstants.ERROR_MESSAGE_REQUESTED_NUMBER_OF_QUANTITY_CANNOT_BE_LESS_OR_EQUAL_ZERO, claimRequest.getClaimedQuantity());
            log.warn(errorMessage);
            throw new ClaimProcessingException(errorMessage);
        }

        if (claimRequest.getClaimedQuantity() > item.getTotalFoundQuantity()) {
            final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_INVALID_NUMBER_OF_QUANTITY,
                    claimRequest.getClaimedQuantity(), item.getTotalFoundQuantity());
            log.warn(errorMessage);
            throw new ClaimProcessingException(errorMessage);
        }

        final User user = userRepository.findById(claimRequest.getUserId())
                .orElseThrow(() -> {
                    final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_USER_NOT_FOUND, claimRequest.getUserId());
                    log.warn(errorMessage);
                    return new ClaimProcessingException(errorMessage);
                });

        final Claim claim = Claim.builder()
                .itemLocationInventory(item)
                .user(user)
                .claimedQuantity(claimRequest.getClaimedQuantity())
                .build();

        final Claim savedClaim = claimRepository.save(claim);
        log.info("Claim successfully saved with ID: {} for user ID: {} and item ID: {}",
                savedClaim.getClaimId(), user.getUserId(), item.getItemLocationInventoryId());

        return convertToClaimResponseDTO(savedClaim);
    }

    public List<ClaimResponseDTO> getAllClaimsForAdmin() {
        log.info("Fetching all claims for admin");
        final List<Claim> claims = claimRepository.findAll();

        final List<ClaimResponseDTO> claimResponseDTOS = claims.stream()
                .map(this::convertToClaimResponseDTO)
                .toList();

        log.info("Successfully retrieved {} claims", claimResponseDTOS.size());

        return claimResponseDTOS;
    }

    private ClaimResponseDTO convertToClaimResponseDTO(final Claim claimEntity) {
        if (claimEntity == null) {
            return null;
        }
        // set claim details
        final ClaimResponseDTO claimResponseDTO = ClaimResponseDTO.builder()
                .claimId(claimEntity.getClaimId())
                .claimedQuantity(claimEntity.getClaimedQuantity())
                .build();

        // set item details
        final ItemLocationInventory itemEntity = claimEntity.getItemLocationInventory();

        if (itemEntity != null) {
            final LostItemDTO lostItemDetails = LostItemDTO.builder()
                    .itemLocationInventoryId(itemEntity.getItemLocationInventoryId())
                    .itemName(itemEntity.getItemName())
                    .place(itemEntity.getPlace())
                    .totalFoundQuantity(itemEntity.getTotalFoundQuantity())
                    .build();
            claimResponseDTO.setLostItemDetails(lostItemDetails);
        }

        final User userEntity = claimEntity.getUser();

        if (userEntity != null) {
            final UserDTO userDetails = UserDTO.builder()
                    .userId(userEntity.getUserId())
                    .username(userEntity.getUsername()).build();
            claimResponseDTO.setUserDetails(userDetails);
        }

        return claimResponseDTO;
    }
}
