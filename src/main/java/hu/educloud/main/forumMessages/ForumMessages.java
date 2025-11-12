package hu.educloud.main.forumMessages;

import com.fasterxml.jackson.annotation.JsonBackReference;
import hu.educloud.main.forum.Forum;
import hu.educloud.main.users.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
public class ForumMessages implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    private String message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private Users author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "forum_id")
    @JsonBackReference
    private Forum forum;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @CreatedBy
    @Column(name = "created_by")
    private UUID createdBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;
}
