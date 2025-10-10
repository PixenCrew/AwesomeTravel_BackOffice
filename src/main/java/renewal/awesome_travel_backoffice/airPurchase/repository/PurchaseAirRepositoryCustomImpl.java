package renewal.awesome_travel_backoffice.airPurchase.repository;

import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import renewal.awesome_travel_backoffice.airPurchase.dto.request.PurchaseAirSearchCondition;
import renewal.common.entity.QPurchaseAir;
import renewal.common.entity.PurchaseAir;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PurchaseAirRepositoryCustomImpl implements PurchaseAirRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PurchaseAir> searchByCondition(PurchaseAirSearchCondition cond, Pageable pageable) {
        QPurchaseAir q = QPurchaseAir.purchaseAir;

        BooleanBuilder builder = new BooleanBuilder();

        if (cond.getStatus() != null) {
            builder.and(q.purchaseStatus.eq(cond.getStatus()));
        }
        if (cond.getName() != null && !cond.getName().isBlank()) {
            builder.and(q.name.containsIgnoreCase(cond.getName()));
        }
        if (cond.getEmail() != null && !cond.getEmail().isBlank()) {
            builder.and(q.email.containsIgnoreCase(cond.getEmail()));
        }
        if (cond.getStartDate() != null) {
            builder.and(q.purchaseDate.goe(cond.getStartDate().atStartOfDay()));
        }
        if (cond.getEndDate() != null) {
            builder.and(q.purchaseDate.loe(cond.getEndDate().atTime(23, 59, 59)));
        }

        List<PurchaseAir> results = queryFactory.selectFrom(q)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(q.purchaseDate.desc())
                .fetch();

        Long count = queryFactory.select(q.count())
                .from(q)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, count);
    }
}

