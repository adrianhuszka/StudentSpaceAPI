package hu.educloud.main.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    @Query("SELECT s FROM Subject s JOIN s.professions p WHERE p.id = :professionId ORDER BY s.name ASC")
    List<Subject> getAllByProfessionId(@Param("professionId") UUID professionId);
}
