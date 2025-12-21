package renewal.awesome_travel_backoffice.popup.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import renewal.common.entity.Popup;

import java.time.LocalDate;

public class PopupSpecification {

    // 제목으로 검색 (키워드 포함)
    public static Specification<Popup> titleContains(String keyword) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(keyword)) {
                return null;
            }
            String keywordPattern = "%" + keyword.toLowerCase() + "%";
            return builder.like(builder.lower(root.get("title")), keywordPattern);
        };
    }

    // 노출기간이 필터 기간과 겹치는 팝업 검색
    // 필터 기간과 팝업의 노출기간이 겹치는 조건: popup.startDate <= filterEndDate AND popup.endDate >= filterStartDate
    public static Specification<Popup> displayPeriodOverlaps(LocalDate filterStartDate, LocalDate filterEndDate) {
        return (root, query, builder) -> {
            if (filterStartDate != null && filterEndDate != null) {
                // 두 기간이 겹치는 조건: 팝업 시작일 <= 필터 종료일 AND 팝업 종료일 >= 필터 시작일
                return builder.and(
                    builder.lessThanOrEqualTo(root.get("startDate"), filterEndDate),
                    builder.greaterThanOrEqualTo(root.get("endDate"), filterStartDate)
                );
            } else if (filterStartDate != null) {
                // 시작일만 있는 경우: 팝업 종료일 >= 필터 시작일
                return builder.greaterThanOrEqualTo(root.get("endDate"), filterStartDate);
            } else if (filterEndDate != null) {
                // 종료일만 있는 경우: 팝업 시작일 <= 필터 종료일
                return builder.lessThanOrEqualTo(root.get("startDate"), filterEndDate);
            }
            return null;
        };
    }

    // 활성화 상태 필터
    public static Specification<Popup> isActive(Boolean active) {
        return (root, query, builder) -> {
            if (active == null) {
                return null;
            }
            return builder.equal(root.get("active"), active);
        };
    }
}

