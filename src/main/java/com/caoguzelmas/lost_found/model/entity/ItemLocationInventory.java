package com.caoguzelmas.lost_found.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "item_location_inventory", uniqueConstraints = @UniqueConstraint(columnNames = {"item_name", "place"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemLocationInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemLocationInventoryId;
    @Column(name = "item_name")
    private String itemName;
    @Column(name = "place")
    private String place;
    @Column(name = "total_found_quantity")
    private Integer totalFoundQuantity;
    @OneToMany(mappedBy = "itemLocationInventory")
    private List<Claim> claims;
    @OneToMany(mappedBy = "itemLocationInventory")
    private List<FindingEvent> findingEvents;

}
