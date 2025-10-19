package hu.educloud.main.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String username = SecurityUtils.getCurrentUsername();
        return Optional.ofNullable(username);
    }
}

