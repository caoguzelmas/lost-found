package com.caoguzelmas.lost_found.service;

import com.caoguzelmas.lost_found.repository.UserRepository;
import com.caoguzelmas.lost_found.util.constants.ErrorMessageConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_USER_NOT_FOUND, username);
                    log.warn(errorMessage);
                    return new UsernameNotFoundException(errorMessage);
                });
    }
}
