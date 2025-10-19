package hu.educloud.main.subject;

import hu.educloud.main.common.IService;
import hu.educloud.main.common.IServiceSimple;
import hu.educloud.main.errors.NotFoundException;
import hu.educloud.main.professions.ProfessionsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService implements IService<Subject, SubjectRequestDTO> {
    private final SubjectRepository subjectRepository;
    private final ProfessionsRepository professionsRepository;

    @Override
    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    public List<Subject> getAllByProfessionId(@NonNull String professionId) {
        return subjectRepository.getAllByProfessionId(UUID.fromString(professionId));
    }

    @Override
    public Subject findById(@NonNull UUID id) {
        return subjectRepository.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));
    }

    @Override
    public String save(@NonNull SubjectRequestDTO subject) {
        var newSubject = Subject.builder()
                .name(subject.name())
                .description(subject.description())
                .build();

        return subjectRepository.save(newSubject).getId().toString();
    }

    @Override
    public String update(@NonNull SubjectRequestDTO subject) {
        var existing = subjectRepository.findById(UUID.fromString(subject.id()))
                .orElseThrow(() -> new NotFoundException(subject.id()));

        existing.setName(subject.name());
        existing.setDescription(subject.description());

        return subjectRepository.save(existing).getId().toString();
    }

    public String linkSubjectToProfession(@NonNull UUID subjectId, @NonNull UUID professionId) {
        var subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundException(subjectId.toString()));

        var profession = professionsRepository.findById(professionId)
                .orElseThrow(() -> new NotFoundException(professionId.toString()));

        profession.getSubjects().add(subject);

        professionsRepository.save(profession);
        return subject.getId().toString();
    }

    @Override
    public void delete(@NonNull UUID id) {
        subjectRepository.deleteById(id);
    }
}
