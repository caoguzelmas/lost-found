package com.caoguzelmas.lost_found.repository;

import com.caoguzelmas.lost_found.model.entity.ItemLocationInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemLocationInventoryRepository extends JpaRepository<ItemLocationInventory, Long> {

    Optional<ItemLocationInventory> findByItemNameAndPlace(final String itemName, final String place);
}
