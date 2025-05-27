package com.caoguzelmas.lost_found.controller;

import com.caoguzelmas.lost_found.model.entity.Claim;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    @PostMapping
    public ResponseEntity claim(@RequestBody Claim claim) {
        return null;
    }

    @GetMapping
    public ResponseEntity getAllClaims() {
        return null;
    }
    // claimItem POST

    // getAllClaims GET

    // claim service
}
