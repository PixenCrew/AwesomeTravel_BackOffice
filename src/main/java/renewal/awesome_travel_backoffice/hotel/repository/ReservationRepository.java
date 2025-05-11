package renewal.awesome_travel_backoffice.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import renewal.awesome_travel_backoffice.hotel.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByHotelId(Long id);
}
