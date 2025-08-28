package renewal.awesome_travel_backoffice.qna.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QnaAnswerResponseDto {
    private Long id;
    private Long qnaId;
    private Long responderId;
    private String content;
    private LocalDateTime createdAt;
}


