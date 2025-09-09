package com.LuckyHub.Backend.Configuration;

import com.LuckyHub.Backend.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Extract JWT from "Authorization" header
        final String authHeader = request.getHeader("Authorization");

        // 2. If header is missing OR does not start with "Bearer ", skip and continue the filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract JWT Token
        final String token = authHeader.substring(7);

        // 4. Extract username from token
        final String userName = jwtService.extractUserEmail(token);

        // 5. Check if user is not already authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (userName != null && authentication == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

            // 6. Validate token
            if (jwtService.isTokenValid(token, userDetails)) {

                // check if user is verified
                Boolean isVerified = (Boolean) jwtService.extractAllClaims(token).get("isVerified");
                if (isVerified == null || !isVerified) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"status\":403 ,\"error\":\"Account not verified\",\"message\":\"Please verify your email.\"}");
                    return;
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // 7. Attach request details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 8. Store authentication in context (so Spring Security knows the user is logged in)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
          // 9. Continue filter chain
        filterChain.doFilter(request, response);
    }
}
