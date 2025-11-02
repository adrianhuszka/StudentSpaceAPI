package hu.educloud.main.module;

import hu.educloud.main.common.IController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @GetMapping("/{id}")
    public ResponseEntity<Module> getById(@PathVariable String id) {
        return ResponseEntity.ok(moduleService.findById(UUID.fromString(id)));
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody ModuleRequestDTO module) {
        return ResponseEntity.ok(moduleService.save(module));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createMultipart(
            @RequestParam("title") String title,
            @RequestParam("moduleType") String moduleType,
            @RequestParam("subjectId") String subjectId,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) throws IOException {

        byte[] pdfBytes = null;
        String pdfFileName = null;

        if (pdfFile != null && !pdfFile.isEmpty()) {
            pdfBytes = pdfFile.getBytes();
            pdfFileName = pdfFile.getOriginalFilename();
        }

        var moduleDTO = new ModuleRequestDTO(
                null,
                title,
                content,
                moduleType,
                subjectId,
                pdfBytes,
                pdfFileName
        );

        return ResponseEntity.ok(moduleService.save(moduleDTO));
    }

    @Override
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@RequestBody ModuleRequestDTO module) {
        return ResponseEntity.ok(moduleService.update(module));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateMultipart(
            @RequestParam("id") String id,
            @RequestParam("title") String title,
            @RequestParam("moduleType") String moduleType,
            @RequestParam("subjectId") String subjectId,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) throws IOException {

        byte[] pdfBytes = null;
        String pdfFileName = null;

        if (pdfFile != null && !pdfFile.isEmpty()) {
            pdfBytes = pdfFile.getBytes();
            pdfFileName = pdfFile.getOriginalFilename();
        }

        var moduleDTO = new ModuleRequestDTO(
                id,
                title,
                content,
                moduleType,
                subjectId,
                pdfBytes,
                pdfFileName
        );

        return ResponseEntity.ok(moduleService.update(moduleDTO));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String id) {
        var module = moduleService.findById(UUID.fromString(id));

        if (module.getModuleType() != ModuleTypes.PDF || module.getPdfFile() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + module.getPdfFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(module.getPdfFile());
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        moduleService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
