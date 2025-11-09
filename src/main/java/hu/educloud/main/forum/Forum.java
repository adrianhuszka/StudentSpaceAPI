package hu.educloud.main.forum;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import hu.educloud.main.forumMessages.ForumMessages;
import hu.educloud.main.subject.Subject;
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
public class Forum implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @OneToMany(mappedBy = "forum", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ForumMessages> forumMessages;

    @OneToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private Subject subject;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @CreatedBy
    private String createdBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedAt;

    @LastModifiedBy
    private String updatedBy;
}
