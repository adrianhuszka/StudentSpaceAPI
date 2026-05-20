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

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Autowired
    private UsersRepository usersRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<String> getCurrentAuditor() {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return Optional.empty();
        }

        
        FlushModeType originalFlushMode = entityManager.getFlushMode();
        try {
            entityManager.setFlushMode(FlushModeType.COMMIT);
            return usersRepository.findByUsername(username)
                    .map(Users::getUsername);
        } finally {
            entityManager.setFlushMode(originalFlushMode);
        }
    }
}
