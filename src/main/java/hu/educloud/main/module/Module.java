package hu.educloud.main.module;

import com.fasterxml.jackson.annotation.JsonBackReference;
import hu.educloud.main.exercises.Exercises;
import hu.educloud.main.subject.Subject;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Module implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;
    private ModuleTypes moduleType;

    @JdbcTypeCode(Types.VARBINARY)
    @Column(name = "pdf_file", columnDefinition = "bytea")
    private byte[] pdfFile;

    private String pdfFileName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Subject subject;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exercises> exercises;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedAt;

    @LastModifiedBy
    private String updatedBy;
}
