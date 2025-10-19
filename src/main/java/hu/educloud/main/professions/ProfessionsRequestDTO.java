package hu.educloud.main.professions;

public record ProfessionsRequestDTO(
        String id,
        String name,
        String description,
        String image
) {
}
