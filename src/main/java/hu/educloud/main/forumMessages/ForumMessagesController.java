package hu.educloud.main.forumMessages;

import hu.educloud.main.common.IControllerSimple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/forum-messages")
public class ForumMessagesController implements IControllerSimple<ForumMessages> {
    private final ForumMessagesService forumMessagesService;

    @Override
    @GetMapping
    public ResponseEntity<List<ForumMessages>> getAll() {
        return ResponseEntity.ok(forumMessagesService.getAll());
    }

    @Override
    @GetMapping("/:id")
    public ResponseEntity<ForumMessages> getById(String id) {
        return ResponseEntity.ok(forumMessagesService.findById(UUID.fromString(id)));
    }

    @Override
    @PostMapping
    public ResponseEntity<String> create(ForumMessages forumMessages) {
        return ResponseEntity.ok(forumMessagesService.save(forumMessages));
    }

    @Override
    @PutMapping
    public ResponseEntity<String> update(ForumMessages forumMessages) {
        return ResponseEntity.ok(forumMessagesService.update(forumMessages));
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> delete(String id) {
        forumMessagesService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}

