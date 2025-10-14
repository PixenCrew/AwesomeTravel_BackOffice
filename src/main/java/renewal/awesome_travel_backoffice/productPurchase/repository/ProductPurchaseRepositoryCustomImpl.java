package renewal.awesome_travel_backoffice.productPurchase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import renewal.awesome_travel_backoffice.productPurchase.dto.request.ProductPurchaseSearchCondition;
import renewal.common.entity.PurchaseProduct;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductPurchaseRepositoryCustomImpl implements ProductPurchaseRepositoryCustom {

    private final EntityManager entityManager;

    public ProductPurchaseRepositoryCustomImpl(EntityManager em) {
        this.entityManager = em;
    }

    @Override
    public Page<PurchaseProduct> searchByCondition(ProductPurchaseSearchCondition condition, Pageable pageable) {
        // TODO: QueryDSL Q클래스 생성 후 구현
        // 임시로 빈 결과 반환
        List<PurchaseProduct> content = new ArrayList<>();
        long total = 0;
        return new PageImpl<>(content, pageable, total);
    }
}
