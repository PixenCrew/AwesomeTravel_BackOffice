package renewal.awesome_travel_backoffice.qna.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

import renewal.awesome_travel_backoffice.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Qna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 연관관계 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer; // 유저

    private String title;
    private String content;
    private boolean isAnswered;

    private LocalDateTime createdAt;

    private LocalDateTime answeredAt;

    public static Qna create(User writer, String title, String content) {
        Qna qna = new Qna();
        qna.writer = writer;
        qna.title = title;
        qna.content = content;
        qna.createdAt = LocalDateTime.now();
        return qna;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }


    public void markAnswered() {
        this.isAnswered = true;
        this.answeredAt = LocalDateTime.now();
    }
}
