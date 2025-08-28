package renewal.awesome_travel_backoffice.airPurchase.repository;

import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import renewal.awesome_travel_backoffice.airPurchase.dto.request.AirPurchaseSearchCondition;
import renewal.awesome_travel_backoffice.airPurchase.entity.AirPurchase;
import renewal.awesome_travel_backoffice.airPurchase.entity.QAirPurchase;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AirPurchaseRepositoryCustomImpl implements AirPurchaseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AirPurchase> searchByCondition(AirPurchaseSearchCondition cond, Pageable pageable) {
        QAirPurchase q = QAirPurchase.airPurchase;

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

        List<AirPurchase> results = queryFactory.selectFrom(q)
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

