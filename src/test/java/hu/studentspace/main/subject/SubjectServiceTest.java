package hu.studentspace.main.subject;

import hu.studentspace.main.errors.NotFoundException;
import hu.studentspace.main.forum.ForumRepository;
import hu.studentspace.main.professions.Professions;
import hu.studentspace.main.professions.ProfessionsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private ProfessionsRepository professionsRepository;

    @Mock
    private ForumRepository forumRepository;

    @InjectMocks
    private SubjectService subjectService;

    @Test
    void getAllReturnsSubjects() {
        when(subjectRepository.findAll()).thenReturn(List.of(Subject.builder().name("Math").build()));

        var result = subjectService.getAll();

        assertEquals(1, result.size());
        assertEquals("Math", result.getFirst().getName());
    }

    @Test
    void saveWithoutForumStoresSubject() {
        var id = UUID.randomUUID();
        var request = new SubjectRequestDTO(null, "Math", "Basics", false);
        var created = Subject.builder().id(id).name("Math").description("Basics").build();
        when(subjectRepository.save(any(Subject.class))).thenReturn(created);

        var result = subjectService.save(request);

        assertEquals(id.toString(), result);
        verify(forumRepository, never()).save(any());
        verify(subjectRepository, times(2)).save(any(Subject.class));
    }

    @Test
    void saveWithForumCreatesForum() {
        var id = UUID.randomUUID();
        var request = new SubjectRequestDTO(null, "Math", "Basics", true);
        var created = Subject.builder().id(id).name("Math").description("Basics").build();
        when(subjectRepository.save(any(Subject.class))).thenReturn(created);
        when(forumRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = subjectService.save(request);

        assertEquals(id.toString(), result);
        verify(forumRepository).save(any());
        verify(subjectRepository, times(2)).save(any(Subject.class));
    }

    @Test
    void linkSubjectToProfessionAddsBothSidesAndSaves() {
        var subjectId = UUID.randomUUID();
        var professionId = UUID.randomUUID();

        var subject = Subject.builder().id(subjectId).professions(new ArrayList<>()).build();
        var profession = Professions.builder().id(professionId).subjects(new ArrayList<>()).build();

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(professionsRepository.findById(professionId)).thenReturn(Optional.of(profession));

        var result = subjectService.linkSubjectToProfession(subjectId, professionId);

        assertEquals(subjectId.toString(), result);
        assertTrue(subject.getProfessions().stream().anyMatch(p -> p.getId().equals(professionId)));
        assertTrue(profession.getSubjects().stream().anyMatch(s -> s.getId().equals(subjectId)));
        verify(professionsRepository).save(profession);
        verify(subjectRepository).save(subject);
    }

    @Test
    void unlinkSubjectFromProfessionRemovesBothSidesAndSaves() {
        var subjectId = UUID.randomUUID();
        var professionId = UUID.randomUUID();

        var subject = Subject.builder().id(subjectId).professions(new ArrayList<>()).build();
        var profession = Professions.builder().id(professionId).subjects(new ArrayList<>()).build();
        subject.getProfessions().add(profession);
        profession.getSubjects().add(subject);

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(professionsRepository.findById(professionId)).thenReturn(Optional.of(profession));

        var result = subjectService.unlinkSubjectFromProfession(subjectId, professionId);

        assertEquals(subjectId.toString(), result);
        assertTrue(subject.getProfessions().isEmpty());
        assertTrue(profession.getSubjects().isEmpty());
        verify(professionsRepository).save(profession);
        verify(subjectRepository).save(subject);
    }

    @Test
    void deleteRemovesSubjectFromProfessionsThenDeletes() {
        var subjectId = UUID.randomUUID();
        var profession = Professions.builder().id(UUID.randomUUID()).subjects(new ArrayList<>()).build();
        var subject = Subject.builder().id(subjectId).professions(new ArrayList<>(List.of(profession))).build();
        profession.getSubjects().add(subject);

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        subjectService.delete(subjectId);

        assertTrue(profession.getSubjects().isEmpty());
        verify(professionsRepository).save(profession);
        verify(subjectRepository).deleteById(subjectId);
    }

    @Test
    void updateThrowsWhenMissing() {
        var id = UUID.randomUUID();
        var request = new SubjectRequestDTO(id.toString(), "Math", "Basics", false);
        when(subjectRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subjectService.update(request));
    }
}
