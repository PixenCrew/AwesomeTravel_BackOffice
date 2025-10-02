package renewal.awesome_travel_backoffice.tour;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// import renewal.awesome_travel_backoffice.hotel.repository.HotelReservationRepository;
import renewal.awesome_travel_backoffice.tour.dto.TourFilterDTO;
import renewal.awesome_travel_backoffice.tour.repository.TourRepository;
import renewal.awesome_travel_backoffice.tour.repository.TourSpecification;
// import renewal.common.entity.HotelReservation;
// import renewal.common.entity.HotelReservation.HotelReservationStatus;
import renewal.common.entity.Tour;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;
    // private final HotelReservationRepository hotelReservationRepo;
    
    public List<String> getAllCompanies() {
        return tourRepository.findDistinctCompanies();
    }

    public Page<Tour> searchTours(TourFilterDTO filter, Pageable pageable) {
        Specification<Tour> spec = Specification.where(TourSpecification.withProductJoin()); // 기본적으로 Product와 JOIN

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
        if (filter.getPointLocation() != null && !filter.getPointLocation().isEmpty()) {
            spec = spec.and(TourSpecification.scheduleLocationContains(filter.getPointLocation()));
        }
        if (filter.getCity() != null && !filter.getCity().isEmpty()) {
            spec = spec.and(TourSpecification.cityContains(filter.getCity()));
        }
        if (filter.getStartCount() != null || filter.getEndCount() != null) {
            spec = spec.and(TourSpecification.maxCapacityBetween(filter.getStartCount(), filter.getEndCount()));
        }
        if (filter.isFindOrphan()) {
            spec = spec.and(TourSpecification.productIsEmptyOrNull());
        }

        return tourRepository.findAll(spec, pageable);
    }

    // public void cancelHotelAir(Long tourId) throws Exception{

    //     // 연결된 Air, Hotel 예약 CANCELED로 변경
    //     List<AirReservation> airReserves = airReservationRepo.findByTourId(tourId);
    //     for (AirReservation reserve : airReserves) {
    //         reserve.setStatus(AirReservationStatus.CANCELLED);
    //         // SeatClass 잔여좌석 복원
    //         reserve.getSeatClass().cancelSeats(reserve.getSeatCount());
    //     }
    //     airReservationRepo.saveAll(airReserves);

    //     List<HotelReservation> hotelReserves = hotelReservationRepo.findByTourId(tourId);
    //     for (HotelReservation reserve : hotelReserves) {
    //         reserve.setStatus(HotelReservationStatus.CANCELLED);
    //     }
    //     hotelReservationRepo.saveAll(hotelReserves);
    // }

    // public void cancelHotel(Long tourId) throws Exception{

    //     // 연결된 Hotel 예약 CANCELED로 변경
    //     List<HotelReservation> hotelReserves = hotelReservationRepo.findByTourId(tourId);
    //     for (HotelReservation reserve : hotelReserves) {
    //         reserve.setStatus(HotelReservationStatus.CANCELLED);
    //     }
    //     hotelReservationRepo.saveAll(hotelReserves);

    // }

}
