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
import renewal.awesome_travel_backoffice.air.dto.request.AirRequestDto;
import renewal.awesome_travel_backoffice.air.dto.request.AirSearchRequestDto;
import renewal.awesome_travel_backoffice.air.dto.request.SeatClassRequestDto;
import renewal.awesome_travel_backoffice.air.dto.response.AirResponseDto;
import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.air.entity.Airline;
import renewal.awesome_travel_backoffice.air.entity.SeatClass;
import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.awesome_travel_backoffice.air.repository.AirRepositoryCustomImpl;
import renewal.awesome_travel_backoffice.air.repository.AirSpecification;
import renewal.awesome_travel_backoffice.air.repository.AirlineRepository;
import renewal.awesome_travel_backoffice.air.repository.SeatClassRepository;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;

@Service
@RequiredArgsConstructor
public class AirService {

    private final AirRepository airRepository;
    private final AirlineRepository airlineRepository;
    private final SeatClassRepository seatClassRepository;

    private final AirRepositoryCustomImpl airRepositoryCustom;

    @Transactional
    public void createAir(Air air) {
        if (airRepository.existsByCode(air.getCode())) {
            throw new IllegalArgumentException("중복된 항공편 코드입니다.");
        }

        airlineRepository
                .findByCode(air.getAirline().getCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항공사입니다."));

        // Air air = new Air(
        // dto.getCode(),
        // airline,
        // dto.getDepart(),
        // dto.getDepartTime(),
        // dto.getArrive(),
        // dto.getArriveTime(),
        // dto.getStopovers(),
        // dto.getFlightType()
        // );

        // for (SeatClassRequestDto seatDto : dto.getSeatClasses()) {
        // SeatClass seat = new SeatClass(
        // seatDto.getClassType(),
        // seatDto.getPrice(),
        // seatDto.getMaxSeats(),
        // seatDto.getAvailableSeats()
        // );
        // seat.setAir(air);
        // air.addSeatClass(seat);
        // }

        for (SeatClass seat : air.getSeatClasses()) {
            seat.setAir(air);
        }

        airRepository.save(air);
        // Air saved = airRepository.save(air);
        // return toDto(saved);
    }
    public List<String> getAllCompanies() {
            return airlineRepository.findDistinctAirlines();
    }
    public Page<AirResponseDto> getAirList(AirSearchRequestDto req) {
        return airRepositoryCustom.searchAdminAirList(req);
    }

    @Transactional
    public AirResponseDto updateAir(Long id, AirRequestDto dto) {
        Air air = airRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 항공편이 존재하지 않습니다."));

        Airline airline = airlineRepository.findByCode(dto.getAirline().getCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항공사입니다."));

        air.updateAir(
                dto.getCode(),
                airline,
                dto.getDepart(),
                dto.getDepartTime(),
                dto.getArrive(),
                dto.getArriveTime(),
                dto.getStopovers(),
                dto.getFlightType());

        air.clearSeatClasses();
        for (SeatClassRequestDto seatDto : dto.getSeatClasses()) {
            SeatClass seat = new SeatClass(
                    air,
                    seatDto.getClassType(),
                    seatDto.getPrice(),
                    seatDto.getMaxSeats(),
                    seatDto.getAvailableSeats());
            air.addSeatClass(seat);
        }

        return toDto(air);
    }

    @Transactional
    public AirResponseDto updateDetails(Long id, AirRequestDto dto) {
        Air air = airRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 항공편이 존재하지 않습니다."));
        if (dto.getCode() != null) {
            air.setCode(dto.getCode());
        }
        if (dto.getAirline().getCode() != null) {
            Airline airline = airlineRepository.findByCode(dto.getAirline().getCode())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항공사입니다."));
            air.setAirline(airline);
        }
        if (dto.getDepart() != null) {
            air.setDepart(dto.getDepart());
        }
        if (dto.getDepartTime() != null) {
            air.setDepart_time(dto.getDepartTime());
        }
        if (dto.getArrive() != null) {
            air.setArrive(dto.getArrive());
        }
        if (dto.getArriveTime() != null) {
            air.setArrive_time(dto.getArriveTime());
        }
        if (dto.getStopovers() != null) {
            air.setStopovers(dto.getStopovers());
        }
        if (dto.getFlightType() != null) {
            air.setFlightType(dto.getFlightType());
        }
        if (dto.getSeatClasses() != null) {
            for (SeatClassRequestDto seatDto : dto.getSeatClasses()) {
                if (seatDto.getId() != null) {
                    // 기존 seat 수정
                    SeatClass seat = seatClassRepository.findById(seatDto.getId())
                            .orElseThrow(() -> new IllegalArgumentException("SeatClass 없음"));
                    seat.update(
                            seatDto.getClassType(),
                            seatDto.getPrice(),
                            seatDto.getMaxSeats(),
                            seatDto.getAvailableSeats());
                } else {
                    // 새로운 seat 추가
                    SeatClass seat = new SeatClass(
                            air,
                            seatDto.getClassType(),
                            seatDto.getPrice(),
                            seatDto.getMaxSeats(),
                            seatDto.getAvailableSeats());
                    air.addSeatClass(seat);
                }
            }
        }

        return toDto(air);
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

    private AirResponseDto toDto(Air air) {
        return AirResponseDto.builder()
                .id(air.getId())
                .code(air.getCode())
                .airlineCode(air.getAirline().getCode())
                .airlineNameKor(air.getAirline().getNameKor())
                .airlineNameEng(air.getAirline().getNameEng())
                .depart(air.getDepart())
                .departTime(air.getDepart_time())
                .arrive(air.getArrive())
                .arriveTime(air.getArrive_time())
                .stopovers(air.getStopovers())
                .flightType(air.getFlightType())
                .status(air.getStatus())
                .seatClasses(air.getSeatClasses())
                .build();
    }

    public Page<Air> searchAirs(AirFilterDTO filter, Pageable pageable) {

        Specification<Air> spec = Specification.where(null);

        // code LIKE %code%
        if (StringUtils.hasText(filter.getCode())) {
            spec = spec.and(AirSpecification.codeContains(filter.getCode()));
        }

        // airline IN (...)
        if (filter.getArilines() != null && !filter.getArilines().isEmpty()) {
            spec = spec.and(AirSpecification.airlinesIn(filter.getArilines()));
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
        if (StringUtils.hasText(filter.getDepart())) {
            spec = spec.and(AirSpecification.departEquals(filter.getDepart()));
        }

        // arrive == value
        if (StringUtils.hasText(filter.getArrive())) {
            spec = spec.and(AirSpecification.arriveEquals(filter.getArrive()));
        }

        // stopovers == value
        if (filter.getStopovers() != null) {
            spec = spec.and(AirSpecification.stopoversEquals(filter.getStopovers()));
        }

        // // seatCount BETWEEN min AND max
        // if (filter.getStartCount() != null || filter.getEndCount() != null) {
        //     spec = spec.and(AirSpecification.seatCountBetween(
        //         filter.getStartCount(), filter.getEndCount()));
        // }

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

        return airRepository.findAll(spec, pageable);
    }
}
