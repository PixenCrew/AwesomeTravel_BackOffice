package renewal.awesome_travel_backoffice.tour;

// TourService.java
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import renewal.awesome_travel_backoffice.tour.dto.TourFilterDTO;
import renewal.awesome_travel_backoffice.tour.entity.Tour;
import renewal.awesome_travel_backoffice.tour.repository.TourRepository;
import renewal.awesome_travel_backoffice.tour.repository.TourSpecification;

@Service
public class TourService {

    @Autowired
    private TourRepository tourRepository;

    public List<String> getAllCompanies() {
        return tourRepository.findDistinctCompanies();
    }

    public Page<Tour> searchTours(TourFilterDTO filter, Pageable pageable) {
        Specification<Tour> spec = Specification.where(null);

        if (filter.getName() != null && !filter.getName().isEmpty()) {
            spec = spec.and(TourSpecification.nameContains(filter.getName()));
        }
        if (filter.getCompanies() != null && !filter.getCompanies().isEmpty()) {
            spec = spec.and(TourSpecification.companyIn(filter.getCompanies()));
        }
        if (filter.getStartDateFrom() != null || filter.getStartDateTo() != null) {
            spec = spec.and(TourSpecification.startDateBetween(filter.getStartDateFrom(), filter.getStartDateTo()));
        }
        if (filter.getEndDateFrom() != null || filter.getEndDateTo() != null) {
            spec = spec.and(TourSpecification.endDateBetween(filter.getEndDateFrom(), filter.getEndDateTo()));
        }
        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            spec = spec.and(TourSpecification.priceBetween(filter.getMinPrice(), filter.getMaxPrice()));
        }
        if (filter.getCourseLocation() != null && !filter.getCourseLocation().isEmpty()) {
            spec = spec.and(TourSpecification.courseLocationContains(filter.getCourseLocation()));
        }
        if (filter.getCountry() != null && !filter.getCountry().isEmpty()) {
            spec = spec.and(TourSpecification.countryContains(filter.getCountry()));
        }
        if (filter.getStartCount() != null || filter.getEndCount() != null) {
            spec = spec.and(TourSpecification.countBetween(filter.getStartCount(), filter.getEndCount()));
        }

        return tourRepository.findAll(spec, pageable);
    }

}
