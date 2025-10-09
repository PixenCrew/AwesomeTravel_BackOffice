package renewal.awesome_travel_backoffice.productPurchase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import renewal.awesome_travel_backoffice.productPurchase.dto.request.ProductPurchaseSearchCondition;
import renewal.common.entity.ProductPurchase;

public interface ProductPurchaseRepositoryCustom {
    Page<ProductPurchase> searchByCondition(ProductPurchaseSearchCondition condition, Pageable pageable);
}



