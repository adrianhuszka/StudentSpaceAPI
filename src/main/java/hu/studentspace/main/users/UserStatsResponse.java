package hu.studentspace.main.users;

public record UserStatsResponse(
        long totalUsers,
        long activeUsers,
        long adminUsers,
        long teacherUsers) {
}
