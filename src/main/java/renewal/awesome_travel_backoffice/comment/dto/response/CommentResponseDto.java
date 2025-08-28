package renewal.awesome_travel_backoffice.comment.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public class CommentResponseDto {
    private Long id;
    private String writerName;
    private String content;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

