package io.github.dkuechler.longjavaty.healthmetrics.config;

import io.github.dkuechler.longjavaty.users.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class UserSynchronizationFilter extends OncePerRequestFilter {

    private final UserService userService;

    public UserSynchronizationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String sub = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            if (email == null) {
                email = "unknown-" + sub + "@example.com";
            }

            try {
                UUID userId = UUID.fromString(sub);
                userService.getOrCreateUser(userId, email);
            } catch (IllegalArgumentException e) {
            }
        }

        filterChain.doFilter(request, response);
    }
}
