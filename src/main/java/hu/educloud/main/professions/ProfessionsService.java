package hu.educloud.main.professions;

import hu.educloud.main.common.IService;
import hu.educloud.main.errors.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfessionsService {
    private final ProfessionsRepository professionsRepository;

    public List<ProfessionsResponseDTO> getAll() {
        var professions = professionsRepository.findAll();

        return professions.stream().map(profession ->
            new ProfessionsResponseDTO(
                profession.getId().toString(),
                profession.getName(),
                profession.getDescription(),
                profession.getImage() != null ? encodeBase64Image(profession.getImage()) : null,
                profession.getCreatedAt(),
                profession.getCreatedBy(),
                profession.getUpdatedAt(),
                profession.getUpdatedBy()
        )).toList();
    }

    public ProfessionsResponseDTO findById(@NonNull UUID id) {
        var profession = professionsRepository.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));

        return new ProfessionsResponseDTO(
                profession.getId().toString(),
                profession.getName(),
                profession.getDescription(),
                profession.getImage() != null ? encodeBase64Image(profession.getImage()) : null,
                profession.getCreatedAt(),
                profession.getCreatedBy(),
                profession.getUpdatedAt(),
                profession.getUpdatedBy()
        );
    }

    public String save(@NonNull ProfessionsRequestDTO professions) {
        return professionsRepository.save(
                Professions.builder()
                        .name(professions.name())
                        .description(professions.description())
                        .image(professions.image() != null ? decodeBase64Image(professions.image()) : null)
                        .build()
        ).getId().toString();
    }

    public String update(@NonNull ProfessionsRequestDTO professions) {
        var existing = professionsRepository.findById(UUID.fromString(professions.id()))
                .orElseThrow(() -> new NotFoundException(professions.id()));

        existing.setName(professions.name());
        existing.setDescription(professions.description());
        if (professions.image() != null) {
            existing.setImage(decodeBase64Image(professions.image()));
        }

        return professionsRepository.save(existing).getId().toString();
    }

    public void delete(@NonNull UUID id) {
        professionsRepository.deleteById(id);
    }

    private byte[] decodeBase64Image(String image) {
        if (image == null) return null;
        String cleaned = image.trim();
        int comma = cleaned.indexOf(',');
        if (comma != -1 && cleaned.substring(0, comma).contains("base64")) {
            cleaned = cleaned.substring(comma + 1);
        }

        cleaned = cleaned.replaceAll("\\s+", "");
        try {
            return Base64.getDecoder().decode(cleaned);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Base64 image data", e);
        }
    }

    private String encodeBase64Image(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) return null;

        String mimeType = detectImageMimeType(imageBytes);
        String base64Data = Base64.getEncoder().encodeToString(imageBytes);
        return "data:" + mimeType + ";base64," + base64Data;
    }

    @Contract(pure = true)
    private @NotNull String detectImageMimeType(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length < 4) {
            return "image/png"; // default
        }

        // Check PNG signature
        if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == 0x50 &&
            imageBytes[2] == 0x4E && imageBytes[3] == 0x47) {
            return "image/png";
        }

        // Check JPEG signature
        if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8) {
            return "image/jpeg";
        }

        // Check GIF signature
        if (imageBytes[0] == 0x47 && imageBytes[1] == 0x49 && imageBytes[2] == 0x46) {
            return "image/gif";
        }

        // Check WebP signature
        if (imageBytes.length >= 12 && imageBytes[0] == 0x52 && imageBytes[1] == 0x49 &&
            imageBytes[2] == 0x46 && imageBytes[3] == 0x46 &&
            imageBytes[8] == 0x57 && imageBytes[9] == 0x45 &&
            imageBytes[10] == 0x42 && imageBytes[11] == 0x50) {
            return "image/webp";
        }

        // Default to PNG if unknown
        return "image/png";
    }
}
