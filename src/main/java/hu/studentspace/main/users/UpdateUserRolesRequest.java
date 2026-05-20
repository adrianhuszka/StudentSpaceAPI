package hu.studentspace.main.users;

import java.util.Set;

public record UpdateUserRolesRequest(Set<UserRole> roles) {
}
