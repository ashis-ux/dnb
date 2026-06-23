package com.bsp.dnb.config;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
 
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IamJwtFilter extends OncePerRequestFilter {

    @Value("${app.iam.refresh-url}")
    private String refreshUrl;

    @Value("${app.iam.login-url}")
    private String loginUrl;

    @Autowired
    private RSAPublicKey iamPublicKey;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
    	
    	log.info("URI : {}", request.getRequestURI());

        String token = extractCookie(request, "BSP_JWT");
        try {
            if (token == null) {
                if (refreshJwt(request, response)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                response.sendRedirect(loginUrl);
                return;
            }
            Claims claims = Jwts.parser()
                    .verifyWith(iamPublicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (shouldRefresh(claims)) {

                log.info(
                        "JWT is near expiry. Refreshing token.");
                
                boolean refreshed =
                        refreshJwt(request, response);

                if (refreshed) {
                    log.info(
                            "JWT cookie updated successfully");
                }

            }

            String username =
                    claims.get("username", String.class);

            List<String> roles =
                    claims.get("roles", List.class);
            log.info("Authorities: {}", roles);
            List<SimpleGrantedAuthority> authorities =
                    roles.stream()
                         .map(role ->
                             new SimpleGrantedAuthority("ROLE_" + role))
                         .toList();
            log.info("Authenticated user: {}", username);
            log.info("Authorities: {}", authorities);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities);
            SecurityContextHolder.getContext()
                                 .setAuthentication(auth);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            log.info("JWT expired. Trying refresh.");
            if (refreshJwt(request, response)) {
                response.sendRedirect(
                        request.getRequestURI());
                return;
            }
            response.sendRedirect(loginUrl);
        } catch (JwtException ex) {
            log.error("Invalid JWT", ex);
            response.sendRedirect(loginUrl);
        }
    }

    private boolean refreshJwt(
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            HttpHeaders headers = new HttpHeaders();
            String cookieHeader = "";

            if (request.getCookies() != null) {
                cookieHeader =
                        Arrays.stream(request.getCookies())
                              .map(c -> c.getName() + "=" + c.getValue())
                              .collect(Collectors.joining("; "));
            }
            headers.add("Cookie", cookieHeader);
            HttpEntity<Void> entity =
                    new HttpEntity<>(headers);
            ResponseEntity<String> refreshResponse =
                    restTemplate.exchange(
                            refreshUrl,
                            HttpMethod.POST,
                            entity,
                            String.class);
            if (refreshResponse.getStatusCode().is2xxSuccessful()) {
                List<String> cookies =
                        refreshResponse
                                .getHeaders()
                                .get("Set-Cookie");
                if (cookies != null) {
                    cookies.forEach(
                            c -> response.addHeader(
                                    "Set-Cookie",
                                    c));
                }
                log.info("JWT refreshed");
                return true;
            }
        } catch (Exception e) {
            log.error(
                    "Refresh token failed",
                    e);
        }
        return false;
    }

    private String extractCookie(
            HttpServletRequest request,
            String cookieName) {

        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(c ->
                        cookieName.equals(
                                c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
    
    private boolean shouldRefresh(Claims claims) {

        long remaining =
                claims.getExpiration().getTime()
                        - System.currentTimeMillis();

        long tenMinutes =
                10 * 60 * 1000;
        
        

        return remaining > 0
                && remaining <= tenMinutes;
    }
    
    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request)
            throws ServletException {

        String uri = request.getRequestURI();

        return uri.startsWith("/sso/")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/");
    }
}