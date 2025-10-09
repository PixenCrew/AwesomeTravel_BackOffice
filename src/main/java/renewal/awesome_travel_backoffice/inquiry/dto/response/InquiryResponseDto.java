package renewal.awesome_travel_backoffice.inquiry.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import renewal.common.entity.Inquiry.InquiryCategory;
import renewal.common.entity.Inquiry.InquiryStatus;

@Getter
@Builder
public class InquiryResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private String title;
    private String content;
    private InquiryCategory category;
    private InquiryStatus status;
    private boolean isAnswered;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
}
