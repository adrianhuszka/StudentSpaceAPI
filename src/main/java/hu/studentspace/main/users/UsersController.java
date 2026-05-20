package hu.studentspace.main.users;

import hu.studentspace.main.common.IController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UsersController implements IController<Users, UsersDTO> {
    private final UsersService usersService;

    @Override
    @GetMapping
    public ResponseEntity<List<Users>> getAll() {
        return ResponseEntity.ok(usersService.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Users> getById(@PathVariable String id) {
        return ResponseEntity.ok(usersService.findById(UUID.fromString(id)));
    }

    @Override
    @PostMapping
    public ResponseEntity<String> create(@RequestBody UsersDTO usersDTO) {
        return ResponseEntity.ok(usersService.save(usersDTO));
    }

    @Override
    @PutMapping
    public ResponseEntity<String> update(@RequestBody UsersDTO usersDTO) {
        return ResponseEntity.ok(usersService.update(usersDTO));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        usersService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<String> updateUserRoles(@PathVariable String id, @RequestBody UpdateUserRolesRequest request) {
        String updatedUserId = usersService.updateUserRoles(UUID.fromString(id), request.roles());
        return ResponseEntity.ok(updatedUserId);
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Boolean> toggleUserStatus(@PathVariable String id) {
        boolean enabled = usersService.toggleUserStatus(UUID.fromString(id));
        return ResponseEntity.ok(enabled);
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        return ResponseEntity.ok(usersService.getUserStats());
    }

    @GetMapping("/me")
    public ResponseEntity<Users> getCurrentUser(Authentication authentication) {

        String username = authentication.getName();


        Users user = usersService.findByUsername(username);
        return ResponseEntity.ok(user);
    }
}
