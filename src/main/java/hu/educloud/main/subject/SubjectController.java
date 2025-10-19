package hu.educloud.main.subject;

import hu.educloud.main.common.IController;
import hu.educloud.main.common.IControllerSimple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subjects")
public class SubjectController implements IController<Subject, SubjectRequestDTO> {
    private final SubjectService subjectService;

    @Override
    @GetMapping
    public ResponseEntity<List<Subject>> getAll() {
        return ResponseEntity.ok(subjectService.getAll());
    }

    @Override
    @GetMapping("/:id")
    public ResponseEntity<Subject> getById(String id) {
        return ResponseEntity.ok(subjectService.findById(UUID.fromString(id)));
    }

    @GetMapping("/by-profession/{professionId}")
    public ResponseEntity<List<Subject>> getAllByProfessionId(@PathVariable String professionId) {
        return ResponseEntity.ok(subjectService.getAllByProfessionId(professionId));
    }

    @Override
    @PostMapping
    public ResponseEntity<String> create(@RequestBody SubjectRequestDTO subject) {
        return ResponseEntity.ok(subjectService.save(subject));
    }

    @Override
    @PutMapping
    public ResponseEntity<String> update(@RequestBody SubjectRequestDTO subject) {
        return ResponseEntity.ok(subjectService.update(subject));
    }

    @PutMapping("/link-subject-to-profession")
    public ResponseEntity<String> linkSubjectToProfession(@RequestParam String subjectId, @RequestParam String professionId) {
        return ResponseEntity.ok(subjectService.linkSubjectToProfession(UUID.fromString(subjectId), UUID.fromString(professionId)));
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> delete(String id) {
        subjectService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}

