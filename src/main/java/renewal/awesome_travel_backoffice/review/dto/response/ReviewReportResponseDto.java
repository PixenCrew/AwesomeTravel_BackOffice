package renewal.awesome_travel_backoffice.review.dto.response;

import java.time.LocalDateTime;

import renewal.common.entity.ReviewReport.ReportReason;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class ReviewReportResponseDto {
    private Long reportId;
    private Long reviewId;
    private String reviewContent;
    private int rating;
    private String reporterName;
    private String reportedUserName;
    private ReportReason reason;
    private LocalDateTime reportedAt;
}


