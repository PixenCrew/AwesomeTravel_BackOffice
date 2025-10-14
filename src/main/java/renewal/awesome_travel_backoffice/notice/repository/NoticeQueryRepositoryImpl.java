package renewal.awesome_travel_backoffice.notice.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import renewal.awesome_travel_backoffice.notice.dto.request.NoticeSearchRequest;
import renewal.awesome_travel_backoffice.notice.dto.response.NoticeResponseDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NoticeQueryRepositoryImpl implements NoticeQueryRepository {

    private final EntityManager entityManager;

    @Override
    public Page<NoticeResponseDto> search(NoticeSearchRequest nsr, Pageable pageable) {
        // TODO: QueryDSL Q클래스 생성 후 구현
        // 임시로 빈 결과 반환
        List<NoticeResponseDto> results = new ArrayList<>();
        long total = 0;
        return new PageImpl<>(results, pageable, total);
    }
}
