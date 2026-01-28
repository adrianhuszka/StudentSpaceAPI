package hu.studentspace.main.config;

import hu.studentspace.main.users.UsersRepository;
import hu.studentspace.main.users.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Autowired
    private UsersRepository usersRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<UUID> getCurrentAuditor() {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return Optional.empty();
        }

        // Disable auto-flush to prevent infinite recursion during auditing
        FlushModeType originalFlushMode = entityManager.getFlushMode();
        try {
            entityManager.setFlushMode(FlushModeType.COMMIT);
            return usersRepository.findByUsername(username)
                    .map(Users::getId);
        } finally {
            entityManager.setFlushMode(originalFlushMode);
        }
    }
}
