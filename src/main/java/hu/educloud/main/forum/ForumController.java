package hu.educloud.main.forum;

import hu.educloud.main.common.IControllerSimple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/forum")
public class ForumController implements IControllerSimple<Forum> {
    private final ForumService forumService;

    @Override
    @GetMapping
    public ResponseEntity<List<Forum>> getAll() {
        return ResponseEntity.ok(forumService.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Forum> getById(@PathVariable String id) {
        return ResponseEntity.ok(forumService.findById(UUID.fromString(id)));
    }

    @Override
    @PostMapping
    public ResponseEntity<String> create(@RequestBody Forum forum) {
        return ResponseEntity.ok(forumService.save(forum));
    }

    @Override
    @PutMapping
    public ResponseEntity<String> update(@RequestBody Forum forum) {
        return ResponseEntity.ok(forumService.update(forum));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        forumService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}

