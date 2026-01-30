package hu.educloud.main.quiz;

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
    private Quiz quiz;

    /**
     * Student identifier (username from JWT sub claim)
     */
    @Column(nullable = false)
    private String studentId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp startedAt;

    private Timestamp completedAt;

    /**
     * Student answers, stored as JSON
     * Format: {"questionId": "answer", ...}
     */
    @Column(columnDefinition = "TEXT")
    private String answers;

    /**
     * Score achieved (0-100 percentage)
     */
    private Integer score;

    /**
     * Points earned out of total possible points
     */
    private Integer pointsEarned;

    private Integer totalPoints;

    private Boolean passed;
}
