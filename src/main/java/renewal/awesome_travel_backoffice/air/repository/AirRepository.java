package renewal.awesome_travel_backoffice.air.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;

import java.util.List;
import java.util.Optional;

public interface AirRepository extends JpaRepository<Air, Long>, JpaSpecificationExecutor<Air>  {
    Optional<Air> findByCode(String code);
    boolean existsByCode(String code);
    List<Air> findAllByStatus(AirStatus status);

    @Override
    @EntityGraph(attributePaths = "seatClasses", type = EntityGraph.EntityGraphType.LOAD)
    Page<Air> findAll(Specification<Air> spec, Pageable pageable);

}
