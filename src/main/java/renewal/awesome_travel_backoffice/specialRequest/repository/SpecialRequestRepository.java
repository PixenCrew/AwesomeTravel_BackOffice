package renewal.awesome_travel_backoffice.specialRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import renewal.awesome_travel_backoffice.specialRequest.entity.SpecialRequest;

public interface SpecialRequestRepository extends JpaRepository<SpecialRequest, Long> {
}
