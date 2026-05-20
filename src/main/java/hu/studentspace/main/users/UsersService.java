package hu.studentspace.main.users;

import hu.studentspace.main.common.IService;
import hu.studentspace.main.errors.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService implements IService<Users, UsersDTO> {
    private final UsersRepository usersRepository;

    @Override
    public List<Users> getAll() {
        return usersRepository.findAll();
    }

    @Override
    public Users findById(@NonNull UUID id) {
        return usersRepository.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));
    }

    public Users findByUsername(@NonNull String username) {
        return usersRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(username));
    }

    @Override
    public String save(@NonNull UsersDTO usersDTO) {
        var user = Users.builder()
                .username(usersDTO.username())
                .email(usersDTO.email())
                .password(usersDTO.password())
                .roles(usersDTO.roles() != null ? usersDTO.roles() : Set.of(UserRole.STUDENT))
                .enabled(true)
                .build();

        return usersRepository.save(user).getId().toString();
    }

    @Override
    public String update(@NonNull UsersDTO usersDTO) {
        var user = usersRepository.findById(UUID.fromString(usersDTO.id()))
                .orElseThrow(() -> new NotFoundException(usersDTO.id()));

        user.setUsername(usersDTO.username());
        user.setEmail(usersDTO.email());
        user.setPassword(usersDTO.password());
        user.setRoles(usersDTO.roles());

        return usersRepository.save(user).getId().toString();
    }

    @Override
    public void delete(@NonNull UUID id) {
        usersRepository.deleteById(id);
    }

    public String updateUserRoles(@NonNull UUID id, Set<UserRole> roles) {
        var user = usersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString()));

        Set<UserRole> updatedRoles = (roles == null || roles.isEmpty())
                ? Set.of(UserRole.STUDENT)
                : roles;

        user.setRoles(updatedRoles);
        return usersRepository.save(user).getId().toString();
    }

    public boolean toggleUserStatus(@NonNull UUID id) {
        var user = usersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString()));

        boolean currentEnabled = Boolean.TRUE.equals(user.getEnabled());
        user.setEnabled(!currentEnabled);
        usersRepository.save(user);

        return user.getEnabled();
    }

    public UserStatsResponse getUserStats() {
        List<Users> users = usersRepository.findAll();

        long totalUsers = users.size();
        long activeUsers = users.stream().filter(user -> Boolean.TRUE.equals(user.getEnabled())).count();
        long adminUsers = users.stream().filter(user -> user.getRoles() != null && user.getRoles().stream()
                .anyMatch(role -> role == UserRole.ADMIN || role == UserRole.SUPERADMIN)).count();
        long teacherUsers = users.stream().filter(user -> user.getRoles() != null && user.getRoles().contains(UserRole.TEACHER)).count();

        return new UserStatsResponse(totalUsers, activeUsers, adminUsers, teacherUsers);
    }
}
