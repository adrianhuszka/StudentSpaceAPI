package hu.educloud.main.subject;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import hu.educloud.main.forum.Forum;
import hu.educloud.main.module.Module;
import hu.educloud.main.professions.Professions;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Subject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    private String name;
    private String description;

    @ManyToMany(mappedBy = "subjects", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Professions> professions;

    @OneToMany(mappedBy = "subject", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Module> module;

    @OneToOne(mappedBy = "subject", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Forum forum;

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
