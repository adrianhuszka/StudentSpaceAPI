package hu.studentspace.main.module;

import hu.studentspace.main.common.IController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    // JSON create (implements interface)
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody ModuleRequestDTO module) {
        return ResponseEntity.ok(moduleService.save(module));
    }

    // Multipart create for file uploads
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createMultipart(MultipartHttpServletRequest mreq) throws IOException {
        var dto = toDtoFromMultipart(mreq);
        return ResponseEntity.ok(moduleService.save(dto));
    }

    // JSON update (implements interface)
    @Override
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@RequestBody ModuleRequestDTO module) {
        return ResponseEntity.ok(moduleService.update(module));
    }

    // Multipart update
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateMultipart(MultipartHttpServletRequest mreq) throws IOException {
        var dto = toDtoFromMultipart(mreq);
        return ResponseEntity.ok(moduleService.update(dto));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String id) {
        var module = moduleService.findById(UUID.fromString(id));

        if (module.getModuleType() != ModuleTypes.PDF || module.getPdfFile() == null) {
            return ResponseEntity.notFound().build();
        }

        var fileName = module.getPdfFileName();
        if (!StringUtils.hasText(fileName))
            fileName = "file.pdf";

        // Use RFC5987 encoding for UTF-8 filenames to avoid Tomcat charset issues
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        String contentDisposition = "attachment; filename*=UTF-8''" + encoded;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_PDF)
                .body(module.getPdfFile());
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        moduleService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    private ModuleRequestDTO toDtoFromMultipart(MultipartHttpServletRequest mreq) throws IOException {
        String id = mreq.getParameter("id");
        String title = mreq.getParameter("title");
        String moduleType = mreq.getParameter("moduleType");
        String subjectId = mreq.getParameter("subjectId");
        String content = mreq.getParameter("content");

        MultipartFile pdfFile = mreq.getFile("pdfFile");
        byte[] pdfBytes = null;
        String pdfFileName = null;
        if (pdfFile != null && !pdfFile.isEmpty()) {
            pdfBytes = pdfFile.getBytes();
            pdfFileName = pdfFile.getOriginalFilename();
        }

        return new ModuleRequestDTO(
                id,
                title,
                content,
                moduleType,
                subjectId,
                pdfBytes,
                pdfFileName);
    }
}
