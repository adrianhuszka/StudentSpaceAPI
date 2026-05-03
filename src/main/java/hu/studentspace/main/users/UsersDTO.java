package hu.studentspace.main.users;

import java.util.Set;

public record UsersDTO(
                String id,
                String username,
                String email,
                String password,
                Set<UserRole> roles) {
}
