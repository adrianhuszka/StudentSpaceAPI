package hu.studentspace.main.auth;

import hu.studentspace.main.users.Users;
import hu.studentspace.main.users.UsersDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.authenticate(
                loginRequest.username(),
                loginRequest.password());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody UsersDTO usersDTO) {
        TokenResponse tokenResponse = authService.register(usersDTO);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest refreshRequest) {
        TokenResponse tokenResponse = authService.refreshAccessToken(refreshRequest.refreshToken());
        return ResponseEntity.ok(tokenResponse);
    }
}
