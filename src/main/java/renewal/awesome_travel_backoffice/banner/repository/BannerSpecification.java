package renewal.awesome_travel_backoffice.banner.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import renewal.common.entity.Banner;

import java.time.LocalDate;

public class BannerSpecification {

    // 제목으로 검색 (키워드 포함)
    public static Specification<Banner> titleContains(String keyword) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(keyword)) {
                return null;
            }
            String keywordPattern = "%" + keyword.toLowerCase() + "%";
            return builder.like(builder.lower(root.get("title")), keywordPattern);
        };
    }

    // 노출기간이 필터 기간과 겹치는 배너 검색
    // 필터 기간과 배너의 노출기간이 겹치는 조건: banner.startDate <= filterEndDate AND banner.endDate >= filterStartDate
    public static Specification<Banner> displayPeriodOverlaps(LocalDate filterStartDate, LocalDate filterEndDate) {
        return (root, query, builder) -> {
            if (filterStartDate != null && filterEndDate != null) {
                // 두 기간이 겹치는 조건: 배너 시작일 <= 필터 종료일 AND 배너 종료일 >= 필터 시작일
                return builder.and(
                    builder.lessThanOrEqualTo(root.get("startDate"), filterEndDate),
                    builder.greaterThanOrEqualTo(root.get("endDate"), filterStartDate)
                );
            } else if (filterStartDate != null) {
                // 시작일만 있는 경우: 배너 종료일 >= 필터 시작일
                return builder.greaterThanOrEqualTo(root.get("endDate"), filterStartDate);
            } else if (filterEndDate != null) {
                // 종료일만 있는 경우: 배너 시작일 <= 필터 종료일
                return builder.lessThanOrEqualTo(root.get("startDate"), filterEndDate);
            }
            return null;
        };
    }

    // 활성화 상태 필터
    public static Specification<Banner> isActive(Boolean active) {
        return (root, query, builder) -> {
            if (active == null) {
                return null;
            }
            return builder.equal(root.get("active"), active);
        };
    }
}

