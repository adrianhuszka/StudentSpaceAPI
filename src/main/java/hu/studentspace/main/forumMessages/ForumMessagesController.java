package hu.studentspace.main.forumMessages;

import hu.studentspace.main.common.IControllerSimple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/forum-messages")
public class ForumMessagesController {
    private final ForumMessagesService forumMessagesService;

    @GetMapping
    public ResponseEntity<List<ForumMessages>> getAll() {
        return ResponseEntity.ok(forumMessagesService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumMessages> getById(@PathVariable String id) {
        return ResponseEntity.ok(forumMessagesService.findById(UUID.fromString(id)));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody ForumMessagesRequest forumMessages) {
        return ResponseEntity.ok(forumMessagesService.save(forumMessages));
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestBody ForumMessagesRequest forumMessages) {
        return ResponseEntity.ok(forumMessagesService.update(forumMessages));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        forumMessagesService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
