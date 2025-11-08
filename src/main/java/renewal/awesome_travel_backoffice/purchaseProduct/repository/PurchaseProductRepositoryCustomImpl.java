package renewal.awesome_travel_backoffice.purchaseProduct.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import renewal.awesome_travel_backoffice.purchaseProduct.dto.request.PurchaseProductSearchCondition;
import renewal.common.entity.PurchaseProduct;

@Repository
public class PurchaseProductRepositoryCustomImpl implements PurchaseProductRepositoryCustom {

    private final EntityManager entityManager;

    public PurchaseProductRepositoryCustomImpl(EntityManager em) {
        this.entityManager = em;
    }

    @Override
    public Page<PurchaseProduct> searchByCondition(PurchaseProductSearchCondition condition, Pageable pageable) {
        // TODO: QueryDSL Q클래스 생성 후 구현
        // 임시로 빈 결과 반환
        List<PurchaseProduct> content = new ArrayList<>();
        long total = 0;
        return new PageImpl<>(content, pageable, total);
    }
}
