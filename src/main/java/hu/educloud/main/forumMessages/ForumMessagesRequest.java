package hu.educloud.main.forumMessages;

public record ForumMessagesRequest(
        String id,
        String message,
        String forumId
) {
}
