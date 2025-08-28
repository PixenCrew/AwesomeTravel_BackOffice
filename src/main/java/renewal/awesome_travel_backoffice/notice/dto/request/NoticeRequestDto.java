package renewal.awesome_travel_backoffice.notice.dto.request;

import java.time.LocalDateTime;

import renewal.awesome_travel_backoffice.notice.utils.NoticeCategory;

import lombok.Getter;

@Getter
public class NoticeRequestDto {
    private String title;
    private String content;
    private Boolean fix;
    private Integer priority;
    private String imageUrl;
    private NoticeCategory category;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}

