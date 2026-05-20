package hu.studentspace.main.professions;

public record ProfessionStatsResponse(
        long totalProfessions,
        long totalSubjects,
        long totalModules,
        double averageSubjectsPerProfession) {
}
