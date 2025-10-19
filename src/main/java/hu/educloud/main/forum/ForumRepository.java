package hu.educloud.main.forum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ForumRepository extends JpaRepository<Forum, UUID> {
}

