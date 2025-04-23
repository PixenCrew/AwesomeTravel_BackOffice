package renewal.awesome_travel_backoffice.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import renewal.awesome_travel_backoffice.entity.Tour;

public interface TourRepo extends JpaRepository<Tour, Long> {
}