package renewal.awesome_travel_backoffice.air.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import renewal.common.entity.Air;
import renewal.common.entity.SeatClass;
import renewal.common.entity.SeatClass.SeatClassType;

public interface SeatClassAdminRepository extends JpaRepository<SeatClass, Long>, JpaSpecificationExecutor<SeatClass> {
    @NonNull Page<SeatClass> findAll(@Nullable Specification<SeatClass> spec, @NonNull Pageable pageable);
    
    Optional<SeatClass> findByAirAndClassType(Air air, SeatClassType classType);

}

