package renewal.awesome_travel_backoffice.air.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import renewal.awesome_travel_backoffice.air.entity.AirReservation;

public interface AirReservationRepository extends JpaRepository<AirReservation, Long>{
    List<AirReservation> findAllBySeatClassId(Long seatId);

    List<AirReservation> findByTourId(Long tourId);
}
