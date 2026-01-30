package hu.educloud.main.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findAllByModuleId(UUID moduleId);
    List<Quiz> findAllByIsActiveTrue();
    List<Quiz> findAllByModuleIdAndIsActiveTrue(UUID moduleId);
    List<Quiz> findAllBySubjectIdAndIsActiveTrue(UUID subjectId);
}
