package hu.studentspace.main.forumMessages;

import hu.studentspace.main.errors.NotFoundException;
import hu.studentspace.main.forum.Forum;
import hu.studentspace.main.forum.ForumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForumMessagesServiceTest {

    @Mock
    private ForumMessagesRepository forumMessagesRepository;

    @Mock
    private ForumRepository forumRepository;

    @InjectMocks
    private ForumMessagesService forumMessagesService;

    @Test
    void getAllReturnsMessages() {
        when(forumMessagesRepository.findAll()).thenReturn(List.of(ForumMessages.builder().message("hi").build()));

        var result = forumMessagesService.getAll();

        assertEquals(1, result.size());
        assertEquals("hi", result.getFirst().getMessage());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        var id = UUID.randomUUID();
        when(forumMessagesRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> forumMessagesService.findById(id));
    }

    @Test
    void saveCreatesMessageWithForum() {
        var forumId = UUID.randomUUID();
        var messageId = UUID.randomUUID();
        var request = new ForumMessagesRequest(null, "hello", forumId.toString());
        var forum = Forum.builder().id(forumId).build();
        when(forumRepository.findById(forumId)).thenReturn(Optional.of(forum));
        when(forumMessagesRepository.save(any(ForumMessages.class))).thenAnswer(invocation -> {
            ForumMessages message = invocation.getArgument(0);
            message.setId(messageId);
            return message;
        });

        var result = forumMessagesService.save(request);

        assertEquals(messageId.toString(), result);
    }

    @Test
    void saveThrowsWhenForumMissing() {
        var forumId = UUID.randomUUID();
        var request = new ForumMessagesRequest(null, "hello", forumId.toString());
        when(forumRepository.findById(forumId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> forumMessagesService.save(request));
    }

    @Test
    void updateChangesMessage() {
        var id = UUID.randomUUID();
        var request = new ForumMessagesRequest(id.toString(), "new text", UUID.randomUUID().toString());
        var existing = ForumMessages.builder().id(id).message("old").build();
        when(forumMessagesRepository.findById(id)).thenReturn(Optional.of(existing));
        when(forumMessagesRepository.save(existing)).thenReturn(existing);

        var result = forumMessagesService.update(request);

        assertEquals(id.toString(), result);
        assertEquals("new text", existing.getMessage());
    }

    @Test
    void deleteDelegatesToRepository() {
        var id = UUID.randomUUID();

        forumMessagesService.delete(id);

        verify(forumMessagesRepository).deleteById(id);
    }
}
