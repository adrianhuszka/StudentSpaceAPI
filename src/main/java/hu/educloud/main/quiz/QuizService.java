package hu.educloud.main.quiz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.educloud.main.errors.NotFoundException;
import hu.educloud.main.module.Module;
import hu.educloud.main.module.ModuleRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final ModuleRepository moduleRepository;
    private final ObjectMapper objectMapper;

    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }

    public List<Quiz> findAllActive() {
        return quizRepository.findAllByIsActiveTrue();
    }

    public Quiz findById(String id) {
        return quizRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Quiz not found"));
    }

    public List<Quiz> findByModule(String moduleId) {
        return quizRepository.findAllByModuleIdAndIsActiveTrue(UUID.fromString(moduleId));
    }

    public List<Quiz> findBySubject(String subjectId) {
        return quizRepository.findAllBySubjectIdAndIsActiveTrue(UUID.fromString(subjectId));
    }

    @Transactional
    public Quiz save(@NotNull QuizRequest request) {
        Quiz quiz = Quiz.builder()
                .title(request.title())
                .description(request.description())
                .timeLimit(request.timeLimit())
                .passingScore(request.passingScore() != null ? request.passingScore() : 60)
                .isActive(request.isActive() != null ? request.isActive() : true)
                .build();

        if (request.moduleId() != null) {
            Module module = moduleRepository.findById(request.moduleId())
                    .orElseThrow(() -> new NotFoundException("Module not found"));
            quiz.setModule(module);
        }

        if (request.subjectId() != null) {
            quiz.setSubjectId(request.subjectId());
        }

        if (request.questions() != null) {
            for (int i = 0; i < request.questions().size(); i++) {
                QuizRequest.QuestionRequest qr = request.questions().get(i);
                QuizQuestion question = QuizQuestion.builder()
                        .type(qr.type())
                        .question(qr.question())
                        .options(qr.options())
                        .correctAnswer(qr.correctAnswer())
                        .points(qr.points() != null ? qr.points() : 1)
                        .orderIndex(qr.orderIndex() != null ? qr.orderIndex() : i)
                        .build();
                quiz.addQuestion(question);
            }
        }

        return quizRepository.save(quiz);
    }

    @Transactional
    public Quiz update(@NotNull QuizRequest request) {
        if (request.id() == null) {
            throw new IllegalArgumentException("Quiz ID is required for update");
        }

        Quiz quiz = quizRepository.findById(request.id())
                .orElseThrow(() -> new NotFoundException("Quiz not found"));

        quiz.setTitle(request.title());
        quiz.setDescription(request.description());
        quiz.setTimeLimit(request.timeLimit());
        quiz.setPassingScore(request.passingScore() != null ? request.passingScore() : 60);
        quiz.setIsActive(request.isActive() != null ? request.isActive() : true);

        if (request.moduleId() != null) {
            Module module = moduleRepository.findById(request.moduleId())
                    .orElseThrow(() -> new NotFoundException("Module not found"));
            quiz.setModule(module);
        } else {
            quiz.setModule(null);
        }

        if (request.subjectId() != null) {
            quiz.setSubjectId(request.subjectId());
        } else {
            quiz.setSubjectId(null);
        }

        // Update questions
        quiz.getQuestions().clear();
        if (request.questions() != null) {
            for (int i = 0; i < request.questions().size(); i++) {
                QuizRequest.QuestionRequest qr = request.questions().get(i);
                QuizQuestion question = QuizQuestion.builder()
                        .id(qr.id())
                        .type(qr.type())
                        .question(qr.question())
                        .options(qr.options())
                        .correctAnswer(qr.correctAnswer())
                        .points(qr.points() != null ? qr.points() : 1)
                        .orderIndex(qr.orderIndex() != null ? qr.orderIndex() : i)
                        .build();
                quiz.addQuestion(question);
            }
        }

        return quizRepository.save(quiz);
    }

    @Transactional
    public void delete(String id) {
        Quiz quiz = quizRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Quiz not found"));
        quizRepository.delete(quiz);
    }

    // Quiz attempt methods

    @Transactional
    public QuizAttempt startQuiz(String quizId, String studentId) {
        Quiz quiz = findById(quizId);

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .studentId(studentId)
                .build();

        return quizAttemptRepository.save(attempt);
    }

    @Transactional
    public QuizAttempt submitQuiz(QuizSubmitRequest request, String studentId) {
        QuizAttempt attempt = quizAttemptRepository.findById(request.attemptId())
                .orElseThrow(() -> new NotFoundException("Quiz attempt not found"));

        if (!attempt.getStudentId().equals(studentId)) {
            throw new IllegalArgumentException("This attempt does not belong to you");
        }

        if (attempt.getCompletedAt() != null) {
            throw new IllegalArgumentException("This quiz has already been submitted");
        }

        Quiz quiz = attempt.getQuiz();

        // Calculate score
        int totalPoints = 0;
        int earnedPoints = 0;

        for (QuizQuestion question : quiz.getQuestions()) {
            totalPoints += question.getPoints();
            String studentAnswer = request.answers().get(question.getId().toString());
            if (studentAnswer != null && studentAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                earnedPoints += question.getPoints();
            }
        }

        int score = totalPoints > 0 ? (earnedPoints * 100) / totalPoints : 0;
        boolean passed = score >= quiz.getPassingScore();

        // Save answers as JSON
        try {
            attempt.setAnswers(objectMapper.writeValueAsString(request.answers()));
        } catch (JsonProcessingException e) {
            attempt.setAnswers("{}");
        }

        attempt.setCompletedAt(Timestamp.from(Instant.now()));
        attempt.setScore(score);
        attempt.setPointsEarned(earnedPoints);
        attempt.setTotalPoints(totalPoints);
        attempt.setPassed(passed);

        return quizAttemptRepository.save(attempt);
    }

    public List<QuizAttempt> getStudentAttempts(String studentId) {
        return quizAttemptRepository.findAllByStudentId(studentId);
    }

    public List<QuizAttempt> getQuizAttempts(String quizId) {
        return quizAttemptRepository.findAllByQuizId(UUID.fromString(quizId));
    }
}
