package hu.educloud.main.professions;

import hu.educloud.main.common.IService;
import hu.educloud.main.common.IServiceSimple;
import hu.educloud.main.errors.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfessionsService implements IService<Professions, ProfessionsRequestDTO> {
    private final ProfessionsRepository professionsRepository;

    @Override
    public List<Professions> getAll() {
        return professionsRepository.findAll();
    }

    @Override
    public Professions findById(@NonNull UUID id) {
        return professionsRepository.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));
    }

    @Override
    public String save(@NonNull ProfessionsRequestDTO professions) {
        return professionsRepository.save(
                Professions.builder()
                        .name(professions.name())
                        .description(professions.description())
                        .image(professions.image() != null ? Base64.getDecoder().decode(professions.image()) : null)
                        .build()
        ).getId().toString();
    }

    @Override
    public String update(@NonNull ProfessionsRequestDTO professions) {
        var existing = professionsRepository.findById(UUID.fromString(professions.id()))
                .orElseThrow(() -> new NotFoundException(professions.id()));

        existing.setName(professions.name());
        existing.setDescription(professions.description());
        if (professions.image() != null) {
            existing.setImage(Base64.getDecoder().decode(professions.image()));
        }

        return professionsRepository.save(existing).getId().toString();
    }

    @Override
    public void delete(@NonNull UUID id) {
        professionsRepository.deleteById(id);
    }
}
