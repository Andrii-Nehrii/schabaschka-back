package schabaschka.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static JwtTokenService.JwtUserData getCurrentUserDataOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtTokenService.JwtUserData) {
            return (JwtTokenService.JwtUserData) principal;
        }

        return null;

    }

    public static JwtTokenService.JwtUserData getCurrentUserData() {
        JwtTokenService.JwtUserData userData = getCurrentUserDataOrNull();
        if (userData == null) {
            throw new AccessDeniedException("Unauthorized");
        }
        return userData;
    }

    public static Long getCurrentUserId() {
        JwtTokenService.JwtUserData userData = getCurrentUserData();
        if (userData.getUserId() == null) {
            throw new AccessDeniedException("Unauthorized");
        }
        return userData.getUserId();
    }

    public static String getCurrentUserEmail() {
        JwtTokenService.JwtUserData userData = getCurrentUserData();
        return userData.getEmail();
    }

    public static String getCurrentUserRole() {
        JwtTokenService.JwtUserData userData = getCurrentUserData();
        return userData.getRole();
    }

    public static void requireRole(String requiredRole) {
        if (requiredRole == null || requiredRole.isBlank()) {
            throw new IllegalArgumentException("requiredRole must not be blank");
        }

        String currentRole = getCurrentUserRole();
        if (currentRole == null) {
            throw new AccessDeniedException("Forbidden");
        }

        if (!currentRole.equalsIgnoreCase(requiredRole.trim())) {
            throw new AccessDeniedException("Forbidden");
        }
    }
}
