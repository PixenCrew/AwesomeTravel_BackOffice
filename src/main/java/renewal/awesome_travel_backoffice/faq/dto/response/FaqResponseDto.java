package renewal.awesome_travel_backoffice.faq.dto.response;

import java.time.LocalDateTime;

import renewal.awesome_travel_backoffice.faq.utils.FaqCategory;

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
