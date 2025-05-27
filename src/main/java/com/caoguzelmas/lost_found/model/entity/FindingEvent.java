package com.caoguzelmas.lost_found.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "finding_event")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindingEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long findingEventId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_location_inventory_id")
    private ItemLocationInventory itemLocationInventory;
    @Column(name = "found_quantity")
    private Integer foundQuantity;
    @Column(name = "source_file_info")
    private String sourceFileInfo;
}
