package renewal.awesome_travel_backoffice.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import renewal.common.entity.ReviewReport.ReportReason;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReportResponseDto {
    private Long id;
    private Long reviewId;
    private String reviewContent;
    private Long reporterId;
    private String reporterName;
    private Long writerId;
    private String writerName;
    private ReportReason reason;
    private LocalDateTime reportedAt;
}

