package hu.studentspace.main.module;

public record ModuleRequestDTO(
                String id,
                String title,
                String content,
                String moduleType,
                String subjectId,
                byte[] pdfFile,
                String pdfFileName) {
}
