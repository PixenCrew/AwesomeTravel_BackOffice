package renewal.awesome_travel_backoffice.hotel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.hotel.dto.HotelFilterDTO;
import renewal.awesome_travel_backoffice.hotel.entity.Hotel;
import renewal.awesome_travel_backoffice.hotel.repository.HotelRepository;
import renewal.awesome_travel_backoffice.hotel.repository.HotelSpecification;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepo;

    public Page<Hotel> searchHotels(HotelFilterDTO filter, Pageable pageable) {

        Specification<Hotel> spec = Specification.where(null);

        if (filter.getName() != null && !filter.getName().isBlank()) {
            // predicates = builder.and(predicates, builder.like(root.get("name"), "%" +
            // filter.getName() + "%"));
            spec = spec.and(HotelSpecification.nameContains(filter.getName()));
        }
        if (filter.getAddress() != null && !filter.getAddress().isBlank()) {
            // predicates = builder.and(predicates, builder.like(root.get("address"), "%" +
            // filter.getAddress() + "%"));
            spec = spec.and(HotelSpecification.addressContains(filter.getAddress()));
        }
        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            // predicates = builder.and(predicates, builder.like(root.get("email"), "%" +
            // filter.getEmail() + "%"));
            spec = spec.and(HotelSpecification.emailContains(filter.getEmail()));
        }
        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            // predicates = builder.and(predicates,
            // builder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            spec = spec.and(HotelSpecification.priceBetween(filter.getMinPrice(), filter.getMaxPrice()));
        }
        // if (filter.getMaxPrice() != null) {
        // predicates = builder.and(predicates,
        // builder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
        // }
        if (filter.getHotelType() != null) {
            // predicates = builder.and(predicates, builder.equal(root.get("hotelType"),
            // filter.getHotelType()));
            spec = spec.and(HotelSpecification.hotelTypeEquals(filter.getHotelType()));
        }
        if (filter.getIsActive() != null) {
            // predicates = builder.and(predicates, builder.equal(root.get("isActive"),
            // filter.getIsActive()));
            spec = spec.and(HotelSpecification.isActiveEquals(filter.getIsActive()));
        }

        // 예약 조회 (3개 값이 모두 있어야 작동)
        if (filter.getStartDate() != null && filter.getEndDate() != null && filter.getRequiredPersons() != null) {
            spec = spec.and(
                    HotelSpecification.availableBetweenAndCapacity(
                            filter.getStartDate(),
                            filter.getEndDate(),
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
