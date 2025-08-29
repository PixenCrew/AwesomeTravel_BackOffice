package renewal.awesome_travel_backoffice.airPurchase.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import renewal.awesome_travel_backoffice.airPurchase.utiles.PurchaseStatus;
import renewal.common.entity.AirPurchase;

public interface AirPurchaseRepository extends JpaRepository<AirPurchase, Long>, AirPurchaseRepositoryCustom {

    Page<AirPurchase> findByPurchaseStatusAndPaymentDueDateBefore(
            PurchaseStatus status,
            LocalDateTime time,
            Pageable pageable
    );


}
