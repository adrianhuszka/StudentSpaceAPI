package hu.educloud.main.module;

import hu.educloud.main.common.IController;
import hu.educloud.main.common.IControllerSimple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/modules")
public class ModuleController implements IController<Module, ModuleRequestDTO> {
    private final ModuleService moduleService;

    @Override
    @GetMapping
    public ResponseEntity<List<Module>> getAll() {
        return ResponseEntity.ok(moduleService.getAll());
    }

    @Override
    @GetMapping("/:id")
    public ResponseEntity<Module> getById(String id) {
        return ResponseEntity.ok(moduleService.findById(UUID.fromString(id)));
    }

    @Override
    @PostMapping
    public ResponseEntity<String> create(@RequestBody ModuleRequestDTO module) {
        return ResponseEntity.ok(moduleService.save(module));
    }

    @Override
    @PutMapping
    public ResponseEntity<String> update(@RequestBody ModuleRequestDTO module) {
        return ResponseEntity.ok(moduleService.update(module));
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> delete(String id) {
        moduleService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}

