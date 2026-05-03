package hu.studentspace.main.auth;

import hu.studentspace.main.errors.BadCredentialsException;
import hu.studentspace.main.users.Users;
import hu.studentspace.main.users.UsersDTO;
import hu.studentspace.main.users.UsersRepository;
import hu.studentspace.main.users.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;

    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.mail.from:no-reply@studentspace.local}")
    private String fromEmail;

    @Value("${app.reset-password.length:12}")
    private int generatedPasswordLength;

    public TokenResponse authenticate(String username, String password) {
        var user = usersService.findByUsername(username);

        if (passwordEncoder.matches(password, user.getPassword())) {
            return generateTokenResponse(user);
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }

    public TokenResponse register(@NotNull UsersDTO usersDTO) {
        var passwordHashed = passwordEncoder.encode(usersDTO.password());

        usersDTO = new UsersDTO(
                usersDTO.id(),
                usersDTO.username(),
                usersDTO.email(),
                passwordHashed,
                usersDTO.roles());

        String userId = usersService.save(usersDTO);
        var user = usersService.findByUsername(usersDTO.username());

        return generateTokenResponse(user);
    }

    private @NotNull TokenResponse generateTokenResponse(@NotNull Users user) {
        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getId().toString(),
                user.getRoles());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername(), user.getId().toString(),
                user.getRoles());

        return new TokenResponse(accessToken, refreshToken, accessTokenExpiration);
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        if (jwtService.isTokenValid(refreshToken, username) &&
                "refresh".equals(jwtService.extractTokenType(refreshToken))) {

            var user = usersService.findByUsername(username);
            String newAccessToken = jwtService.generateAccessToken(user.getUsername(), user.getId().toString(),
                    user.getRoles());
            return new TokenResponse(newAccessToken, refreshToken, accessTokenExpiration);
        }

        throw new BadCredentialsException("Invalid refresh token");
    }

    public void forgotPassword(String email) {
        if (email == null || email.isBlank()) {
            log.warn("Forgot-password called with empty email");
            return;
        }

        String normalizedEmail = email.trim();
        Optional<Users> optionalUser = usersRepository.findByEmailIgnoreCase(normalizedEmail);

        if (optionalUser.isEmpty()) {
            log.info("Forgot-password: no user found for email={}", normalizedEmail);
            return;
        }

        Users user = optionalUser.get();
        String newPlainPassword = generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(newPlainPassword);

        try {
            sendPasswordResetEmail(user.getEmail(), user.getUsername(), newPlainPassword);
            user.setPassword(encodedPassword);
            usersRepository.save(user);
            log.info("Forgot-password email sent successfully for userId={} email={}", user.getId(), user.getEmail());
        } catch (MailException ex) {
            log.error("Failed to send forgot-password email to {}", user.getEmail(), ex);
            throw ex;
        }
    }

    private @NotNull String generateRandomPassword() {
        StringBuilder builder = new StringBuilder(generatedPasswordLength);

        for (int i = 0; i < generatedPasswordLength; i++) {
            int index = SECURE_RANDOM.nextInt(PASSWORD_CHARS.length());
            builder.append(PASSWORD_CHARS.charAt(index));
        }

        return builder.toString();
    }

    private void sendPasswordResetEmail(String toEmail, String username, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("StudentSpace - Új jelszó");
        message.setText("Kedves, " + username + ",\n\nAz új ideiglenes jelszavad: " + newPassword
                + "\n\nBejelentkezés után kérlek változtasd meg a jelszavad.");
        mailSender.send(message);
    }
}
