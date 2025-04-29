package renewal.awesome_travel_backoffice.qna.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QnaRequestDto {
    private String title;
    private String content;
}

