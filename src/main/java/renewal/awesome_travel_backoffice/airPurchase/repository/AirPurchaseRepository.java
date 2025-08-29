package renewal.awesome_travel_backoffice.airPurchase.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import renewal.common.entity.AirPurchase;
import renewal.common.entity.BasePurchase.PurchaseStatus;

public interface AirPurchaseRepository extends JpaRepository<AirPurchase, Long>, AirPurchaseRepositoryCustom {

    Page<AirPurchase> findByPurchaseStatusAndPaymentDueDateBefore(
            PurchaseStatus status,
            LocalDateTime time,
            Pageable pageable
    );


}
