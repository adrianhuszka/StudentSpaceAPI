package hu.studentspace.main.exercises;

import hu.studentspace.main.errors.NotFoundException;
import hu.studentspace.main.module.Module;
import hu.studentspace.main.module.ModuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    void findAllReturnsExercises() {
        when(exerciseRepository.findAll()).thenReturn(List.of(Exercises.builder().question("Q").build()));

        var result = exerciseService.findAll();

        assertEquals(1, result.size());
        assertEquals("Q", result.getFirst().getQuestion());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        var id = UUID.randomUUID().toString();
        when(exerciseRepository.findById(UUID.fromString(id))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> exerciseService.findById(id));
    }

    @Test
    void findByModuleDelegatesToRepository() {
        var moduleId = UUID.randomUUID();
        when(exerciseRepository.findAllByModuleId(moduleId)).thenReturn(List.of());

        var result = exerciseService.findByModule(moduleId.toString());

        assertNotNull(result);
        verify(exerciseRepository).findAllByModuleId(moduleId);
    }

    @Test
    void saveBuildsExerciseAndPersists() {
        var moduleId = UUID.randomUUID();
        var request = new ExerciseRequest(moduleId.toString(), "What?", "42", "SHORT_ANSWER", UUID.randomUUID().toString());
        var module = Module.builder().id(moduleId).build();
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(exerciseRepository.save(any(Exercises.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = exerciseService.save(request);

        assertEquals(ExerciseType.SHORT_ANSWER, result.getType());
        assertEquals("What?", result.getQuestion());
        assertEquals("42", result.getAnswer());
        assertEquals(moduleId, result.getModule().getId());
    }

    @Test
    void updateThrowsWhenExerciseMissing() {
        var exerciseId = UUID.randomUUID();
        var request = new ExerciseRequest(exerciseId.toString(), "Q", "A", "CODING", UUID.randomUUID().toString());
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> exerciseService.update(request));
    }

    @Test
    void deleteRemovesExistingAndReturnsIt() {
        var id = UUID.randomUUID();
        var existing = Exercises.builder().id(id).question("Q").build();
        when(exerciseRepository.findById(id)).thenReturn(Optional.of(existing));

        var result = exerciseService.delete(id.toString());

        assertEquals(id, result.getId());
        verify(exerciseRepository).delete(existing);
    }
}
