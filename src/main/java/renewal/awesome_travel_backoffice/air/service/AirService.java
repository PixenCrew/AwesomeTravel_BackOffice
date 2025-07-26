package renewal.awesome_travel_backoffice.air.service;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import renewal.awesome_travel_backoffice.air.dto.AirFilterDTO;
import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.air.entity.SeatClass;
import renewal.awesome_travel_backoffice.air.repository.AirRepository;
// import renewal.awesome_travel_backoffice.air.repository.AirRepositoryCustomImpl;
import renewal.awesome_travel_backoffice.air.repository.AirSpecification;
import renewal.awesome_travel_backoffice.air.repository.AirlineRepository;
import renewal.awesome_travel_backoffice.air.repository.SeatClassRepository;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;

@Service
@RequiredArgsConstructor
public class AirService {

    private final SeatClassRepository seatClassRepository;
    private final AirRepository airRepository;
    private final AirlineRepository airlineRepository;

    // private final AirRepositoryCustomImpl airRepositoryCustom;

    @Transactional
    public void createAir(Air air) {
        if (airRepository.existsByflightNumber(air.getAirline().getCode())) {
            throw new IllegalArgumentException("중복된 항공편 코드입니다.");
        }

        airlineRepository
                .findByCode(air.getAirline().getCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항공사입니다."));

        for (SeatClass seat : air.getSeatClasses()) {
            seat.setAir(air);
        }

        airRepository.save(air);
    }
    public List<String> getAllCompanies() {
            return airlineRepository.findDistinctAirlines();
    }

    @Transactional
    public void changeStatus(Long id, AirStatus status) {
        Air air = airRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 항공편이 존재하지 않습니다."));
        air.setStatus(status);
    }

    @Transactional
    public void deleteAir(Long id) {
        Air air = airRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 항공편이 존재하지 않습니다."));
        airRepository.delete(air);
    }

    public Page<SeatClass> searchAirs(AirFilterDTO filter, Pageable pageable) {

        Specification<SeatClass> spec = Specification.where(null);

        // seatClassType LIKE %seatClassType%
        if (filter.getSeatClassType() != null) {
            spec = spec.and(AirSpecification.seatClassEquals(filter.getSeatClassType()));
        }

        // code LIKE %code%
        if (StringUtils.hasText(filter.getCode())) {
            spec = spec.and(AirSpecification.codeContains(filter.getCode()));
        }

        // airline IN (...)
        if (filter.getAirlines() != null && !filter.getAirlines().isEmpty()) {
            spec = spec.and(AirSpecification.airlinesIn(filter.getAirlines()));
        }

        // infantSeatsRequired
        if (filter.getInfantSeatsRequired() != null) {
            spec = spec.and(AirSpecification.getInfantSeatsRequired(filter.getInfantSeatsRequired()));
        }

        // departDate BETWEEN from AND to
        if (filter.getDepartDateFrom() != null || filter.getDepartDateTo() != null) {
            spec = spec.and(AirSpecification.departDateBetween(
                filter.getDepartDateFrom(), filter.getDepartDateTo()));
        }

        // arriveDate BETWEEN from AND to
        if (filter.getArriveDateFrom() != null || filter.getArriveDateTo() != null) {
            spec = spec.and(AirSpecification.arriveDateBetween(
                filter.getArriveDateFrom(), filter.getArriveDateTo()));
        }

        // depart == value
        if (StringUtils.hasText(filter.getDepartAirport())) {
            spec = spec.and(AirSpecification.departEquals(filter.getDepartAirport()));
        }

        // arrive == value
        if (StringUtils.hasText(filter.getArriveAirport())) {
            spec = spec.and(AirSpecification.arriveEquals(filter.getArriveAirport()));
        }

        // // stopovers == value
        // if (filter.getStopovers() != null) {
        //     spec = spec.and(AirSpecification.stopoversEquals(filter.getStopovers()));
        // }

        // stopovers BETWEEN from AND to
        if (filter.getMinStopovers() != null || filter.getMaxStopovers() != null) {
            spec = spec.and(AirSpecification.stopoversBetween(
                filter.getMinStopovers(), filter.getMaxStopovers()));
        }

        // flightType == value
        if (filter.getFlightType() != null) {
            spec = spec.and(AirSpecification.flightTypeEquals(filter.getFlightType()));
        }

        // status == value
        if (filter.getStatus() != null) {
            spec = spec.and(AirSpecification.statusEquals(filter.getStatus()));
        }

        // seatClasses.price BETWEEN min AND max
        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            spec = spec.and(AirSpecification.priceBetween(
                filter.getMinPrice(), filter.getMaxPrice()));   
        }

        // seatClasses.availableSeats min
        if (filter.getAvailableSeats() != null) {
            spec = spec.and(AirSpecification.availableSeatsMore(filter.getAvailableSeats()));
        }

        return seatClassRepository.findAll(spec, pageable);
    }
}
