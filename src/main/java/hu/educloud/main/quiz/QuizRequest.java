package hu.educloud.main.quiz;

import hu.educloud.main.exercises.ExerciseType;

import java.util.List;
import java.util.UUID;

public record QuizRequest(
    UUID id,
    String title,
    String description,
    UUID moduleId,
    UUID subjectId,
    Integer timeLimit,
    Integer passingScore,
    Boolean isActive,
    List<QuestionRequest> questions
) {
    public record QuestionRequest(
        UUID id,
        ExerciseType type,
        String question,
        String options,
        String correctAnswer,
        Integer points,
        Integer orderIndex
    ) {}
}
