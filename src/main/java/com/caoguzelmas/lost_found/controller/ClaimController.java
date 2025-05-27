package com.caoguzelmas.lost_found.controller;

import com.caoguzelmas.lost_found.exception.ClaimProcessingException;
import com.caoguzelmas.lost_found.exception.NoItemFoundException;
import com.caoguzelmas.lost_found.model.dto.ClaimRequestDTO;
import com.caoguzelmas.lost_found.model.dto.ClaimResponseDTO;
import com.caoguzelmas.lost_found.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<ClaimResponseDTO> claim(@Valid @RequestBody ClaimRequestDTO claimRequestDTO) throws NoItemFoundException, ClaimProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(claimService.createClaim(claimRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<ClaimResponseDTO>> getAllClaims() {
        return ResponseEntity.ok(claimService.getAllClaimsForAdmin());
    }
}
