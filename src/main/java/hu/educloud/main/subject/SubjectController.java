package hu.studentspace.main.subject;

import hu.studentspace.main.common.IController;
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
    @GetMapping("/{id}")
    public ResponseEntity<Subject> getById(@PathVariable String id) {
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
    public ResponseEntity<String> linkSubjectToProfession(@RequestParam String subjectId,
            @RequestParam String professionId) {
        return ResponseEntity
                .ok(subjectService.linkSubjectToProfession(UUID.fromString(subjectId), UUID.fromString(professionId)));
    }

    @PutMapping("/unlink-subject-from-profession")
    public ResponseEntity<String> unlinkSubjectFromProfession(@RequestParam String subjectId,
            @RequestParam String professionId) {
        return ResponseEntity.ok(
                subjectService.unlinkSubjectFromProfession(UUID.fromString(subjectId), UUID.fromString(professionId)));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        subjectService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
