package renewal.awesome_travel_backoffice.purchaseProduct.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.JoinType;
import renewal.common.entity.PurchaseBase.PurchaseStatus;
import renewal.common.entity.PurchaseProduct;

public class PurchaseProductSpecification {

    // 구매 ID
    public static Specification<PurchaseProduct> productPurchaseIdEquals(Long productPurchaseId) {
        return (root, query, builder) -> productPurchaseId == null
                ? null
                : builder.equal(root.get("id"), productPurchaseId);
    }

    // 구매 상태
    public static Specification<PurchaseProduct> purchaseStatusEquals(String purchaseStatus) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(purchaseStatus)) {
                return null;
            }
            try {
                PurchaseStatus status = PurchaseStatus.valueOf(purchaseStatus.toUpperCase());
                return builder.equal(root.get("purchaseStatus"), status);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    // 회원 ID
    public static Specification<PurchaseProduct> memberIdEquals(Long memberId) {
        return (root, query, builder) -> memberId == null
                ? null
                : builder.equal(root.get("user").get("id"), memberId);
    }

    // 상품 ID
    public static Specification<PurchaseProduct> productIdEquals(Long productId) {
        return (root, query, builder) -> productId == null
                ? null
                : builder.equal(root.get("product").get("id"), productId);
    }

    // 구매일 범위
    public static Specification<PurchaseProduct> purchaseDateBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, builder) -> {
            if (from != null && to != null) {
                return builder.between(root.get("purchaseDate"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(root.get("purchaseDate"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(root.get("purchaseDate"), to);
            }
            return null;
        };
    }

    // 가격 범위
    public static Specification<PurchaseProduct> priceBetween(Long minPrice, Long maxPrice) {
        return (root, query, builder) -> {
            if (minPrice != null && maxPrice != null) {
                return builder.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return builder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else if (maxPrice != null) {
                return builder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            return null;
        };
    }

    // 고객명 LIKE
    public static Specification<PurchaseProduct> customerNameContains(String customerName) {
        return (root, query, builder) -> !StringUtils.hasText(customerName)
                ? null
                : builder.like(builder.lower(root.get("name")), "%" + customerName.toLowerCase() + "%");
    }

    // 고객 이메일 LIKE
    public static Specification<PurchaseProduct> customerEmailContains(String customerEmail) {
        return (root, query, builder) -> !StringUtils.hasText(customerEmail)
                ? null
                : builder.like(builder.lower(root.get("email")), "%" + customerEmail.toLowerCase() + "%");
    }

    // 상품명 LIKE
    public static Specification<PurchaseProduct> productTitleContains(String productTitle) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(productTitle)) {
                return null;
            }
            query.distinct(true);
            return builder.like(builder.lower(root.get("product").get("title")),
                    "%" + productTitle.toLowerCase() + "%");
        };
    }
}

