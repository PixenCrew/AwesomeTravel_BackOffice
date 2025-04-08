package renewal.awesome_travel_backoffice.air.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.air.dto.request.SeatClassRequestDto;
import renewal.awesome_travel_backoffice.air.dto.response.SeatClassResponseDto;
import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.air.entity.SeatClass;
import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.awesome_travel_backoffice.air.repository.SeatClassRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatClassService {

    private final AirRepository airRepository;
    private final SeatClassRepository seatClassRepository;

    public SeatClassResponseDto create(Long airId, SeatClassRequestDto dto) {
        Air air = airRepository.findById(airId)
                .orElseThrow(() -> new IllegalArgumentException("항공편이 존재하지 않습니다."));

        // 중복 좌석 타입 검사
        boolean exists = air.getSeatClasses().stream()
                .anyMatch(sc -> sc.getClassType() == dto.getClassType());
        if (exists) {
            throw new IllegalArgumentException("이미 등록된 좌석 등급입니다: " + dto.getClassType());
        }

        // 유효성 검사
        if (dto.getAvailableSeats() > dto.getMaxSeats()) {
            throw new IllegalArgumentException("잔여 좌석 수는 최대 좌석 수보다 클 수 없습니다.");
        }

        SeatClass seatClass = new SeatClass(
                dto.getClassType(),
                dto.getPrice(),
                dto.getMaxSeats(),
                dto.getAvailableSeats()
        );
        seatClass.setAir(air);
        air.addSeatClass(seatClass); // 양방향 설정
        seatClassRepository.save(seatClass);

        return toDto(seatClass);
    }

    public List<SeatClassResponseDto> getByAir(Long airId) {
        return seatClassRepository.findByAir_Id(airId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SeatClassResponseDto update(Long id, SeatClassRequestDto dto) {
        SeatClass seatClass = seatClassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("좌석 클래스가 존재하지 않습니다."));
        seatClass.updateSeatClass(dto.getPrice(), dto.getMaxSeats(), dto.getAvailableSeats());
        return toDto(seatClass);
    }

    public void delete(Long id) {
        SeatClass seatClass = seatClassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("좌석 클래스가 존재하지 않습니다."));

        Air air = seatClass.getAir();
        air.removeSeatClass(seatClass); // 양방향 제거
        seatClassRepository.delete(seatClass);
    }


    private SeatClassResponseDto toDto(SeatClass sc) {
        return SeatClassResponseDto.builder()
                .id(sc.getId())
                .classType(sc.getClassType())
                .price(sc.getPrice())
                .maxSeats(sc.getMaxSeats())
                .availableSeats(sc.getAvailableSeats())
                .build();
    }
}
