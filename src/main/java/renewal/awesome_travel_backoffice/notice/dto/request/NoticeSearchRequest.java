package renewal.awesome_travel_backoffice.notice.dto.request;

import renewal.awesome_travel_backoffice.notice.utils.NoticeCategory;
import renewal.awesome_travel_backoffice.notice.utils.SearchType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeSearchRequest {
    private NoticeCategory category;
    private SearchType searchType; // 제목 검색
    private Boolean fix;  // 고정 여부
    private String keyword;
    private Boolean includeHidden; // true: 숨김 포함 / false or null: 공개된 공지만
}
