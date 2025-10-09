package renewal.awesome_travel_backoffice.comment.dto.response;

import java.time.LocalDateTime;

import renewal.common.entity.CommentReport.ReportReason;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class CommentReportResponseDto {
    private Long reportId;
    private Long commentId;
    private String commentContent;
    private int rating;
    private String reporterName;
    private String reportedUserName;
    private ReportReason reason;
    private LocalDateTime reportedAt;
}


