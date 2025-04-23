package renewal.awesome_travel_backoffice.tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import renewal.awesome_travel_backoffice.tour.entity.Tour;

public interface TourRepo extends JpaRepository<Tour, Long> {
}