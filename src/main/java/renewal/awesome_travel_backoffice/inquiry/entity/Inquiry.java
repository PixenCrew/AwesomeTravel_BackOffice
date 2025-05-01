package renewal.awesome_travel_backoffice.inquiry.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private boolean isAnswered = false;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;

    public static Inquiry create(Long userId, String title, String content) {
        Inquiry i = new Inquiry();
        i.userId = userId;
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


