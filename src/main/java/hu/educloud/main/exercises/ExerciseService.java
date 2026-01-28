package hu.studentspace.main.exercises;

import hu.studentspace.main.errors.NotFoundException;
import hu.studentspace.main.module.ModuleRepository;
import hu.studentspace.main.module.ModuleTypes;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ModuleRepository moduleRepository;

    public List<Exercises> findAll() {
        return exerciseRepository.findAll();
    }

    public Exercises findById(String id) {
        return exerciseRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Exercise not found"));
    }

    public List<Exercises> findByModule(String moduleId) {
        return exerciseRepository.findAllByModuleId(UUID.fromString(moduleId));
    }

    public Exercises save(@NotNull ExerciseRequest request) {
        var module = moduleRepository.findById(UUID.fromString(request.id())).orElseThrow(
                () -> new NotFoundException("Module not found"));

        Exercises exercise = Exercises.builder()
                .module(module)
                .type(ExerciseType.valueOf(request.type()))
                .question(request.question())
                .answer(request.answer())
                .build();

        return exerciseRepository.save(exercise);
    }

    public Exercises update(@NotNull ExerciseRequest request) {
        var existing = exerciseRepository.findById(UUID.fromString(request.id())).orElseThrow(
                () -> new NotFoundException("Exercise not found"));

        var module = moduleRepository.findById(UUID.fromString(request.id())).orElseThrow(
                () -> new NotFoundException("Module not found"));

        existing.setModule(module);
        existing.setType(ExerciseType.valueOf(request.type()));
        existing.setQuestion(request.question());
        existing.setAnswer(request.answer());

        return exerciseRepository.save(existing);
    }

    public Exercises delete(@NotNull String id) {
        var existing = exerciseRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new NotFoundException("Exercise not found"));
        exerciseRepository.delete(existing);
        return existing;
    }
}
