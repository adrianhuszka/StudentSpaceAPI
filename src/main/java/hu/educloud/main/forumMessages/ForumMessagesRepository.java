package hu.studentspace.main.forumMessages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ForumMessagesRepository extends JpaRepository<ForumMessages, UUID> {
}
