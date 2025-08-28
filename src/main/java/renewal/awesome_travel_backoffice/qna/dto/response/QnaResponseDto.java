package renewal.awesome_travel_backoffice.qna.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QnaResponseDto {
    private Long id;
    private Long writerId;
    private String title;
    private String content;
    private boolean isAnswered;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
}

