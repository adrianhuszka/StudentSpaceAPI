package hu.studentspace.main.forumMessages;

public record ForumMessagesRequest(
                String id,
                String message,
                String forumId) {
}
