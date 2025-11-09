package hu.educloud.main.exercises;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<?> getAllExercises() {
        return ResponseEntity.ok(exerciseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExerciseById(@PathVariable String id) {
        return ResponseEntity.ok(exerciseService.findById(id));
    }

    @GetMapping("/by-module/{moduleId}")
    public ResponseEntity<?> getExercisesByModule(@PathVariable String moduleId) {
        return ResponseEntity.ok(exerciseService.findByModule(moduleId));
    }

    @PostMapping
    public ResponseEntity<?> createExercise(@RequestBody ExerciseRequest request) {
        return ResponseEntity.ok(exerciseService.save(request));
    }

    @PutMapping
    public ResponseEntity<?> updateExercise(@RequestBody ExerciseRequest request) {
        return ResponseEntity.ok(exerciseService.update(request));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteExercise(@RequestParam String id) {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
