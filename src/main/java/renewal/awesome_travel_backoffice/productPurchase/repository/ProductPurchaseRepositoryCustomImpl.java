package renewal.awesome_travel_backoffice.productPurchase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import renewal.awesome_travel_backoffice.productPurchase.dto.request.ProductPurchaseSearchCondition;
import renewal.common.entity.BasePurchase;
import renewal.common.entity.ProductPurchase;
import renewal.common.entity.QProductPurchase;

import java.time.LocalDateTime;
import java.util.List;

import static renewal.common.entity.QProductPurchase.productPurchase;

@Repository
public class ProductPurchaseRepositoryCustomImpl implements ProductPurchaseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductPurchaseRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ProductPurchase> searchByCondition(ProductPurchaseSearchCondition condition, Pageable pageable) {
        List<ProductPurchase> content = queryFactory
                .selectFrom(productPurchase)
                .where(
                        productPurchaseIdEq(condition.getProductPurchaseId()),
                        purchaseStatusEq(condition.getPurchaseStatus()),
                        memberIdEq(condition.getMemberId()),
                        productIdEq(condition.getProductId()),
                        purchaseDateBetween(condition.getPurchaseDateFrom(), condition.getPurchaseDateTo()),
                        priceBetween(condition.getMinPrice(), condition.getMaxPrice())
                )
                .orderBy(productPurchase.purchaseDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(productPurchase.count()) // ✅ count projection 명시
                .from(productPurchase)
                .where(
                        productPurchaseIdEq(condition.getProductPurchaseId()),
                        purchaseStatusEq(condition.getPurchaseStatus()),
                        memberIdEq(condition.getMemberId()),
                        productIdEq(condition.getProductId()),
                        purchaseDateBetween(condition.getPurchaseDateFrom(), condition.getPurchaseDateTo()),
                        priceBetween(condition.getMinPrice(), condition.getMaxPrice())
                )
                .fetchOne(); // 단일 결과

        return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
    }

    private BooleanExpression productPurchaseIdEq(Long productPurchaseId) {
        return productPurchaseId != null ? productPurchase.productPurchaseId.eq(productPurchaseId) : null;
    }

    private BooleanExpression purchaseStatusEq(String purchaseStatus) {
        return purchaseStatus != null && !purchaseStatus.trim().isEmpty() ? 
            productPurchase.purchaseStatus.eq(BasePurchase.PurchaseStatus.valueOf(purchaseStatus)) : null;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return memberId != null ? productPurchase.member_id.eq(memberId) : null;
    }

    private BooleanExpression productIdEq(Long productId) {
        return productId != null ? productPurchase.product.id.eq(productId) : null;
    }

    private BooleanExpression purchaseDateBetween(LocalDateTime purchaseDateFrom, LocalDateTime purchaseDateTo) {
        if (purchaseDateFrom != null && purchaseDateTo != null) {
            return productPurchase.purchaseDate.between(purchaseDateFrom, purchaseDateTo);
        } else if (purchaseDateFrom != null) {
            return productPurchase.purchaseDate.goe(purchaseDateFrom);
        } else if (purchaseDateTo != null) {
            return productPurchase.purchaseDate.loe(purchaseDateTo);
        }
        return null;
    }

    private BooleanExpression priceBetween(Long minPrice, Long maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return productPurchase.price.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return productPurchase.price.goe(minPrice);
        } else if (maxPrice != null) {
            return productPurchase.price.loe(maxPrice);
        }
        return null;
    }
}
