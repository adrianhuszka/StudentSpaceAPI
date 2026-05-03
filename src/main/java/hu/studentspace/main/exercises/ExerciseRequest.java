package hu.studentspace.main.exercises;

public record ExerciseRequest(
                String id,
                String question,
                String answer,
                String type,
                String moduleId) {
}
