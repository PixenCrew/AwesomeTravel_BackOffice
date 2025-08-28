package renewal.awesome_travel_backoffice.faq.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import renewal.awesome_travel_backoffice.config.AuditingFields;
import renewal.awesome_travel_backoffice.faq.utils.FaqCategory;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Faq extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private String answer;
    @Enumerated(EnumType.STRING)
    private FaqCategory category;

    // 생성 메서드
    public Faq (String question, String answer, FaqCategory category) {
        this.question = question;
        this.answer = answer;
        this.category = category;
    }

    // 수정 메서드
    public void update(String question, String answer, FaqCategory category) {
        this.question = question;
        this.answer = answer;
        this.category = category;
    }
}

