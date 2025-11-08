package renewal.awesome_travel_backoffice.airPurchase.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.PurchaseAir;
import renewal.common.entity.PurchaseBase.PurchaseStatus;

public interface PurchaseAirRepository extends JpaRepository<PurchaseAir, Long>, PurchaseAirRepositoryCustom {

    Page<PurchaseAir> findByPurchaseStatusAndPaymentDueDateBefore(
            PurchaseStatus status,
            LocalDateTime time,
            Pageable pageable);

    // 기존 코드 호환성을 위한 메서드 (실제로는 findById와 동일)
    default Optional<PurchaseAir> findByPurchaseProductId(Long productPurchaseId) {
        return findById(productPurchaseId);
    }

    @Query("SELECT ap FROM PurchaseAir ap " +
            "LEFT JOIN FETCH ap.passengers passengers " +
            "WHERE ap.id = :id")
    Optional<PurchaseAir> findByIdWithPassengers(@Param("id") Long id);

}
