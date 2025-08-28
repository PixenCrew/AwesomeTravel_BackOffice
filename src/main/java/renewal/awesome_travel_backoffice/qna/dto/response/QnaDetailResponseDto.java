package renewal.awesome_travel_backoffice.qna.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QnaDetailResponseDto {
    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private boolean isAnswered;
    private LocalDateTime createdAt;
    private List<QnaAnswerResponseDto> answers;
}


