package hu.educloud.main.config;

import hu.educloud.main.users.UsersRepository;
import hu.educloud.main.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public Optional<UUID> getCurrentAuditor() {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return Optional.empty();
        }

        return usersRepository.findByUsername(username)
                .map(Users::getId);
    }
}
