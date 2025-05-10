package renewal.awesome_travel_backoffice.comment.dto.response;

import lombok.Builder;
import renewal.awesome_travel_backoffice.comment.utiles.ReportReason;

import java.time.LocalDateTime;
import lombok.Getter;

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


