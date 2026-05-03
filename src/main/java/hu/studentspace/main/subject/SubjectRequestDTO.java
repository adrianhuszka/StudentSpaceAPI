package hu.studentspace.main.subject;

public record SubjectRequestDTO(
                String id,
                String name,
                String description,
                Boolean createForum) {
}
