package renewal.awesome_travel_backoffice.airPurchase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import renewal.awesome_travel_backoffice.airPurchase.dto.request.PurchaseAirSearchCondition;
import renewal.common.entity.PurchaseAir;

public interface PurchaseAirRepositoryCustom {
    Page<PurchaseAir> searchByCondition(PurchaseAirSearchCondition condition, Pageable pageable);
}
