package renewal.awesome_travel_backoffice.airPurchase.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;
import renewal.awesome_travel_backoffice.airPurchase.dto.request.PurchaseAirSearchCondition;
import renewal.common.entity.PurchaseAir;

public class PurchaseAirRepositoryCustomImpl implements PurchaseAirRepositoryCustom {

    private final EntityManager entityManager;

    public PurchaseAirRepositoryCustomImpl(EntityManager em) {
        this.entityManager = em;
    }

    @Override
    public Page<PurchaseAir> searchByCondition(PurchaseAirSearchCondition cond, Pageable pageable) {
        // TODO: QueryDSL Q클래스 생성 후 구현
        // 임시로 빈 결과 반환
        List<PurchaseAir> content = new ArrayList<>();
        long total = 0;
        return new PageImpl<>(content, pageable, total);
    }
}
