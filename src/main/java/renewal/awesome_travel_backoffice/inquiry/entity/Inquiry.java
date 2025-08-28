package renewal.awesome_travel_backoffice.inquiry.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;

import renewal.awesome_travel_backoffice.user.entity.User;

import lombok.Getter;

@Entity
@Getter
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private boolean isAnswered = false;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;

    public static Inquiry create(User user, String title, String content) {
        Inquiry i = new Inquiry();
        i.user = user;
        i.title = title;
        i.content = content;
        i.createdAt = LocalDateTime.now();
        return i;
    }

    public void markAnswered() {
        this.isAnswered = true;
        this.answeredAt = LocalDateTime.now();
    }

    public void cancelAnswered() {
        this.isAnswered = false;
        this.answeredAt = null;
    }
}


