package hu.studentspace.main.quiz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttempt implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;


    @Column(nullable = false)
    private String studentId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp startedAt;

    private Timestamp completedAt;


    @Column(columnDefinition = "TEXT")
    private String answers;


    private Integer score;


    private Integer pointsEarned;

    private Integer totalPoints;

    private Boolean passed;
}
