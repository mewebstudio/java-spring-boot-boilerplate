package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.security.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final MessageSourceService messageSourceService;

    /**
     * Getting username from the security context.
     *
     * @param aInRoles -- roles that user must have
     * @return boolean -- username or null
     * @throws AccessDeniedException -- if user does not have required roles
     */
    public boolean isAuthorized(final String... aInRoles) throws AccessDeniedException {
        JwtUserDetails jwtUserDetails = getPrincipal();
        if (jwtUserDetails == null) {
            throw new AccessDeniedException(messageSourceService.get("access_denied"));
        }

        try {
            for (String role : aInRoles) {
                for (GrantedAuthority authority : jwtUserDetails.getAuthorities()) {
                    if (authority.getAuthority().equalsIgnoreCase(role)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new AccessDeniedException(messageSourceService.get("access_denied"));
        }

        return false;
    }

    /**
     * Getting user object that is in the security context.
     *
     * @return JwtUserDetails -- security user object or null
     */
    public JwtUserDetails getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return (JwtUserDetails) authentication.getPrincipal();
        } catch (ClassCastException e) {
            log.error("Exception while casting principal to JwtUserDetails, Ex: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
