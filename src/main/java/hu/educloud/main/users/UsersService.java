package hu.educloud.main.users;

import hu.educloud.main.common.IService;
import hu.educloud.main.errors.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService implements IService<Users, UsersDTO> {
    private final UsersRepository usersRepository;

    @Override
    public List<Users> getAll() {
        return usersRepository.findAll();
    }

    @Override
    public Users findById(@NonNull UUID id) {
        return usersRepository.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));
    }

    public  Users findByUsername(@NonNull String username) {
        return usersRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(username));
    }

    @Override
    public String save(@NonNull UsersDTO usersDTO) {
        var user = Users.builder()
                .username(usersDTO.username())
                .email(usersDTO.email())
                .password(usersDTO.password())
                .build();

        return usersRepository.save(user).getId();
    }

    @Override
    public String update(@NonNull UsersDTO usersDTO) {
        var user = usersRepository.findById(UUID.fromString(usersDTO.id()))
                .orElseThrow(() -> new NotFoundException(usersDTO.id()));

        user.setUsername(usersDTO.username());
        user.setEmail(usersDTO.email());
        user.setPassword(usersDTO.password());

        return usersRepository.save(user).getId();
    }

    @Override
    public void delete(@NonNull UUID id) {
        usersRepository.deleteById(id);
    }
}
