package com.bsp.dnb.serviceImpl;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtil {

    public static String getUsername() {

        Authentication auth =
                SecurityContextHolder.getContext()
                                     .getAuthentication();

        return auth.getName();
    }

    public static List<String> getRoles() {

        Authentication auth =
                SecurityContextHolder.getContext()
                                     .getAuthentication();

        return auth.getAuthorities()
                   .stream()
                   .map(a -> a.getAuthority())
                   .collect(Collectors.toList());
    }
}
