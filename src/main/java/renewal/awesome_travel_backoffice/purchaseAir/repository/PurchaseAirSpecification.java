package renewal.awesome_travel_backoffice.purchaseAir.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import renewal.common.entity.PurchaseAir;
import renewal.common.entity.PurchaseBase.PurchaseStatus;

public class PurchaseAirSpecification {

    // 구매 상태
    public static Specification<PurchaseAir> statusEquals(PurchaseStatus status) {
        return (root, query, builder) -> status == null
                ? null
                : builder.equal(root.get("purchaseStatus"), status);
    }

    // 예약자 이름 LIKE
    public static Specification<PurchaseAir> nameContains(String name) {
        return (root, query, builder) -> !StringUtils.hasText(name)
                ? null
                : builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    // 예약자 이메일 LIKE
    public static Specification<PurchaseAir> emailContains(String email) {
        return (root, query, builder) -> !StringUtils.hasText(email)
                ? null
                : builder.like(builder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    // 구매일 범위 (LocalDate로 변환)
    public static Specification<PurchaseAir> purchaseDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, builder) -> {
            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
                return builder.between(root.get("purchaseDate"), startDateTime, endDateTime);
            } else if (startDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                return builder.greaterThanOrEqualTo(root.get("purchaseDate"), startDateTime);
            } else if (endDate != null) {
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
                return builder.lessThanOrEqualTo(root.get("purchaseDate"), endDateTime);
            }
            return null;
        };
    }
}

