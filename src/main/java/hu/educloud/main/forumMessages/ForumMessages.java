package hu.educloud.main.forumMessages;

import com.fasterxml.jackson.annotation.JsonBackReference;
import hu.educloud.main.forum.Forum;
import hu.educloud.main.users.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

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
    @JsonBackReference
    private Users author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "forum_id")
    @JsonBackReference
    private Forum forum;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;
    private String createdBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedAt;
    private String updatedBy;
}
