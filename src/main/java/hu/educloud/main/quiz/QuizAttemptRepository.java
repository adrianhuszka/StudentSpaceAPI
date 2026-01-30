package hu.educloud.main.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    List<QuizAttempt> findAllByStudentId(String studentId);
    List<QuizAttempt> findAllByQuizId(UUID quizId);
    List<QuizAttempt> findAllByQuizIdAndStudentId(UUID quizId, String studentId);
}
