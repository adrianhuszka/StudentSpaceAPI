package hu.studentspace.main.professions;

import hu.studentspace.main.errors.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessionsServiceTest {

    @Mock
    private ProfessionsRepository professionsRepository;

    @InjectMocks
    private ProfessionsService professionsService;

    @Test
    void getAllMapsAndEncodesImage() {
        var pngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 1};
        var profession = Professions.builder()
                .id(UUID.randomUUID())
                .name("IT")
                .description("Tech")
                .image(pngBytes)
                .build();
        when(professionsRepository.findAll()).thenReturn(List.of(profession));

        var result = professionsService.getAll();

        assertEquals(1, result.size());
        assertTrue(result.getFirst().image().startsWith("data:image/png;base64,"));
    }

    @Test
    void findByIdThrowsWhenMissing() {
        var id = UUID.randomUUID();
        when(professionsRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> professionsService.findById(id));
    }

    @Test
    void saveDecodesRawBase64Image() {
        var bytes = new byte[]{1, 2, 3};
        var base64 = Base64.getEncoder().encodeToString(bytes);
        var request = new ProfessionsRequestDTO(null, "IT", "Tech", base64);
        var saved = Professions.builder().id(UUID.randomUUID()).build();
        when(professionsRepository.save(any(Professions.class))).thenReturn(saved);

        professionsService.save(request);

        ArgumentCaptor<Professions> captor = ArgumentCaptor.forClass(Professions.class);
        verify(professionsRepository).save(captor.capture());
        assertArrayEquals(bytes, captor.getValue().getImage());
    }

    @Test
    void saveDecodesDataUrlBase64Image() {
        var bytes = new byte[]{5, 6, 7};
        var base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        var request = new ProfessionsRequestDTO(null, "IT", "Tech", base64);
        var saved = Professions.builder().id(UUID.randomUUID()).build();
        when(professionsRepository.save(any(Professions.class))).thenReturn(saved);

        professionsService.save(request);

        ArgumentCaptor<Professions> captor = ArgumentCaptor.forClass(Professions.class);
        verify(professionsRepository).save(captor.capture());
        assertArrayEquals(bytes, captor.getValue().getImage());
    }

    @Test
    void saveThrowsOnInvalidBase64() {
        var request = new ProfessionsRequestDTO(null, "IT", "Tech", "%%%not-base64%%%");

        assertThrows(IllegalArgumentException.class, () -> professionsService.save(request));
    }

    @Test
    void updateWithImageReplacesImage() {
        var id = UUID.randomUUID();
        var existing = Professions.builder().id(id).image(new byte[]{1}).name("Old").description("Old").build();
        var newImageBytes = new byte[]{9, 8};
        var request = new ProfessionsRequestDTO(id.toString(), "New", "Desc", Base64.getEncoder().encodeToString(newImageBytes));

        when(professionsRepository.findById(id)).thenReturn(Optional.of(existing));
        when(professionsRepository.save(existing)).thenReturn(existing);

        var result = professionsService.update(request);

        assertEquals(id.toString(), result);
        assertEquals("New", existing.getName());
        assertEquals("Desc", existing.getDescription());
        assertArrayEquals(newImageBytes, existing.getImage());
    }
}
