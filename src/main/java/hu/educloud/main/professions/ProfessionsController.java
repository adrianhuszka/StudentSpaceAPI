package hu.educloud.main.professions;

import hu.educloud.main.common.IController;
import hu.educloud.main.common.IControllerSimple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/professions")
public class ProfessionsController implements IController<Professions, ProfessionsRequestDTO> {
    private final ProfessionsService professionsService;

    @Override
    @GetMapping
    public ResponseEntity<List<Professions>> getAll() {
        return ResponseEntity.ok(professionsService.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Professions> getById(@PathVariable String id) {
        return ResponseEntity.ok(professionsService.findById(UUID.fromString(id)));
    }

    @Override
    @PostMapping
    public ResponseEntity<String> create(@RequestBody ProfessionsRequestDTO professions) {
        return ResponseEntity.ok(professionsService.save(professions));
    }

    @Override
    @PutMapping
    public ResponseEntity<String> update(@RequestBody ProfessionsRequestDTO professions) {
        return ResponseEntity.ok(professionsService.update(professions));
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> delete(String id) {
        professionsService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
