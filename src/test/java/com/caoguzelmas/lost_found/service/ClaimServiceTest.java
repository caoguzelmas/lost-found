package com.caoguzelmas.lost_found.service;

import com.caoguzelmas.lost_found.exception.ClaimProcessingException;
import com.caoguzelmas.lost_found.exception.NoItemFoundException;
import com.caoguzelmas.lost_found.helper.TestDataBuilder;
import com.caoguzelmas.lost_found.model.dto.ClaimRequestDTO;
import com.caoguzelmas.lost_found.model.dto.ClaimResponseDTO;
import com.caoguzelmas.lost_found.model.entity.Claim;
import com.caoguzelmas.lost_found.model.entity.ItemLocationInventory;
import com.caoguzelmas.lost_found.model.entity.User;
import com.caoguzelmas.lost_found.repository.ClaimRepository;
import com.caoguzelmas.lost_found.repository.ItemLocationInventoryRepository;
import com.caoguzelmas.lost_found.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ItemLocationInventoryRepository itemLocationInventoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClaimService claimService;

    private ClaimRequestDTO claimRequestDTO;
    private ItemLocationInventory testItem;
    private User testUser;
    private Claim testClaim;

    @BeforeEach
    void setUp() {
        claimRequestDTO = TestDataBuilder.createDefaultClaimRequestDTO();
        testItem = TestDataBuilder.createDefaultItem();
        testUser = TestDataBuilder.createDefaultUser();
        testClaim = TestDataBuilder.createClaim(TestDataBuilder.DEFAULT_CLAIM_ID, testItem, testUser, TestDataBuilder.DEFAULT_CLAIM_QUANTITY);
    }

    @Nested
    @DisplayName("Unit tests for createClaim method")
    class CreateClaimTests {

        @Test
        @DisplayName("Validate creating claim successfully")
        void testCreateClaim_whenValidRequestReceived_shouldCreateClaimSuccessfully() throws NoItemFoundException, ClaimProcessingException {
            // Given
            when(itemLocationInventoryRepository.findById(claimRequestDTO.getItemLocationInventoryId())).thenReturn(Optional.of(testItem));
            when(userRepository.findById(claimRequestDTO.getUserId())).thenReturn(Optional.of(testUser));
            when(claimRepository.save(any(Claim.class))).thenReturn(testClaim);
            // When
            final ClaimResponseDTO response = claimService.createClaim(claimRequestDTO);
            // Then
            assertNotNull(response);
            assertEquals(testClaim.getClaimId(), response.getClaimId());
            assertEquals(TestDataBuilder.DEFAULT_CLAIM_QUANTITY, response.getClaimedQuantity());
        }

        @Test
        @DisplayName("Validate throws NoItemFoundException when no item found")
        void testCreateClaim_whenNoItemFound_shouldThrowNoItemFoundException() {
            // Given
            when(itemLocationInventoryRepository.findById(claimRequestDTO.getItemLocationInventoryId())).thenReturn(Optional.empty());
            // When
            final NoItemFoundException exception = assertThrows(NoItemFoundException.class, () -> claimService.createClaim(claimRequestDTO));
            // Then
            assertTrue(exception.getMessage().contains(String.valueOf(claimRequestDTO.getItemLocationInventoryId())));
            verify(itemLocationInventoryRepository, times(1)).findById(claimRequestDTO.getItemLocationInventoryId());
            verify(userRepository, never()).findById(anyLong());
            verify(claimRepository, never()).save(any(Claim.class));
        }

        @Test
        @DisplayName("Validate throws ClaimProcessingException when no user found")
        void testCreateClaim_whenNoUserFound_shouldThrowClaimProcessingException() {
            // Given
            when(itemLocationInventoryRepository.findById(claimRequestDTO.getItemLocationInventoryId())).thenReturn(Optional.of(testItem));
            when(userRepository.findById(claimRequestDTO.getUserId())).thenReturn(Optional.empty());
            // When
            final ClaimProcessingException exception = assertThrows(ClaimProcessingException.class, () -> claimService.createClaim(claimRequestDTO));
            // Then
            assertTrue(exception.getMessage().contains(String.valueOf(claimRequestDTO.getUserId())));
            verify(itemLocationInventoryRepository, times(1)).findById(claimRequestDTO.getItemLocationInventoryId());
            verify(userRepository, times(1)).findById(claimRequestDTO.getUserId());
            verify(claimRepository, never()).save(any(Claim.class));
        }

        @Test
        @DisplayName("Validate throws ClaimProcessingException when claimed quantity is less than or equals to zero")
        void testCreateClaim_whenClaimedQuantityIsInvalid_shouldThrowClaimProcessingException() {
            // Given
            final String expectedMessageContent = "cannot be less than or equal to 0";
            when(itemLocationInventoryRepository.findById(claimRequestDTO.getItemLocationInventoryId())).thenReturn(Optional.of(testItem));
            final ClaimRequestDTO invalidRequestZero = TestDataBuilder.createClaimRequestDTO(TestDataBuilder.DEFAULT_USER_ID, TestDataBuilder.DEFAULT_ITEM_ID, 0);
            final ClaimRequestDTO invalidRequestNegative = TestDataBuilder.createClaimRequestDTO(TestDataBuilder.DEFAULT_USER_ID, TestDataBuilder.DEFAULT_ITEM_ID, -1);
            // When
            final ClaimProcessingException exceptionZero = assertThrows(ClaimProcessingException.class, () -> claimService.createClaim(invalidRequestZero));
            final ClaimProcessingException exceptionNegative = assertThrows(ClaimProcessingException.class, () -> claimService.createClaim(invalidRequestNegative));
            // Then
            assertTrue(exceptionZero.getMessage().contains(expectedMessageContent));
            assertTrue(exceptionNegative.getMessage().contains(expectedMessageContent));
            verify(claimRepository, never()).save(any(Claim.class));
        }

        @Test
        @DisplayName("Validate throws ClaimProcessingException when claimed quantity is more than available quantity")
        void testCreateClaim_whenClaimedQuantityIsMoreThenAvailableQuantity_shouldThrowClaimProcessingException() {
            // Given
            final ClaimRequestDTO excessiveQuantityRequest = TestDataBuilder.createClaimRequestDTO(
                    TestDataBuilder.DEFAULT_USER_ID,
                    TestDataBuilder.DEFAULT_ITEM_ID,
                    testItem.getTotalFoundQuantity() + 1);
            when(itemLocationInventoryRepository.findById(excessiveQuantityRequest.getItemLocationInventoryId())).thenReturn(Optional.of(testItem));
            // When
            final ClaimProcessingException exception = assertThrows(ClaimProcessingException.class, () -> claimService.createClaim(excessiveQuantityRequest));
            // Then
            assertTrue(exception.getMessage().contains("cannot be more than available quantity "));
            verify(itemLocationInventoryRepository, times(1)).findById(excessiveQuantityRequest.getItemLocationInventoryId());
            verify(claimRepository, never()).save(any(Claim.class));
        }
    }

    @Nested
    @DisplayName("Unit tests for getAllClaimsForAdmin method")
    class GetAllClaimsForAdminTests {

        @Test
        @DisplayName("Validate returns DTO List when more than one Claims stored")
        void testGetAllClaimsForAdmin_whenMoreThanOneClaimsStored_shouldReturnDTOList() {
            // Given
            final User anotherUser = TestDataBuilder.createUser(2L, "another_user");
            final ItemLocationInventory anotherItem = TestDataBuilder.createItem(20L, "Mouse", "Desk", 2);
            final Claim claim1 = TestDataBuilder.createClaim(100L, testItem, testUser, 1);
            final Claim claim2 = TestDataBuilder.createClaim(101L, anotherItem, anotherUser, 1);
            final List<Claim> claims = Arrays.asList(claim1, claim2);

            when(claimRepository.findAll()).thenReturn(claims);
            // When
            final List<ClaimResponseDTO> responseDTOs = claimService.getAllClaimsForAdmin();
            // Then
            assertNotNull(responseDTOs);
            assertEquals(2, responseDTOs.size());

            assertEquals(claim1.getClaimId(), responseDTOs.get(0).getClaimId());
            assertEquals(testUser.getUsername(), responseDTOs.get(0).getUserDetails().getUsername());
            assertEquals(testItem.getItemName(), responseDTOs.get(0).getLostItemDetails().getItemName());

            assertEquals(claim2.getClaimId(), responseDTOs.get(1).getClaimId());
            assertEquals(anotherUser.getUsername(), responseDTOs.get(1).getUserDetails().getUsername());
            assertEquals(anotherItem.getItemName(), responseDTOs.get(1).getLostItemDetails().getItemName());

            verify(claimRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Validate returns empty DTO List when no Claims stored")
        void testGetAllClaimsForAdmin_whenNoClaimsStored_shouldReturnEmptyDTOList() {
            // Given
            when(claimRepository.findAll()).thenReturn(Collections.emptyList());
            // When
            final List<ClaimResponseDTO> responseDTOs = claimService.getAllClaimsForAdmin();
            // Then
            assertNotNull(responseDTOs);
            assertTrue(responseDTOs.isEmpty());
            verify(claimRepository, times(1)).findAll();
        }
    }
}
