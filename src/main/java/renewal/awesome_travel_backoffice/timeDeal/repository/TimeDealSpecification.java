package renewal.awesome_travel_backoffice.timeDeal.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;
import renewal.common.entity.TimeDeal;
import renewal.common.entity.TimeDeal.DiscountType;

public class TimeDealSpecification {

    // 할인 유형 필터
    public static Specification<TimeDeal> discountTypeEquals(DiscountType discountType) {
        return (root, query, builder) -> {
            if (discountType == null) {
                return null;
            }
            return builder.equal(root.get("discountType"), discountType);
        };
    }

    // 상태 필터 (진행중/종료)
    public static Specification<TimeDeal> isActive(Boolean active) {
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
    public static Specification<TimeDeal> startTimeBetween(LocalDate from, LocalDate to) {
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
    public static Specification<TimeDeal> endTimeBetween(LocalDate from, LocalDate to) {
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

    // 할인 값 범위 필터
    public static Specification<TimeDeal> valueBetween(Long min, Long max) {
        return (root, query, builder) -> {
            if (min != null && max != null) {
                return builder.between(root.get("value"), min, max);
            } else if (min != null) {
                return builder.greaterThanOrEqualTo(root.get("value"), min);
            } else if (max != null) {
                return builder.lessThanOrEqualTo(root.get("value"), max);
            }
            return null;
        };
    }
}

