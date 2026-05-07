package hu.studentspace.main.module;

import hu.studentspace.main.errors.NotFoundException;
import hu.studentspace.main.subject.Subject;
import hu.studentspace.main.subject.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private ModuleService moduleService;

    @Test
    void getAllReturnsModules() {
        var module = Module.builder().id(UUID.randomUUID()).title("Intro").build();
        when(moduleRepository.findAll()).thenReturn(List.of(module));

        var result = moduleService.getAll();

        assertEquals(1, result.size());
        assertEquals("Intro", result.getFirst().getTitle());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        var id = UUID.randomUUID();
        when(moduleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> moduleService.findById(id));
    }

    @Test
    void savePdfModuleSetsPdfFields() {
        var subjectId = UUID.randomUUID();
        var savedId = UUID.randomUUID();
        var request = new ModuleRequestDTO(null, "PDF Mod", "ignored", "PDF", subjectId.toString(), new byte[]{1, 2}, "f.pdf");
        var subject = Subject.builder().id(subjectId).build();
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(moduleRepository.save(any(Module.class))).thenAnswer(inv -> {
            Module m = inv.getArgument(0);
            m.setId(savedId);
            return m;
        });

        var result = moduleService.save(request);

        assertEquals(savedId.toString(), result);
        ArgumentCaptor<Module> captor = ArgumentCaptor.forClass(Module.class);
        verify(moduleRepository).save(captor.capture());
        assertEquals(ModuleTypes.PDF, captor.getValue().getModuleType());
        assertArrayEquals(new byte[]{1, 2}, captor.getValue().getPdfFile());
        assertEquals("f.pdf", captor.getValue().getPdfFileName());
    }

    @Test
    void saveMdModuleSetsContent() {
        var subjectId = UUID.randomUUID();
        var request = new ModuleRequestDTO(null, "MD Mod", "# content", "MD", subjectId.toString(), new byte[]{5}, "x.pdf");
        var subject = Subject.builder().id(subjectId).build();
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(moduleRepository.save(any(Module.class))).thenAnswer(inv -> {
            Module m = inv.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        moduleService.save(request);

        ArgumentCaptor<Module> captor = ArgumentCaptor.forClass(Module.class);
        verify(moduleRepository).save(captor.capture());
        assertEquals(ModuleTypes.MD, captor.getValue().getModuleType());
        assertEquals("# content", captor.getValue().getContent());
    }

    @Test
    void updatePdfClearsContent() {
        var moduleId = UUID.randomUUID();
        var subjectId = UUID.randomUUID();
        var existing = Module.builder().id(moduleId).content("old").build();
        var subject = Subject.builder().id(subjectId).build();
        var request = new ModuleRequestDTO(moduleId.toString(), "Updated", "ignored", "PDF", subjectId.toString(), new byte[]{9}, "u.pdf");
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(existing));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(moduleRepository.save(existing)).thenReturn(existing);

        moduleService.update(request);

        assertEquals(ModuleTypes.PDF, existing.getModuleType());
        assertNull(existing.getContent());
        assertArrayEquals(new byte[]{9}, existing.getPdfFile());
        assertEquals("u.pdf", existing.getPdfFileName());
    }

    @Test
    void updateMdClearsPdfFields() {
        var moduleId = UUID.randomUUID();
        var subjectId = UUID.randomUUID();
        var existing = Module.builder().id(moduleId).pdfFile(new byte[]{1}).pdfFileName("old.pdf").build();
        var subject = Subject.builder().id(subjectId).build();
        var request = new ModuleRequestDTO(moduleId.toString(), "Updated", "new body", "MD", subjectId.toString(), null, null);
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(existing));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(moduleRepository.save(existing)).thenReturn(existing);

        moduleService.update(request);

        assertEquals(ModuleTypes.MD, existing.getModuleType());
        assertEquals("new body", existing.getContent());
        assertNull(existing.getPdfFile());
        assertNull(existing.getPdfFileName());
    }
}
