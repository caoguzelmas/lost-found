package com.caoguzelmas.lost_found.controller;

import com.caoguzelmas.lost_found.model.dto.LoginRequestDTO;
import com.caoguzelmas.lost_found.model.dto.LoginResponseDTO;
import com.caoguzelmas.lost_found.util.JwtUtil;
import com.caoguzelmas.lost_found.util.constants.ErrorMessageConstants;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Authentication attempt for user: {}", loginRequest.getUsername());

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            log.info("Authentication successful for user: {}", loginRequest.getUsername());
        } catch (BadCredentialsException e) {
            final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_INVALID_CREDENTIALS, loginRequest.getUsername());
            log.warn(errorMessage);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        } catch (AuthenticationException e) {
            final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_AUTHENTICATION_FAILED, loginRequest.getUsername(), e.getMessage());
            log.warn(errorMessage);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        } catch (Exception e) {
            final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_UNEXPECTED_ERROR, e);
            log.error(errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        log.info("UserDetails loaded for user '{}' after successful authentication.", userDetails.getUsername());

        final String jwtToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(LoginResponseDTO.builder()
                .jwtToken(jwtToken)
                .username(userDetails.getUsername()).build());
    }
}
