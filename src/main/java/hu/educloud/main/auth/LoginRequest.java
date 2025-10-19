package hu.educloud.main.auth;

public record LoginRequest(
        String username,
        String password
) {
}

