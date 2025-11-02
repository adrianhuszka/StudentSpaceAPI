package hu.educloud.main.auth;

import hu.educloud.main.errors.BadCredentialsException;
import hu.educloud.main.users.Users;
import hu.educloud.main.users.UsersDTO;
import hu.educloud.main.users.UsersRepository;
import hu.educloud.main.users.UsersService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

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
}