package renewal.awesome_travel_backoffice.inquiry.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import lombok.Getter;

@Entity
@Getter
public class InquiryAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long inquiryId;
    private Long adminId;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static InquiryAnswer create(Long inquiryId, Long adminId, String content) {
        InquiryAnswer a = new InquiryAnswer();
        a.inquiryId = inquiryId;
        a.adminId = adminId;
        a.content = content;
        a.createdAt = LocalDateTime.now();
        return a;
    }

    public void updateContent(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }
}

