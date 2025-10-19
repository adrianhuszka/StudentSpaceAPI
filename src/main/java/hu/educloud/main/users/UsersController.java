package hu.educloud.main.users;

import hu.educloud.main.common.IController;
import hu.educloud.main.config.SecurityUtils;
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
    @GetMapping("/:id")
    public ResponseEntity<Users> getById(String id) {
        return ResponseEntity.ok(usersService.findById(UUID.fromString(id)));
    }

    @Override
    @PostMapping
    public ResponseEntity<String> create(UsersDTO usersDTO) {
        return ResponseEntity.ok(usersService.save(usersDTO));
    }

    @Override
    @PutMapping
    public ResponseEntity<String> update(UsersDTO usersDTO) {
        return ResponseEntity.ok(usersService.update(usersDTO));
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> delete(String id) {
        usersService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Users> getCurrentUser(Authentication authentication) {
        // Option 1: Using Authentication parameter injected by Spring
        String username = authentication.getName();

        // Option 2: Using SecurityUtils
        // String username = SecurityUtils.getCurrentUsername();

        Users user = usersService.findByUsername(username);
        return ResponseEntity.ok(user);
    }
}
