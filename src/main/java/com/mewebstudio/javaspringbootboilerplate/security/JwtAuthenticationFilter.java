package com.mewebstudio.javaspringbootboilerplate.security;

import com.mewebstudio.javaspringbootboilerplate.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Profile("!mvcIT")
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    @Override
    protected final void doFilterInternal(@NonNull final HttpServletRequest request,
                                          @NonNull final HttpServletResponse response,
                                          @NonNull final FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.extractJwtFromRequest(request);
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token, request)) {
            String id = jwtTokenProvider.getUserIdFromToken(token);
            UserDetails user = userService.loadUserById(id);

            if (Objects.nonNull(user)) {
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationManager.authenticate(auth);
            }
        }

        filterChain.doFilter(request, response);
        log.info(request.getRemoteAddr());
    }
}
