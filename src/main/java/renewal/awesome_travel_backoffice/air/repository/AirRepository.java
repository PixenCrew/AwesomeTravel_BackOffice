package renewal.awesome_travel_backoffice.air.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import renewal.awesome_travel_backoffice.air.entity.Air;

public interface AirRepository extends JpaRepository<Air, Long>, JpaSpecificationExecutor<Air>  {
    @Override
    @EntityGraph(attributePaths = "seatClasses", type = EntityGraph.EntityGraphType.LOAD)
    Page<Air> findAll(Specification<Air> spec, Pageable pageable);

    boolean existsByflightNumber(String code);

}
