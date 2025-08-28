package renewal.awesome_travel_backoffice.airPurchase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import renewal.awesome_travel_backoffice.airPurchase.dto.request.AirPurchaseSearchCondition;
import renewal.awesome_travel_backoffice.airPurchase.entity.AirPurchase;

public interface AirPurchaseRepositoryCustom {
    Page<AirPurchase> searchByCondition(AirPurchaseSearchCondition condition, Pageable pageable);
}
