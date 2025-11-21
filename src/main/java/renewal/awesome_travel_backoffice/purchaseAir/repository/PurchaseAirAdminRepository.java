package renewal.awesome_travel_backoffice.purchaseAir.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.PurchaseAir;

public interface PurchaseAirAdminRepository extends JpaRepository<PurchaseAir, Long>, JpaSpecificationExecutor<PurchaseAir> {

    @Query("SELECT ap FROM PurchaseAir ap " +
            "LEFT JOIN FETCH ap.passengers passengers " +
            "WHERE ap.id = :id")
    Optional<PurchaseAir> findByIdWithPassengers(@Param("id") Long id);
    
    @Query("SELECT DISTINCT ap FROM PurchaseAir ap " +
            "LEFT JOIN FETCH ap.finalSeatClasses " +
            "WHERE ap.id = :id")
    Optional<PurchaseAir> findByIdWithSeatClasses(@Param("id") Long id);

}

