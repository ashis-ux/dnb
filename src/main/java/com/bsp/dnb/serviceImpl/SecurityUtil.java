package com.bsp.dnb.serviceImpl;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

	public SecurityUtil() {
	}

	public static String getLoggedInUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			return "SYSTEM";
		}

		return authentication.getName();
	}

	public static boolean hasRole(String roleName) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getAuthorities() == null) {

			return false;
		}

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		return authorities.stream().anyMatch(authority -> authority.getAuthority().equalsIgnoreCase(roleName));
	}

	public static boolean isMastOrSu() {

		return hasRole("MAST") || hasRole("SU");
	}
}