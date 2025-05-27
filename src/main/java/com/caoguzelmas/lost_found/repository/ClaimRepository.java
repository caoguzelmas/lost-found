package com.caoguzelmas.lost_found.repository;

import com.caoguzelmas.lost_found.model.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    //
}
