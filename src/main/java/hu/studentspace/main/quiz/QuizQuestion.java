package hu.studentspace.main.quiz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hu.studentspace.main.exercises.ExerciseType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
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
    @JsonIgnore
    private Quiz quiz;

    @Enumerated(EnumType.STRING)
    private ExerciseType type;

    @Column(columnDefinition = "TEXT")
    private String question;


    @Column(columnDefinition = "TEXT")
    private String options;


    @Column(columnDefinition = "TEXT")
    private String correctAnswer;


    @Builder.Default
    private Integer points = 1;


    @Builder.Default
    private Integer orderIndex = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedAt;
}
