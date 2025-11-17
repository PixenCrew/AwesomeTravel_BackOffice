package renewal.awesome_travel_backoffice.notice.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import renewal.common.entity.Notice;
import renewal.common.entity.Notice.NoticeCategory;
import renewal.common.entity.Notice.SearchType;

public class NoticeSpecification {

    // 키워드 검색 (제목, 내용)
    public static Specification<Notice> keywordContains(String keyword, SearchType searchType) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(keyword)) {
                return null;
            }
            
            String keywordPattern = "%" + keyword.toLowerCase() + "%";
            
            if (searchType == null || searchType == SearchType.TITLE_CONTENT) {
                return builder.or(
                    builder.like(builder.lower(root.get("title")), keywordPattern),
                    builder.like(builder.lower(root.get("content")), keywordPattern)
                );
            } else if (searchType == SearchType.TITLE) {
                return builder.like(builder.lower(root.get("title")), keywordPattern);
            } else if (searchType == SearchType.CONTENT) {
                return builder.like(builder.lower(root.get("content")), keywordPattern);
            }
            
            return null;
        };
    }

    // 카테고리 필터
    public static Specification<Notice> categoryEquals(NoticeCategory category) {
        return (root, query, builder) -> {
            if (category == null) {
                return null;
            }
            return builder.equal(root.get("category"), category);
        };
    }

    // 고정 여부 필터
    public static Specification<Notice> fixEquals(Boolean fix) {
        return (root, query, builder) -> {
            if (fix == null) {
                return null;
            }
            return builder.equal(root.get("fix"), fix);
        };
    }

    // 숨김 공지사항 포함 여부
    public static Specification<Notice> includeHidden(Boolean includeHidden) {
        return (root, query, builder) -> {
            if (includeHidden == null || !includeHidden) {
                // 기본적으로는 공개된 공지만 표시
                return builder.equal(root.get("isVisible"), true);
            }
            // includeHidden이 true면 모든 공지사항 표시 (조건 없음)
            return null;
        };
    }
}

