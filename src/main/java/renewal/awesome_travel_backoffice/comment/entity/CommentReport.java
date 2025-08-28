package renewal.awesome_travel_backoffice.comment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;

import renewal.awesome_travel_backoffice.comment.utiles.ReportReason;
import renewal.awesome_travel_backoffice.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "comment_id"})
})
public class CommentReport {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    private LocalDateTime reportedAt;

    public static CommentReport create(User reporter, Comment comment, ReportReason reason) {
        CommentReport report = new CommentReport();
        report.reporter = reporter;
        report.comment = comment;
        report.reason = reason;
        report.reportedAt = LocalDateTime.now();
        return report;
    }
}

