package com.caoguzelmas.lost_found.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "finding_event")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long findingEventId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_location_inventory_id")
    private ItemLocationInventory itemLocationInventoryId;
    @Column(name = "found_quantity")
    private Integer foundQuantity;
    @Column(name = "source_file_info")
    private String sourceFileInfo;
}
