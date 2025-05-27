package com.caoguzelmas.lost_found.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "claims")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_location_inventory_id")
    private ItemLocationInventory itemLocationInventory;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "claimed_quantity")
    private int claimedQuantity;
}
