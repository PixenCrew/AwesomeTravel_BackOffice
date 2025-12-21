package renewal.awesome_travel_backoffice.promotion.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import renewal.common.entity.Promotion;

public class PromotionSpecification {

    // 제목으로 검색 (키워드 포함)
    public static Specification<Promotion> titleContains(String keyword) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(keyword)) {
                return null;
            }
            String keywordPattern = "%" + keyword.toLowerCase() + "%";
            return builder.like(builder.lower(root.get("title")), keywordPattern);
        };
    }

    // 메뉴 코드 필터
    public static Specification<Promotion> menuCodeEquals(String menuCodeCode) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(menuCodeCode)) {
                return null;
            }
            return builder.equal(root.get("menuCode").get("code"), menuCodeCode);
        };
    }

    // 상태 필터 (진행중/종료)
    public static Specification<Promotion> isActive(Boolean active) {
        return (root, query, builder) -> {
            if (active == null) {
                return null;
            }
            LocalDateTime now = LocalDateTime.now();
            if (active) {
                // 진행중: 현재 시간이 startTime과 endTime 사이
                return builder.and(
                    builder.lessThanOrEqualTo(root.get("startTime"), now),
                    builder.greaterThan(root.get("endTime"), now)
                );
            } else {
                // 종료/예정: 현재 시간이 startTime 이전이거나 endTime 이후
                return builder.or(
                    builder.greaterThan(root.get("startTime"), now),
                    builder.lessThanOrEqualTo(root.get("endTime"), now)
                );
            }
        };
    }

    // 시작일 범위 필터
    public static Specification<Promotion> startTimeBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            if (from != null && to != null) {
                LocalDateTime fromDateTime = from.atStartOfDay();
                LocalDateTime toDateTime = to.atTime(23, 59, 59);
                return builder.between(root.get("startTime"), fromDateTime, toDateTime);
            } else if (from != null) {
                LocalDateTime fromDateTime = from.atStartOfDay();
                return builder.greaterThanOrEqualTo(root.get("startTime"), fromDateTime);
            } else if (to != null) {
                LocalDateTime toDateTime = to.atTime(23, 59, 59);
                return builder.lessThanOrEqualTo(root.get("startTime"), toDateTime);
            }
            return null;
        };
    }

    // 종료일 범위 필터
    public static Specification<Promotion> endTimeBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            if (from != null && to != null) {
                LocalDateTime fromDateTime = from.atStartOfDay();
                LocalDateTime toDateTime = to.atTime(23, 59, 59);
                return builder.between(root.get("endTime"), fromDateTime, toDateTime);
            } else if (from != null) {
                LocalDateTime fromDateTime = from.atStartOfDay();
                return builder.greaterThanOrEqualTo(root.get("endTime"), fromDateTime);
            } else if (to != null) {
                LocalDateTime toDateTime = to.atTime(23, 59, 59);
                return builder.lessThanOrEqualTo(root.get("endTime"), toDateTime);
            }
            return null;
        };
    }
}

