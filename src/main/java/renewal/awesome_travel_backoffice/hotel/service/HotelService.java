package renewal.awesome_travel_backoffice.hotel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import renewal.awesome_travel_backoffice.hotel.dto.HotelFilterDTO;
import renewal.awesome_travel_backoffice.hotel.entity.Hotel;
import renewal.awesome_travel_backoffice.hotel.repository.HotelRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepo;

    public Page<Hotel> searchHotels(HotelFilterDTO filter, Pageable pageable) {
        // 예시로 Specification 방식 사용
        return hotelRepo.findAll((root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates = cb.and(predicates, cb.like(root.get("name"), "%" + filter.getName() + "%"));
            }
            if (filter.getAddress() != null && !filter.getAddress().isBlank()) {
                predicates = cb.and(predicates, cb.like(root.get("address"), "%" + filter.getAddress() + "%"));
            }
            if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
                predicates = cb.and(predicates, cb.like(root.get("email"), "%" + filter.getEmail() + "%"));
            }
            if (filter.getMinPrice() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }
            if (filter.getHotelType() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("hotelType"), filter.getHotelType()));
            }
            if (filter.getIsActive() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("isActive"), filter.getIsActive()));
            }

            return predicates;
        }, pageable);
    }

    public List<String> getAllEmails() {
        return hotelRepo.findAll()
                .stream()
                .map(Hotel::getEmail)
                .distinct()
                .toList();
    }
}
