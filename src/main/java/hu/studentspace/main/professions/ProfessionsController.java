package hu.studentspace.main.professions;

import hu.studentspace.main.common.IController;
import hu.studentspace.main.common.IControllerSimple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/professions")
public class ProfessionsController {
    private final ProfessionsService professionsService;

    @GetMapping
    public ResponseEntity<List<ProfessionsResponseDTO>> getAll() {
        return ResponseEntity.ok(professionsService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionsResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(professionsService.findById(UUID.fromString(id)));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody ProfessionsRequestDTO professions) {
        return ResponseEntity.ok(professionsService.save(professions));
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestBody ProfessionsRequestDTO professions) {
        return ResponseEntity.ok(professionsService.update(professions));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        professionsService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
