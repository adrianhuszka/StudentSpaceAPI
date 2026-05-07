package hu.studentspace.main.users;

import hu.studentspace.main.errors.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersService usersService;

    @Test
    void getAllReturnsUsers() {
        var user = Users.builder().id(UUID.randomUUID()).username("alice").build();
        when(usersRepository.findAll()).thenReturn(List.of(user));

        var result = usersService.getAll();

        assertEquals(1, result.size());
        assertEquals("alice", result.getFirst().getUsername());
    }

    @Test
    void findByIdReturnsUserWhenExists() {
        var id = UUID.randomUUID();
        var user = Users.builder().id(id).username("alice").build();
        when(usersRepository.findById(id)).thenReturn(Optional.of(user));

        var result = usersService.findById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        var id = UUID.randomUUID();
        when(usersRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> usersService.findById(id));
    }

    @Test
    void findByUsernameReturnsMatch() {
        var user = Users.builder().id(UUID.randomUUID()).username("alice").build();
        when(usersRepository.findAll()).thenReturn(List.of(user));

        var result = usersService.findByUsername("alice");

        assertEquals("alice", result.getUsername());
    }

    @Test
    void findByUsernameThrowsWhenMissing() {
        when(usersRepository.findAll()).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> usersService.findByUsername("nobody"));
    }

    @Test
    void saveUsesDefaultStudentRoleWhenRolesNull() {
        var request = new UsersDTO(null, "alice", "alice@example.com", "pw", null);
        var saved = Users.builder().id(UUID.randomUUID()).username("alice").roles(Set.of(UserRole.STUDENT)).build();
        when(usersRepository.save(any(Users.class))).thenReturn(saved);

        usersService.save(request);

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(captor.capture());
        assertEquals(Set.of(UserRole.STUDENT), captor.getValue().getRoles());
    }

    @Test
    void updatePersistsChangedUser() {
        var id = UUID.randomUUID();
        var request = new UsersDTO(id.toString(), "bob", "bob@example.com", "newpw", Set.of(UserRole.ADMIN));
        var existing = Users.builder().id(id).username("old").email("old@example.com").password("oldpw").roles(Set.of(UserRole.STUDENT)).build();
        when(usersRepository.findById(id)).thenReturn(Optional.of(existing));
        when(usersRepository.save(existing)).thenReturn(existing);

        var resultId = usersService.update(request);

        assertEquals(id.toString(), resultId);
        assertEquals("bob", existing.getUsername());
        assertEquals("bob@example.com", existing.getEmail());
        assertEquals("newpw", existing.getPassword());
        assertEquals(Set.of(UserRole.ADMIN), existing.getRoles());
    }
}
