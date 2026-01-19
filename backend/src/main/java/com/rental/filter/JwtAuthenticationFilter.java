package com.rental.filter;

import com.rental.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    private static final String TOKEN_COOKIE_NAME = "authToken";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        logger.debug("Processing request: {} {}", request.getMethod(), requestPath);
        
        String token = getTokenFromCookie(request);
        
        if (token != null) {
            logger.debug("JWT token found in cookie for request: {}", requestPath);
            
            if (jwtUtil.validateToken(token)) {
                try {
                    String email = jwtUtil.extractEmail(token);
                    String roleName = jwtUtil.extractRoleName(token);
                    
                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + roleName)
                        );
                        
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(email, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("Authentication set for user: {} with role: {} for request: {}", 
                                email, roleName, requestPath);
                    }
                } catch (Exception e) {
                    logger.error("Cannot set user authentication for request: {} - Error: {}", 
                            requestPath, e.getMessage(), e);
                }
            } else {
                logger.warn("Invalid JWT token for request: {}", requestPath);
            }
        } else {
            logger.debug("No JWT token found in cookie for request: {}", requestPath);
        }
        
        filterChain.doFilter(request, response);
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            logger.debug("Found {} cookies in request: {}", cookies.length, 
                    java.util.Arrays.stream(cookies)
                            .map(c -> c.getName() + "=" + (c.getValue() != null && c.getValue().length() > 20 
                                    ? c.getValue().substring(0, 20) + "..." : c.getValue()))
                            .collect(java.util.stream.Collectors.joining(", ")));
            for (Cookie cookie : cookies) {
                if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    logger.debug("Found authToken cookie with path: {}, domain: {}", 
                            cookie.getPath(), cookie.getDomain());
                    return cookie.getValue();
                }
            }
        } else {
            logger.debug("No cookies found in request");
        }
        return null;
    }
}

