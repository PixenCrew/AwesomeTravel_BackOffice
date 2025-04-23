package renewal.awesome_travel_backoffice.air.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.air.dto.request.AirRequestDto;
import renewal.awesome_travel_backoffice.air.dto.request.AirSearchRequestDto;
import renewal.awesome_travel_backoffice.air.dto.request.SeatClassRequestDto;
import renewal.awesome_travel_backoffice.air.dto.response.AirResponseDto;
import renewal.awesome_travel_backoffice.air.dto.response.SeatClassResponseDto;
import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.air.entity.Airline;
import renewal.awesome_travel_backoffice.air.entity.SeatClass;
import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.awesome_travel_backoffice.air.repository.AirRepositoryCustomImpl;
import renewal.awesome_travel_backoffice.air.repository.AirlineRepository;
import renewal.awesome_travel_backoffice.air.repository.SeatClassRepository;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirService {

    private final AirRepository airRepository;
    private final AirlineRepository airlineRepository;
    private final SeatClassRepository seatClassRepository;

    private final AirRepositoryCustomImpl airRepositoryCustom;

    @Transactional
    public AirResponseDto createAir(AirRequestDto dto) {
        if (airRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("중복된 항공편 코드입니다.");
        }

        Airline airline = airlineRepository.findByCode(dto.getAirlineCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항공사입니다."));

        Air air = new Air(
                dto.getCode(),
                airline,
                dto.getDepart(),
                dto.getDepartTime(),
                dto.getArrive(),
                dto.getArriveTime(),
                dto.getStopovers(),
                dto.getFlightType()
        );

        for (SeatClassRequestDto seatDto : dto.getSeatClasses()) {
            SeatClass seat = new SeatClass(
                    seatDto.getClassType(),
                    seatDto.getPrice(),
                    seatDto.getMaxSeats(),
                    seatDto.getAvailableSeats()
            );
            seat.setAir(air);
            air.addSeatClass(seat);
        }

        Air saved = airRepository.save(air);
        return toDto(saved);
    }

    public Page<AirResponseDto> getAirList(AirSearchRequestDto req) {
        return airRepositoryCustom.searchAdminAirList(req);
    }
    @Transactional
    public AirResponseDto updateAir(Long id, AirRequestDto dto) {
        Air air = airRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 항공편이 존재하지 않습니다."));

        Airline airline = airlineRepository.findByCode(dto.getAirlineCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항공사입니다."));

        air.updateAir(
                dto.getCode(),
                airline,
                dto.getDepart(),
                dto.getDepartTime(),
                dto.getArrive(),
                dto.getArriveTime(),
                dto.getStopovers(),
                dto.getFlightType()
        );

        air.clearSeatClasses();
        for (SeatClassRequestDto seatDto : dto.getSeatClasses()) {
            SeatClass seat = new SeatClass(
                    seatDto.getClassType(),
                    seatDto.getPrice(),
                    seatDto.getMaxSeats(),
                    seatDto.getAvailableSeats()
            );
            seat.setAir(air);
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
        if (dto.getAirlineCode() != null) {
            Airline airline = airlineRepository.findByCode(dto.getAirlineCode())
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
                            seatDto.getAvailableSeats()
                    );
                } else {
                    // 새로운 seat 추가
                    SeatClass seat = new SeatClass(
                            seatDto.getClassType(),
                            seatDto.getPrice(),
                            seatDto.getMaxSeats(),
                            seatDto.getAvailableSeats()
                    );
                    seat.setAir(air);
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
        air.updateStatus(status);
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
                .seatClasses(air.getSeatClasses().stream()
                        .map(s -> SeatClassResponseDto.builder()
                                .id(s.getId())
                                .classType(s.getClassType())
                                .price(s.getPrice())
                                .maxSeats(s.getMaxSeats())
                                .availableSeats(s.getAvailableSeats())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
