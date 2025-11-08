package renewal.awesome_travel_backoffice.purchaseProduct.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import renewal.awesome_travel_backoffice.purchaseProduct.dto.request.PurchaseProductSearchCondition;
import renewal.common.entity.PurchaseProduct;

public interface PurchaseProductRepositoryCustom {
    Page<PurchaseProduct> searchByCondition(PurchaseProductSearchCondition condition, Pageable pageable);
}
