package hu.studentspace.main.professions;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfessionsResponseDTO(
                String id,
                String name,
                String description,
                String image,
                LocalDateTime createdAt,
                String createdBy,
                LocalDateTime updatedAt,
                String updatedBy) {
}
