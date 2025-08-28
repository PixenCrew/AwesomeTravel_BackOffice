package renewal.awesome_travel_backoffice.notice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import renewal.awesome_travel_backoffice.notice.dto.request.NoticeSearchRequest;
import renewal.awesome_travel_backoffice.notice.dto.response.NoticeResponseDto;

public interface NoticeQueryRepository {
    Page<NoticeResponseDto> search(NoticeSearchRequest noticeSearchRequest, Pageable pageable);
}
