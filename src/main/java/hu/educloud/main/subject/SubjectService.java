package hu.studentspace.main.subject;

import hu.studentspace.main.common.IService;
import hu.studentspace.main.common.IServiceSimple;
import hu.studentspace.main.errors.NotFoundException;
import hu.studentspace.main.forum.Forum;
import hu.studentspace.main.forum.ForumRepository;
import hu.studentspace.main.professions.ProfessionsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService implements IService<Subject, SubjectRequestDTO> {
    private final SubjectRepository subjectRepository;
    private final ProfessionsRepository professionsRepository;
    private final ForumRepository forumRepository;

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
        final var newSubject = Subject.builder()
                .name(subject.name())
                .description(subject.description())
                .build();

        final var createdSubject = subjectRepository.save(newSubject);
        Forum newForum = null;

        if (subject.createForum()) {
            newForum = forumRepository.save(
                    Forum.builder()
                            .subject(createdSubject)
                            .build());
        }

        createdSubject.setForum(newForum);

        return subjectRepository.save(createdSubject).getId().toString();
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

        // Update both sides
        if (profession.getSubjects() == null
                || profession.getSubjects().stream().noneMatch(s -> s.getId().equals(subjectId))) {
            profession.getSubjects().add(subject);
        }
        if (subject.getProfessions() == null
                || subject.getProfessions().stream().noneMatch(p -> p.getId().equals(professionId))) {
            subject.getProfessions().add(profession);
        }

        professionsRepository.save(profession);
        subjectRepository.save(subject);
        return subject.getId().toString();
    }

    @Transactional
    public String unlinkSubjectFromProfession(@NonNull UUID subjectId, @NonNull UUID professionId) {
        var subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundException(subjectId.toString()));

        var profession = professionsRepository.findById(professionId)
                .orElseThrow(() -> new NotFoundException(professionId.toString()));

        // Remove from both sides
        if (profession.getSubjects() != null) {
            profession.getSubjects().removeIf(s -> s.getId().equals(subjectId));
            professionsRepository.save(profession);
        }

        if (subject.getProfessions() != null) {
            subject.getProfessions().removeIf(p -> p.getId().equals(professionId));
            subjectRepository.save(subject);
        }

        return subject.getId().toString();
    }

    @Override
    public void delete(@NonNull UUID id) {
        var subject = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString()));

        // Remove this subject from all professions (clears the many-to-many join table)
        if (subject.getProfessions() != null) {
            for (var profession : subject.getProfessions()) {
                profession.getSubjects().removeIf(s -> s.getId().equals(id));
                professionsRepository.save(profession);
            }
        }

        subjectRepository.deleteById(id);
    }
}
