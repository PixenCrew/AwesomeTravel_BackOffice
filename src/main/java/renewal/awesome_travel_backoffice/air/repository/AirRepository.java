package renewal.awesome_travel_backoffice.air.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.lang.NonNull;

import renewal.awesome_travel_backoffice.air.entity.Air;

public interface AirRepository extends JpaRepository<Air, Long>, JpaSpecificationExecutor<Air>  {
    @Override
    @EntityGraph(attributePaths = "seatClasses", type = EntityGraph.EntityGraphType.LOAD)
    @NonNull 
    Page<Air> findAll(@Nullable Specification<Air> spec, @NonNull Pageable pageable);

    boolean existsByflightNumber(String code);

}
