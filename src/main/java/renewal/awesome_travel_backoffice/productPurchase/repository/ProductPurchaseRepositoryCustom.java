package renewal.awesome_travel_backoffice.productPurchase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import renewal.awesome_travel_backoffice.productPurchase.dto.request.ProductPurchaseSearchCondition;
import renewal.common.entity.PurchaseProduct;

public interface ProductPurchaseRepositoryCustom {
    Page<PurchaseProduct> searchByCondition(ProductPurchaseSearchCondition condition, Pageable pageable);
}



