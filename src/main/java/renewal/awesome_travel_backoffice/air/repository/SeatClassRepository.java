package renewal.awesome_travel_backoffice.air.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import renewal.awesome_travel_backoffice.air.entity.SeatClass;

public interface SeatClassRepository extends JpaRepository<SeatClass, Long>, JpaSpecificationExecutor<SeatClass>   {
    Page<SeatClass> findAll(Specification<SeatClass> spec, Pageable pageable);

}
