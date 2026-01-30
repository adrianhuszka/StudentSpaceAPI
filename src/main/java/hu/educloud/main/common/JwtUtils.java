package hu.educloud.main.common;

import java.util.Base64;

public class JwtUtils {
    
    private JwtUtils() {
        // Utility class
    }

    /**
     * Extract subject (sub claim) from Authorization header
     * @param authHeader Authorization header value (Bearer token)
     * @return Subject as String (username or ID)
     */
    public static String extractSubject(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }
        
        String token = authHeader.substring(7);
        return extractSubjectFromToken(token);
    }

    /**
     * Extract subject (sub claim) from JWT token without validation
     */
    private static String extractSubjectFromToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }
        
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        
        int subIndex = payload.indexOf("\"sub\"");
        if (subIndex == -1) {
            throw new IllegalArgumentException("Token does not contain 'sub' claim");
        }
        
        int colonIndex = payload.indexOf(":", subIndex);
        int startQuote = payload.indexOf("\"", colonIndex);
        int endQuote = payload.indexOf("\"", startQuote + 1);
        
        return payload.substring(startQuote + 1, endQuote);
    }
}
