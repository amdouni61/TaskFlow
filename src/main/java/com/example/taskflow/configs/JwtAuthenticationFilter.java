package com.example.taskflow.configs;

import com.example.taskflow.service.JwtService;
import com.example.taskflow.service.OnlineStatusService;
import com.example.taskflow.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final SessionService sessionService;
    private final OnlineStatusService onlineStatusService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        logger.debug("🔐 JWT Filter - Processing request: " + request.getRequestURI());
        logger.debug("🔐 JWT Filter - Authorization header: " + (authHeader != null ? "present" : "missing"));
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("🔐 JWT Filter - No Bearer token, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        
        logger.debug("🔐 JWT Filter - Extracted user email: " + userEmail);
        
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("🔐 JWT Filter - Loading user details for: " + userEmail);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            logger.debug("🔐 JWT Filter - User details loaded: " + (userDetails != null ? "success" : "failed"));
            
            // Check if user is hidden/deactivated
            boolean isHidden = sessionService.isUserHidden(userEmail);
            logger.debug("🔐 JWT Filter - User hidden check result: " + isHidden);
            
            if (isHidden) {
                logger.warn("Access denied: User " + userEmail + " is deactivated");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("User account is deactivated");
                return;
            }
            
            // Check if user session is expired
            boolean isSessionExpired = sessionService.isSessionExpired(userEmail);
            logger.debug("🔐 JWT Filter - Session expired check result: " + isSessionExpired);
            
            if (isSessionExpired) {
                logger.warn("Access denied: User " + userEmail + " session expired");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Session expired. Please login again.");
                return;
            }
            
            boolean isTokenValid = jwtService.isTokenValid(jwt, userDetails);
            logger.debug("🔐 JWT Filter - Token validity check result: " + isTokenValid);
            
            if (isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                // Update user's last activity
                sessionService.updateUserActivity(userEmail);
                
                // Track user online status (only on first request of session)
                String sessionId = getOrCreateSessionId(request, userEmail);
                if (sessionId != null) {
                    onlineStatusService.userConnected(userEmail, sessionId);
                }
                
                logger.info("JWT token is valid for user: " + userEmail);
            } else {
                logger.warn("🔐 JWT Filter - Token validation failed for user: " + userEmail);
            }
        } else {
            logger.debug("🔐 JWT Filter - User email is null or already authenticated");
        }
        
        filterChain.doFilter(request, response);
    }

    private String getOrCreateSessionId(HttpServletRequest request, String userEmail) {
        // Check if this is a new session for the user
        String existingSessionId = (String) request.getSession().getAttribute("userSessionId");
        if (existingSessionId == null) {
            // Create new session ID for this user
            String newSessionId = UUID.randomUUID().toString();
            request.getSession().setAttribute("userSessionId", newSessionId);
            request.getSession().setAttribute("userEmail", userEmail);
            return newSessionId;
        }
        return null; // Return null if session already exists
    }
}
