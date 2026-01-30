package hu.educloud.main.quiz;

import hu.educloud.main.common.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quizzes")
public class QuizController {
    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<?> getAllQuizzes() {
        return ResponseEntity.ok(quizService.findAllActive());
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllQuizzesAdmin() {
        return ResponseEntity.ok(quizService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable String id) {
        return ResponseEntity.ok(quizService.findById(id));
    }

    @GetMapping("/by-module/{moduleId}")
    public ResponseEntity<?> getQuizzesByModule(@PathVariable String moduleId) {
        return ResponseEntity.ok(quizService.findByModule(moduleId));
    }

    @GetMapping("/by-subject/{subjectId}")
    public ResponseEntity<?> getQuizzesBySubject(@PathVariable String subjectId) {
        return ResponseEntity.ok(quizService.findBySubject(subjectId));
    }

    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody QuizRequest request) {
        return ResponseEntity.ok(quizService.save(request));
    }

    @PutMapping
    public ResponseEntity<?> updateQuiz(@RequestBody QuizRequest request) {
        return ResponseEntity.ok(quizService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable String id) {
        quizService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Quiz attempt endpoints

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startQuiz(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String studentId = JwtUtils.extractSubject(authHeader);
        return ResponseEntity.ok(quizService.startQuiz(id, studentId));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitQuiz(
            @PathVariable String id,
            @RequestBody QuizSubmitRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String studentId = JwtUtils.extractSubject(authHeader);
        return ResponseEntity.ok(quizService.submitQuiz(request, studentId));
    }

    @GetMapping("/my-attempts")
    public ResponseEntity<?> getMyAttempts(@RequestHeader("Authorization") String authHeader) {
        String studentId = JwtUtils.extractSubject(authHeader);
        return ResponseEntity.ok(quizService.getStudentAttempts(studentId));
    }

    @GetMapping("/{id}/attempts")
    public ResponseEntity<?> getQuizAttempts(@PathVariable String id) {
        return ResponseEntity.ok(quizService.getQuizAttempts(id));
    }
}
