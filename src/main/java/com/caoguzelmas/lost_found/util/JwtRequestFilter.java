package com.caoguzelmas.lost_found.util;

import com.caoguzelmas.lost_found.util.constants.ErrorMessageConstants;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
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
                log.warn(String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_TOKEN_EXPIRED, e.getMessage()));
            } catch (Exception e) {
                log.error(String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_UNEXPECTED_ERROR, e.getMessage()), e);
            }
        } else {
            log.trace(String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_INVALID_TOKEN, request.getRequestURI()));
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;

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
                log.warn(String.format(ErrorMessageConstants.ERROR_MESSAGE_JWT_TOKEN_VALIDATION_FAILED, username));
            }
        }
        filterChain.doFilter(request, response);
    }
}
