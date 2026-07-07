package nus.iss.wellness.backend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.repository.UserRepository;
import nus.iss.wellness.backend.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Validates the Bearer JWT on every request (except /api/auth/**).
 * On success sets the user's Long userId as the Spring Security principal
 * so controllers can read it via Authentication.getPrincipal().
 *
 * Author: Htet Nandar
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Cookie name kept for AuthController logout endpoint compatibility. */
    public static final String AUTH_COOKIE_NAME = "authJwtToken";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Auth endpoints are public — skip token check
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // No token header — pass through; Spring Security will reject if endpoint requires auth
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtService.authenticateToken(token);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            //fixed logic flow
            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isEmpty()) {
                sendUnauthorized(response, "User not found");
                return;
            }

            User user = userOpt.get();

            // Check whether the JWT matches the one stored in the database
            if (user.getJwtToken() == null || !token.equals(user.getJwtToken())) {
                sendUnauthorized(response, "Token has been invalidated");
                return;
            }


            // Set userId (Long) as principal so controllers can cast directly:
            //   Long userId = (Long) authentication.getPrincipal();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getUserId(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            sendUnauthorized(response, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
