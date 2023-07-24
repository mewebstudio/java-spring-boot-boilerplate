package com.mewebstudio.javaspringbootboilerplate.security;

import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.service.MessageSourceService;
import com.mewebstudio.javaspringbootboilerplate.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationManager implements AuthenticationManager {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final MessageSourceService messageSourceService;

    /**
     * Authenticate user.
     *
     * @param authentication Authentication
     */
    @Override
    @Transactional
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        User user = userService.findByEmail(authentication.getName());

        if (Objects.nonNull(authentication.getCredentials())) {
            boolean matches = passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword());
            if (!matches) {
                log.error("AuthenticationCredentialsNotFoundException occurred for {}", authentication.getName());
                throw new AuthenticationCredentialsNotFoundException(messageSourceService.get("bad_credentials"));
            }
        }

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .toList();
        UserDetails userDetails = userService.loadUserByEmail(authentication.getName());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
            user.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return auth;
    }
}
