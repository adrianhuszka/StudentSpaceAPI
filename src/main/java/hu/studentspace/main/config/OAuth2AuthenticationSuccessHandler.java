package hu.studentspace.main.config;

import hu.studentspace.main.auth.JwtService;
import hu.studentspace.main.users.UserRole;
import hu.studentspace.main.users.Users;
import hu.studentspace.main.users.UsersRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final ObjectProvider<PasswordEncoder> passwordEncoderProvider;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${app.frontend-url:http://localhost:4200/}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof OAuth2User oauth2User)) {
            response.sendRedirect(frontendUrl);
            return;
        }

        Users user = findOrCreateUser(oauth2User);

        String accessToken = jwtService.generateAccessToken(
                user.getUsername(),
                user.getId().toString(),
                user.getRoles());

        String refreshToken = jwtService.generateRefreshToken(
                user.getUsername(),
                user.getId().toString(),
                user.getRoles());

        addCookie(response, "auth_token", accessToken, accessTokenExpiration);
        addCookie(response, "auth_refresh_token", refreshToken, refreshTokenExpiration);
        addCookie(response, "auth_user", buildAuthUserCookieValue(user), refreshTokenExpiration);

        response.sendRedirect(frontendUrl);
    }

    private Users findOrCreateUser(OAuth2User oAuth2User) {
        String email = normalizeToNull(readAttribute(oAuth2User, "email"));
        String username = normalizeToNull(readAttribute(oAuth2User, "preferred_username"));

        if (username == null && email != null && email.contains("@")) {
            username = email.substring(0, email.indexOf('@'));
        }

        if (username == null) {
            username = normalizeToNull(readAttribute(oAuth2User, "name"));
        }

        if (username == null) {
            throw new IllegalStateException("OAuth2 login did not provide username or email");
        }

        if (email == null) {
            email = username + "@keycloak.local";
        }

        Optional<Users> byEmail = usersRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            return byEmail.get();
        }

        Optional<Users> byUsername = usersRepository.findByUsername(username);
        if (byUsername.isPresent()) {
            return byUsername.get();
        }

        Users user = Users.builder()
                .username(username)
                .email(email)
            .password(passwordEncoderProvider.getObject().encode(UUID.randomUUID().toString()))
                .roles(Set.of(UserRole.STUDENT))
                .build();

        return usersRepository.save(user);
    }

    private String readAttribute(OAuth2User oAuth2User, String key) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Object value = attributes.get(key);
        return value != null ? value.toString() : null;
    }

    private String normalizeToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String buildAuthUserCookieValue(Users user) {
        String primaryRole = user.getRoles() != null && !user.getRoles().isEmpty()
                ? user.getRoles().iterator().next().name()
                : UserRole.STUDENT.name();

        return user.getId() + "|" + user.getUsername() + "|" + user.getEmail() + "|" + primaryRole;
    }

    private void addCookie(HttpServletResponse response, String name, String value, long expirationMillis) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(false)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofMillis(expirationMillis))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
