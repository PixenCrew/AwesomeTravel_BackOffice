package renewal.awesome_travel_backoffice.notice.repository;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import renewal.awesome_travel_backoffice.notice.dto.request.NoticeSearchRequest;
import renewal.awesome_travel_backoffice.notice.dto.response.NoticeResponseDto;
import renewal.awesome_travel_backoffice.notice.dto.response.QNoticeResponseDto;
import renewal.common.entity.Notice.NoticeCategory;
import renewal.common.entity.Notice.SearchType;
import static renewal.common.entity.QNotice.notice;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NoticeQueryRepositoryImpl implements NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NoticeResponseDto> search(NoticeSearchRequest nsr, Pageable pageable) {
        List<NoticeResponseDto> results = queryFactory
                .select(new QNoticeResponseDto(
                        notice.id,
                        notice.title,
                        notice.content,
                        notice.fix,
                        notice.priority,
                        notice.imageUrl,
                        notice.category,
                        notice.startAt,
                        notice.endAt,
                        notice.isVisible,
                        notice.createdAt,
                        notice.modifiedAt,
                        notice.createdBy,
                        notice.modifiedBy
                ))
                .from(notice)
                .where(
                        categoryEq(nsr.getCategory()),
                        searchByType(nsr.getKeyword(), nsr.getSearchType()),
                        fixEq(nsr.getFix()),
                        isVisibleCondition(nsr.getIncludeHidden())
                        // 관리자 페이지에서는 노출 기간 조건 제거
                )
                .orderBy(
                        notice.fix.desc(),
                        notice.priority.asc().nullsLast(),
                        notice.createdAt.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(notice.count())
                .from(notice)
                .where(
                        categoryEq(nsr.getCategory()),
                        searchByType(nsr.getKeyword(), nsr.getSearchType()),
                        fixEq(nsr.getFix()),
                        isVisibleCondition(nsr.getIncludeHidden())
                        // 관리자 페이지에서는 노출 기간 조건 제거
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression categoryEq(NoticeCategory category) {
        return category != null ? notice.category.eq(category) : null;
    }

    private BooleanExpression fixEq(Boolean fix) {
        return fix != null ? notice.fix.eq(fix) : null;
    }

    private BooleanExpression isVisibleCondition(Boolean includeHidden) {
        if (includeHidden != null && includeHidden) {
            System.out.println("DEBUG: includeHidden=true, 전체 공지사항 조회");
            return null; // true일 때 전체 조회
        }
        System.out.println("DEBUG: includeHidden=" + includeHidden + ", 공개 공지만 조회");
        return notice.isVisible.eq(true); // 기본: 공개 공지만
    }




    private BooleanExpression searchByType(String keyword, SearchType searchType) {
        if (keyword == null || keyword.isBlank() || searchType == null) return null;

        return switch (searchType) {
            case TITLE -> notice.title.containsIgnoreCase(keyword);
            case CONTENT -> notice.content.containsIgnoreCase(keyword);
            case TITLE_CONTENT -> notice.title.containsIgnoreCase(keyword)
                    .or(notice.content.containsIgnoreCase(keyword));
        };
    }
}
