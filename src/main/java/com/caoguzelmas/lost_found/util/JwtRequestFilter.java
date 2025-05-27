package com.caoguzelmas.lost_found.util;

import com.caoguzelmas.lost_found.model.dto.ErrorResponseDTO;
import com.caoguzelmas.lost_found.util.constants.ErrorMessageConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtRequestFilter(JwtUtil jwtUtil,
                            UserDetailsService userDetailsService,
                            ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String username = null;

        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            // remove the "bearer" part from token
            jwtToken = authorizationHeader.substring(7);

            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (ExpiredJwtException e) {
                final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_TOKEN_EXPIRED, e.getMessage());
                log.warn(errorMessage);
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, errorMessage);
            } catch (Exception e) {
                final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_UNEXPECTED_ERROR, e.getMessage());
                log.error(errorMessage, e);
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, errorMessage);
            }
        } else {
            log.trace(String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_INVALID_TOKEN, request.getRequestURI()));
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;

            try {
                userDetails = this.userDetailsService.loadUserByUsername(username);

                // validate the token
                if (jwtUtil.validateToken(jwtToken, userDetails)) {
                    final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.debug("User '{}' authenticated successfully via JWT. Setting security context.", username);
                } else {
                    log.warn(String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_TOKEN_VALIDATION_FAILED, username));
                }
            } catch (Exception e) {
                final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_TOKEN_VALIDATION_FAILED, e.getMessage());
                log.warn(errorMessage);
                sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(final HttpServletResponse response,
                                   final HttpStatus status,
                                   final String errorMessage) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        final ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(errorMessage)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
