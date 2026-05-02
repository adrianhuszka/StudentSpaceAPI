package hu.studentspace.main.quiz;

import java.util.Map;
import java.util.UUID;

public record QuizSubmitRequest(
    UUID quizId,
    UUID attemptId,
    Map<String, String> answers
) {}
