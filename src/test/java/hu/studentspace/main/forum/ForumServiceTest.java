package hu.studentspace.main.forum;

import hu.studentspace.main.errors.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForumServiceTest {

    @Mock
    private ForumRepository forumRepository;

    @InjectMocks
    private ForumService forumService;

    @Test
    void getAllReturnsForums() {
        when(forumRepository.findAll()).thenReturn(List.of(Forum.builder().id(UUID.randomUUID()).build()));

        var result = forumService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        var id = UUID.randomUUID();
        when(forumRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> forumService.findById(id));
    }

    @Test
    void saveReturnsSavedId() {
        var id = UUID.randomUUID();
        var forum = Forum.builder().build();
        when(forumRepository.save(forum)).thenReturn(Forum.builder().id(id).build());

        var result = forumService.save(forum);

        assertEquals(id.toString(), result);
    }

    @Test
    void updateChangesSubjectAndMessages() {
        var id = UUID.randomUUID();
        var existing = Forum.builder().id(id).build();
        var incoming = Forum.builder().id(id).forumMessages(List.of()).subject(null).build();
        when(forumRepository.findById(id)).thenReturn(Optional.of(existing));
        when(forumRepository.save(existing)).thenReturn(existing);

        var result = forumService.update(incoming);

        assertEquals(id.toString(), result);
        verify(forumRepository).save(existing);
    }

    @Test
    void deleteDelegatesToRepository() {
        var id = UUID.randomUUID();

        forumService.delete(id);

        verify(forumRepository).deleteById(id);
    }
}
