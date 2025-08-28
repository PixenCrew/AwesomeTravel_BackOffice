package renewal.awesome_travel_backoffice.hotel.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import renewal.awesome_travel_backoffice.hotel.dto.HotelFilterDTO;
import renewal.awesome_travel_backoffice.hotel.entity.Hotel;
import renewal.awesome_travel_backoffice.hotel.repository.HotelRepository;
import renewal.awesome_travel_backoffice.hotel.repository.HotelSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepo;

    public Page<Hotel> searchHotels(HotelFilterDTO filter, Pageable pageable) {

        Specification<Hotel> spec = Specification.where(null);

        if (filter.getName() != null && !filter.getName().isBlank()) {
            spec = spec.and(HotelSpecification.nameContains(filter.getName()));
        }
        if (filter.getCity() != null && !filter.getCity().isBlank()) {
            spec = spec.and(HotelSpecification.cityEquals(filter.getCity()));
        }
        if (filter.getAddress() != null && !filter.getAddress().isBlank()) {
            spec = spec.and(HotelSpecification.addressContains(filter.getAddress()));
        }
        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            spec = spec.and(HotelSpecification.emailContains(filter.getEmail()));
        }
        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            spec = spec.and(HotelSpecification.priceBetween(filter.getMinPrice(), filter.getMaxPrice()));
        }

        if (filter.getHotelType() != null) {
            spec = spec.and(HotelSpecification.hotelTypeEquals(filter.getHotelType()));
        }
        if (filter.getIsActive() != null) {
            spec = spec.and(HotelSpecification.isActiveEquals(filter.getIsActive()));
        }

        // 기간 예약 조회 (3개 값이 모두 있어야 작동)
        if (filter.getStartDate() != null && filter.getEndDate() != null && filter.getRequiredPersons() != null) {
            spec = spec.and(
                    HotelSpecification.availableBetweenAndCapacity(
                            filter.getStartDate(),
                            filter.getEndDate(),
                            filter.getRequiredPersons()));
        }

        // 특정일 예약 조회 (2개 값이 모두 있어야 작동)
        if (filter.getTargetDate() != null && filter.getRequiredPersons() != null) {
            spec = spec.and(
                    HotelSpecification.availableOnDateAndRooms(
                            filter.getTargetDate(),
                            filter.getRequiredPersons()));
        }

        return hotelRepo.findAll(spec, pageable);
    }

    public List<String> getAllEmails() {
        return hotelRepo.findAll()
                .stream()
                .map(Hotel::getEmail)
                .distinct()
                .toList();
    }
}
