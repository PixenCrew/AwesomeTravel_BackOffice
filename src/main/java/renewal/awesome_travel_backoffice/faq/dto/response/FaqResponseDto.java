package renewal.awesome_travel_backoffice.faq.dto.response;

import java.time.LocalDateTime;

import renewal.common.entity.Faq.FaqCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FaqResponseDto {
    Long id;
    String question;
    String answer;
    FaqCategory category;
    LocalDateTime createdAt;
}
