package hu.educloud.main.module;

import hu.educloud.main.common.IService;
import hu.educloud.main.common.IServiceSimple;
import hu.educloud.main.errors.NotFoundException;
import hu.educloud.main.subject.SubjectRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModuleService implements IService<Module, ModuleRequestDTO> {
    private final ModuleRepository moduleRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public List<Module> getAll() {
        return moduleRepository.findAll();
    }

    @Override
    public Module findById(@NonNull UUID id) {
        return moduleRepository.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));
    }

    @Override
    public String save(@NonNull ModuleRequestDTO module) {
        var subject = subjectRepository.findById(UUID.fromString(module.subjectId()))
                .orElseThrow(() -> new NotFoundException(module.subjectId()));

        var moduleBuilder = Module.builder()
                .title(module.title())
                .moduleType(ModuleTypes.valueOf(module.moduleType()))
                .subject(subject);

        // Handle PDF vs MD content
        if (ModuleTypes.valueOf(module.moduleType()) == ModuleTypes.PDF) {
            moduleBuilder.pdfFile(module.pdfFile())
                    .pdfFileName(module.pdfFileName());
        } else {
            moduleBuilder.content(module.content());
        }

        var newModule = moduleBuilder.build();
        return moduleRepository.save(newModule).getId().toString();
    }

    @Override
    public String update(@NonNull ModuleRequestDTO module) {
        var existing = moduleRepository.findById(UUID.fromString(module.id()))
                .orElseThrow(() -> new NotFoundException(module.id()));

        var subject = subjectRepository.findById(UUID.fromString(module.subjectId()))
                .orElseThrow(() -> new NotFoundException(module.subjectId()));

        existing.setTitle(module.title());
        existing.setModuleType(ModuleTypes.valueOf(module.moduleType()));
        existing.setSubject(subject);

        // Handle PDF vs MD content
        if (ModuleTypes.valueOf(module.moduleType()) == ModuleTypes.PDF) {
            existing.setPdfFile(module.pdfFile());
            existing.setPdfFileName(module.pdfFileName());
            existing.setContent(null); // Clear content when switching to PDF
        } else {
            existing.setContent(module.content());
            existing.setPdfFile(null); // Clear PDF when switching to MD
            existing.setPdfFileName(null);
        }

        return moduleRepository.save(existing).getId().toString();
    }

    @Override
    public void delete(@NonNull UUID id) {
        moduleRepository.deleteById(id);
    }
}
