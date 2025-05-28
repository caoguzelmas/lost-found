package com.caoguzelmas.lost_found.helper;

import com.caoguzelmas.lost_found.model.dto.*;
import com.caoguzelmas.lost_found.model.entity.Claim;
import com.caoguzelmas.lost_found.model.entity.ItemLocationInventory;
import com.caoguzelmas.lost_found.model.entity.Role;
import com.caoguzelmas.lost_found.model.entity.User;

import java.util.ArrayList;

public class TestDataBuilder {

    private void TestDataFactory() {}

    public static final Long DEFAULT_USER_ID = 1L;
    public static final String DEFAULT_USERNAME = "test_user";
    public static final Long DEFAULT_ITEM_ID = 10L;
    public static final String DEFAULT_ITEM_NAME = "Laptop";
    public static final String DEFAULT_ITEM_PLACE = "Office";
    public static final Long DEFAULT_CLAIM_ID = 100L;
    public static final Integer DEFAULT_CLAIM_QUANTITY = 1;
    public static final Integer DEFAULT_TOTAL_FOUND_QUANTITY = 5;


    // USER Related Test Data
    public static User createUser(Long userId, String username) {
        return User.builder()
                .userId(userId)
                .username(username)
                .firstName("Test")
                .lastName("User")
                .password("password")
                .role(Role.valueOf("USER"))
                .build();
    }

    public static User createDefaultUser() {
        return createUser(DEFAULT_USER_ID, DEFAULT_USERNAME);
    }

    public static UserDTO createUserDTO(Long userId, String username) {
        return UserDTO.builder()
                .userId(userId)
                .username(username)
                .build();
    }

    public static UserDTO createDefaultUserDTO() {
        return createUserDTO(DEFAULT_USER_ID, DEFAULT_USERNAME);
    }

    // ItemLocationInventory Related Test Data
    public static ItemLocationInventory createItem(Long itemId, String itemName, String place, int totalQuantity) {
        return ItemLocationInventory.builder()
                .itemLocationInventoryId(itemId)
                .itemName(itemName)
                .place(place)
                .totalFoundQuantity(totalQuantity)
                .findingEvents(new ArrayList<>())
                .build();
    }

    public static ItemLocationInventory createDefaultItem() {
        return createItem(DEFAULT_ITEM_ID, DEFAULT_ITEM_NAME, DEFAULT_ITEM_PLACE, DEFAULT_TOTAL_FOUND_QUANTITY);
    }

    public static LostItemDTO createLostItemDTO(Long itemId, String itemName, String place, int totalQuantity) {
        return LostItemDTO.builder()
                .itemLocationInventoryId(itemId)
                .itemName(itemName)
                .place(place)
                .totalFoundQuantity(totalQuantity)
                .build();
    }

    public static LostItemDTO createDefaultLostItemDTO() {
        return createLostItemDTO(DEFAULT_ITEM_ID, DEFAULT_ITEM_NAME, DEFAULT_ITEM_PLACE, DEFAULT_TOTAL_FOUND_QUANTITY);
    }

    public static ParsedItemDataDTO createParsedItemDataDTO(String name, int quantity, String place) {
        return ParsedItemDataDTO.builder()
                .itemName(name)
                .quantity(quantity)
                .place(place)
                .build();
    }

    // Claim Related Test Data
    public static ClaimRequestDTO createClaimRequestDTO(Long userId, Long itemId, int quantity) {
         return ClaimRequestDTO.builder()
                .userId(userId)
                .itemLocationInventoryId(itemId)
                .claimedQuantity(quantity)
                .build();
    }

    public static ClaimRequestDTO createDefaultClaimRequestDTO() {
        return createClaimRequestDTO(DEFAULT_USER_ID, DEFAULT_ITEM_ID, DEFAULT_CLAIM_QUANTITY);
    }

    public static Claim createClaim(Long claimId, ItemLocationInventory item, User user, int quantity) {
        return Claim.builder()
                .claimId(claimId)
                .itemLocationInventory(item)
                .user(user)
                .claimedQuantity(quantity)
                .build();
    }

    public static Claim createDefaultClaim(ItemLocationInventory item, User user) {
        return createClaim(DEFAULT_CLAIM_ID, item, user, DEFAULT_CLAIM_QUANTITY);
    }

    public static ClaimResponseDTO createClaimResponseDTO(Long claimId, LostItemDTO itemDetails, UserDTO userDetails, int claimedQuantity) {
        return ClaimResponseDTO.builder()
                .claimId(claimId)
                .lostItemDetails(itemDetails)
                .userDetails(userDetails)
                .claimedQuantity(claimedQuantity)
                .build();
    }

    public static ClaimResponseDTO createDefaultClaimResponseDTO() {
        return createClaimResponseDTO(
                DEFAULT_CLAIM_ID,
                createDefaultLostItemDTO(),
                createDefaultUserDTO(),
                DEFAULT_CLAIM_QUANTITY
        );
    }
}
