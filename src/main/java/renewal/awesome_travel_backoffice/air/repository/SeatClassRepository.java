package renewal.awesome_travel_backoffice.air.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import renewal.awesome_travel_backoffice.air.entity.SeatClass;

import java.util.List;

public interface SeatClassRepository extends JpaRepository<SeatClass, Long> {
    List<SeatClass> findByAir_Id(Long airId);
}
