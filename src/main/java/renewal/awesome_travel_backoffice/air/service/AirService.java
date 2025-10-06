package renewal.awesome_travel_backoffice.air.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import renewal.awesome_travel_backoffice.air.dto.AirFilterDTO;
import renewal.common.entity.Air;
import renewal.common.entity.CityCode;
import renewal.common.entity.Air.AirStatus;
import renewal.common.entity.Air.FlightSegment;
import renewal.common.entity.SeatClass;
import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.awesome_travel_backoffice.air.repository.AirSpecification;
import renewal.awesome_travel_backoffice.air.repository.AirlineRepository;
import renewal.awesome_travel_backoffice.air.repository.SeatClassRepository;

import lombok.RequiredArgsConstructor;

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

        // 1. 전체 소요시간 계산
        air.setFlightDuration(
            calcDuration(air.getDepartDateTime(), air.getDepartAirport(), air.getArriveDateTime(), air.getArriveAirport())
            );

        // 2. 각 segment별 소요시간 계산
        for (FlightSegment segment : air.getFlightSegments()) {
            segment.setFlightDuration(
                calcDuration(segment.getDepartDateTime(), segment.getDepartAirport(), segment.getArriveDateTime(), segment.getArriveAirport())
                );
        }

        // 3. segment 사이 대기시간 계산
        List<FlightSegment> segments = air.getFlightSegments();

        for (int i = 0; i < segments.size()-1; i++) {
            FlightSegment currentSegment = segments.get(i);
            LocalDateTime currentArriveTime = currentSegment.getArriveDateTime();
            CityCode currentArriveAirport = currentSegment.getArriveAirport();
            
            FlightSegment nextSegment = segments.get(i+1);
            LocalDateTime nextDepartTime = nextSegment.getDepartDateTime();
            CityCode nextDepartAirport = nextSegment.getDepartAirport();

            currentSegment.setWaitDuration(calcDuration(currentArriveTime, currentArriveAirport, nextDepartTime, nextDepartAirport));
        }

        airRepository.save(air);
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

        // departDateTime BETWEEN from AND to
        if (filter.getDepartDateTimeFrom() != null || filter.getDepartDateTimeTo() != null) {
            spec = spec.and(AirSpecification.departDateTimeBetween(
                filter.getDepartDateTimeFrom(), filter.getDepartDateTimeTo()));
        }

        // arriveDate BETWEEN from AND to
        if (filter.getArriveDateFrom() != null || filter.getArriveDateTo() != null) {
            spec = spec.and(AirSpecification.arriveDateBetween(
                filter.getArriveDateFrom(), filter.getArriveDateTo()));
        }

        // depart == value
        if (filter.getDepartAirport() != null) {
            spec = spec.and(AirSpecification.departEquals(filter.getDepartAirport()));
        }

        // arrive == value
        if (filter.getDepartAirport() != null) {
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

    // (시간+도시코드)로 소요시간[분] 계산
    public Long calcDuration(LocalDateTime departTime, CityCode departCode, LocalDateTime arriveTime, CityCode arriveCode){
        ZoneOffset departOffset = ZoneOffset.ofTotalSeconds((int) (departCode.getUtcOffsetMins() * 60));
        ZoneOffset arriveOffset = ZoneOffset.ofTotalSeconds((int) (arriveCode.getUtcOffsetMins() * 60));

        Instant departUtc = departTime.toInstant(departOffset);
        Instant arriveUtc = arriveTime.toInstant(arriveOffset);
        
        Long result = Duration.between(departUtc, arriveUtc).toMinutes();
        return result;
    }
}
