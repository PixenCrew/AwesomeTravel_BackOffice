package renewal.awesome_travel_backoffice.airPurchase.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import renewal.common.entity.PurchaseAir;
import renewal.common.entity.PurchaseBase.PurchaseStatus;

public interface PurchaseAirRepository extends JpaRepository<PurchaseAir, Long>, PurchaseAirRepositoryCustom {

    Page<PurchaseAir> findByPurchaseStatusAndPaymentDueDateBefore(
            PurchaseStatus status,
            LocalDateTime time,
            Pageable pageable
    );


}
