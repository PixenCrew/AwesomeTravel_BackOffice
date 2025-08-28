package renewal.awesome_travel_backoffice.inquiry.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InquiryResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private boolean isAnswered;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
}
