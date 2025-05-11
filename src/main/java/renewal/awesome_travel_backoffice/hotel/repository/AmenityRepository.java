package renewal.awesome_travel_backoffice.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import renewal.awesome_travel_backoffice.hotel.entity.Amenity;

public interface AmenityRepository  extends JpaRepository<Amenity, Long> {
}
