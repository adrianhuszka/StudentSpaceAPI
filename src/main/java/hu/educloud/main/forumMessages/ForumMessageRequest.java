package hu.educloud.main.forumMessages;

public record ForumMessageRequest(
        String message,
        String forumId
) {
}
