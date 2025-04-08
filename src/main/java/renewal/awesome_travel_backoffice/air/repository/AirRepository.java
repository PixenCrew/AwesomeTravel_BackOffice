package renewal.awesome_travel_backoffice.air.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;

import java.util.List;
import java.util.Optional;

public interface AirRepository extends JpaRepository<Air, Long> {
    Optional<Air> findByCode(String code);
    boolean existsByCode(String code);

    List<Air> findAllByStatus(AirStatus status);
}
