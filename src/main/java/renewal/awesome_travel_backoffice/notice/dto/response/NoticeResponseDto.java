package renewal.awesome_travel_backoffice.notice.dto.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import renewal.common.entity.Notice.NoticeCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeResponseDto {
    private Long id;
    private String title;
    private String content;
    private Boolean fix;
    private Integer priority;
    private String imageUrl;
    private NoticeCategory category;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @QueryProjection
    public NoticeResponseDto(Long id, String title, String content, Boolean fix,
                             Integer priority, String imageUrl, NoticeCategory category,
                             LocalDateTime startAt, LocalDateTime endAt,
                             LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.fix = fix;
        this.priority = priority;
        this.imageUrl = imageUrl;
        this.category = category;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}

