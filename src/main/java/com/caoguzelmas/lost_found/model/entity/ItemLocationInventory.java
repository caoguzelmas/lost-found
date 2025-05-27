package com.caoguzelmas.lost_found.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item_location_inventory", uniqueConstraints = @UniqueConstraint(columnNames = {"item_name", "place"}))
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemLocationInventory extends BaseEntity {

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
    @OneToMany(mappedBy = "itemLocationInventory", cascade = CascadeType.ALL)
    private List<FindingEvent> findingEvents = new ArrayList<>();

    public void addFindingEvent(final FindingEvent findingEvent) {
        this.findingEvents.add(findingEvent);
        findingEvent.setItemLocationInventory(this);
        this.totalFoundQuantity += findingEvent.getFoundQuantity();
    }
}
