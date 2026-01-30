package hu.educloud.main.quiz;

import hu.educloud.main.exercises.ExerciseType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Enumerated(EnumType.STRING)
    private ExerciseType type;

    @Column(columnDefinition = "TEXT")
    private String question;

    /**
     * Options for multiple choice questions, stored as JSON array string
     * e.g., ["Option A", "Option B", "Option C", "Option D"]
     */
    @Column(columnDefinition = "TEXT")
    private String options;

    /**
     * Correct answer(s), format depends on question type
     * For multiple choice: the correct option text
     * For true/false: "true" or "false"
     * For short answer: the expected answer
     */
    @Column(columnDefinition = "TEXT")
    private String correctAnswer;

    /**
     * Points awarded for correct answer
     */
    @Builder.Default
    private Integer points = 1;

    /**
     * Order of the question in the quiz
     */
    @Builder.Default
    private Integer orderIndex = 0;
}
