package renewal.awesome_travel_backoffice.air.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.air.dto.AirFilterDTO;
import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.awesome_travel_backoffice.air.repository.AirSpecification;
import renewal.awesome_travel_backoffice.air.repository.AirlineRepository;
import renewal.awesome_travel_backoffice.air.repository.SeatClassAdminRepository;
import renewal.awesome_travel_backoffice.airport.repository.AirportCodeRepository;
import renewal.common.entity.Air;
import renewal.common.entity.Air.AirStatus;
import renewal.common.entity.Air.FlightSegment;
import renewal.common.entity.Airline;
import renewal.common.entity.AirportCode;
import renewal.common.entity.SeatClass;
import renewal.common.repository.CityCodeRepository;

@Service
@RequiredArgsConstructor
public class AirService {

    private final SeatClassAdminRepository seatClassAdminRepo;
    private final AirRepository airRepo;
    private final AirlineRepository airlineRepo;
    private final CityCodeRepository cityCodeRepo;
    private final AirportCodeRepository airportCodeRepo;

    // private final AirRepositoryCustomImpl airRepositoryCustom;

    @Transactional
    public void createAir(Air air) {
        if (airRepo.existsByflightNumber(air.getFlightNumber())) {
            throw new IllegalArgumentException("중복된 항공편 코드입니다.");
        }

        // Airline 코드로 실제 Airline 엔티티를 조회해서 설정
        Airline airline = airlineRepo
                .findByCode(air.getAirline().getCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항공사입니다."));

        // 조회한 Airline 엔티티로 설정
        air.setAirline(airline);

        // 잔여 좌석 수가 최대 좌석 수를 넘지 않도록 검증
        validateSeatCounts(air);

        saveAir(air);

        // // 조회한 Airline 엔티티로 설정
        // air.setAirline(airline);

        // for (SeatClass seat : air.getSeatClasses()) {
        // seat.setAir(air);
        // }

        // // 1. 전체 소요시간 계산
        // air.setFlightDuration(
        // calcDuration(air.getDepartDateTime(), air.getDepartAirport(),
        // air.getArriveDateTime(),
        // air.getArriveAirport()));

        // // 2. 각 segment별 소요시간 계산
        // for (FlightSegment segment : air.getFlightSegments()) {
        // segment.setFlightDuration(
        // calcDuration(segment.getDepartDateTime(), segment.getDepartAirport(),
        // segment.getArriveDateTime(), segment.getArriveAirport()));
        // }

        // // 3. segment 사이 대기시간 계산
        // List<FlightSegment> segments = air.getFlightSegments();

        // for (int i = 0; i < segments.size() - 1; i++) {
        // FlightSegment currentSegment = segments.get(i);
        // LocalDateTime currentArriveTime = currentSegment.getArriveDateTime();
        // AirportCode currentArriveAirport = currentSegment.getArriveAirport();

        // FlightSegment nextSegment = segments.get(i + 1);
        // LocalDateTime nextDepartTime = nextSegment.getDepartDateTime();
        // AirportCode nextDepartAirport = nextSegment.getDepartAirport();

        // currentSegment.setWaitDuration(
        // calcDuration(currentArriveTime, currentArriveAirport, nextDepartTime,
        // nextDepartAirport));
        // }

        // airRepo.save(air);
    }

    @Transactional
    public void changeStatus(Long id, AirStatus status) {
        Air air = airRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 항공편이 존재하지 않습니다."));
        air.setStatus(status);
    }

    @Transactional
    public void deleteAir(Long id) {
        Air air = airRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 항공편이 존재하지 않습니다."));
        airRepo.delete(air);
    }

    public void saveAir(Air air) {
        // 선택되지 않은 SeatClass 제거 (가격이나 좌석 수가 입력되지 않은 경우)
        if (air.getSeatClasses() != null) {
            air.getSeatClasses().removeIf(seat -> 
                seat == null || 
                seat.getClassType() == null ||
                seat.getPriceAdult() == null || seat.getPriceAdult() == 0 ||
                seat.getMaxSeats() == null || seat.getMaxSeats() == 0
            );
        }
        
        // 잔여 좌석 수가 최대 좌석 수를 넘지 않도록 검증
        validateSeatCounts(air);

        for (SeatClass seat : air.getSeatClasses()) {
            seat.setAir(air);
        }

        // 1. 전체 소요시간 계산
        air.setFlightDuration(
                calcDuration(air.getDepartDateTime(), air.getDepartAirport(), air.getArriveDateTime(),
                        air.getArriveAirport()));

        // 2. flightSegments null 체크 및 초기화
        List<FlightSegment> segments = air.getFlightSegments();
        if (segments == null) {
            segments = new ArrayList<>();
            air.setFlightSegments(segments);
        }

        // 3. 각 segment별 소요시간 계산
        for (FlightSegment segment : segments) {
            if (segment != null && segment.getDepartDateTime() != null && segment.getArriveDateTime() != null) {
                segment.setFlightDuration(
                        calcDuration(segment.getDepartDateTime(), segment.getDepartAirport(),
                                segment.getArriveDateTime(), segment.getArriveAirport()));
            }
        }

        // 4. segment 사이 대기시간 계산
        for (int i = 0; i < segments.size() - 1; i++) {
            FlightSegment currentSegment = segments.get(i);
            FlightSegment nextSegment = segments.get(i + 1);
            
            if (currentSegment != null && nextSegment != null 
                    && currentSegment.getArriveDateTime() != null 
                    && nextSegment.getDepartDateTime() != null) {
                LocalDateTime currentArriveTime = currentSegment.getArriveDateTime();
                AirportCode currentArriveAirport = currentSegment.getArriveAirport();
                LocalDateTime nextDepartTime = nextSegment.getDepartDateTime();
                AirportCode nextDepartAirport = nextSegment.getDepartAirport();

                currentSegment.setWaitDuration(
                        calcDuration(currentArriveTime, currentArriveAirport, nextDepartTime, nextDepartAirport));
            }
        }

        airRepo.save(air);
    }

    public Page<SeatClass> searchAirs(AirFilterDTO filter, Pageable pageable) {

        // Specification<SeatClass> spec = Specification.where(null);
        Specification<SeatClass> spec = Specification.where(AirSpecification.fetchAir());

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
        // spec = spec.and(AirSpecification.stopoversEquals(filter.getStopovers()));
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

        return seatClassAdminRepo.findAll(spec, pageable);
    }

    // (시간+도시코드)로 소요시간[분] 계산
    public Long calcDuration(LocalDateTime departTime, AirportCode departCode, LocalDateTime arriveTime,
            AirportCode arriveCode) {
        Long departUtcOffset = airportCodeRepo.findById(departCode.getAirportCode()).get().getCityCode()
                .getUtcOffsetMins();
        Long arriveUtcOffset = airportCodeRepo.findById(arriveCode.getAirportCode()).get().getCityCode()
                .getUtcOffsetMins();
        ZoneOffset departOffset = ZoneOffset.ofTotalSeconds((int) (departUtcOffset * 60));
        ZoneOffset arriveOffset = ZoneOffset.ofTotalSeconds((int) (arriveUtcOffset * 60));

        Instant departUtc = departTime.toInstant(departOffset);
        Instant arriveUtc = arriveTime.toInstant(arriveOffset);

        Long result = Duration.between(departUtc, arriveUtc).toMinutes();
        return result;
    }

    // !!!!!!!!!!!!!!!!!![TEST] 항공권 100일치 복제 !!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private final Random random = new Random();

    @Transactional
    public List<Air> generateAirVariantsWithRandomPrice(Air sourceAir) {
        List<Air> generated = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            // 5% 확률로 건너뛰기
            if (random.nextDouble() < 0.05)
                continue;

            Air newAir = new Air();

            // === 기본정보 복사 ===
            newAir.setFlightNumber(sourceAir.getFlightNumber() + "-VAR" + i);
            newAir.setAirline(sourceAir.getAirline());
            newAir.setDepartAirport(sourceAir.getDepartAirport());
            newAir.setDepartTerminal(sourceAir.getDepartTerminal());
            newAir.setArriveAirport(sourceAir.getArriveAirport());
            newAir.setArriveTerminal(sourceAir.getArriveTerminal());
            newAir.setFlightDuration(sourceAir.getFlightDuration());
            newAir.setStopovers(sourceAir.getStopovers());
            newAir.setFlightType(sourceAir.getFlightType());
            newAir.setStatus(Air.AirStatus.ACTIVE);

            // === 날짜 +i일 증가 ===
            newAir.setDepartDateTime(sourceAir.getDepartDateTime().plusDays(i));
            newAir.setArriveDateTime(sourceAir.getArriveDateTime().plusDays(i));

            // === SeatClass 복제 (가격만 랜덤 변경) ===
            List<SeatClass> newSeatClasses = new ArrayList<>();
            for (SeatClass oldSeat : sourceAir.getSeatClasses()) {
                SeatClass sc = new SeatClass();
                sc.setAir(newAir);
                sc.setClassType(oldSeat.getClassType());
                sc.setMaxSeats(oldSeat.getMaxSeats());
                sc.setAvailableSeats(oldSeat.getAvailableSeats());

                // 좌석 랜덤 변동 ±20% 범위
                sc.setAvailableSeats(applyRandomSeats(oldSeat.getAvailableSeats()));

                // 가격 랜덤 변화 (±10~30% 변동)
                sc.setPriceAdult(applyRandomPrice(oldSeat.getPriceAdult()));
                sc.setPriceYouth(applyRandomPrice(oldSeat.getPriceYouth()));
                sc.setPriceInfant(applyRandomPrice(oldSeat.getPriceInfant()));

                newSeatClasses.add(sc);
            }
            newAir.setSeatClasses(newSeatClasses);

            // === FlightSegment 그대로 복제 (변동 없이) ===
            List<Air.FlightSegment> newSegments = new ArrayList<>();
            for (Air.FlightSegment oldSeg : sourceAir.getFlightSegments()) {
                Air.FlightSegment seg = new Air.FlightSegment();
                seg.setDepartAirport(oldSeg.getDepartAirport());
                seg.setDepartTerminal(oldSeg.getDepartTerminal());
                seg.setDepartDateTime(oldSeg.getDepartDateTime());
                seg.setArriveAirport(oldSeg.getArriveAirport());
                seg.setArriveTerminal(oldSeg.getArriveTerminal());
                seg.setArriveDateTime(oldSeg.getArriveDateTime());
                seg.setFlightDuration(oldSeg.getFlightDuration());
                seg.setWaitDuration(oldSeg.getWaitDuration());
                newSegments.add(seg);
            }
            newAir.setFlightSegments(newSegments);

            airRepo.save(newAir);
            generated.add(newAir);
        }

        return generated;
    }

    /**
     * 기존 가격에서 ±10~30% 변동 (랜덤)
     */
    private Long applyRandomPrice(Long original) {
        if (original == null || original <= 0)
            return original;
        double variation = (random.nextDouble() * 0.2 + 0.1); // 10~30%
        boolean increase = random.nextBoolean();
        double newValue = increase
                ? original * (1 + variation)
                : original * (1 - variation);
        return Math.round(newValue);
    }

    /**
     * 기존 좌석에서 ±20% 변동 (랜덤)
     */
    private Long applyRandomSeats(Long original) {
        if (original == null || original <= 0)
            return original;
        double variation = (random.nextDouble() * 0.4 - 0.2); // -20% ~ +20%
        long newSeats = Math.round(original * (1 + variation));
        return Math.max(newSeats, 1L); // 최소 1석은 보장
    }

    /**
     * 잔여 좌석 수가 최대 좌석 수를 넘지 않도록 검증
     * 최대 좌석 수는 1000을 넘을 수 없음
     * 가격은 1억원을 넘을 수 없음
     */
    private void validateSeatCounts(Air air) {
        if (air.getSeatClasses() == null) {
            return;
        }
        
        final long MAX_PRICE = 100000000L; // 1억원
        
        for (SeatClass seat : air.getSeatClasses()) {
            // 최대 좌석 수 1000 제한 검증
            if (seat.getMaxSeats() != null && seat.getMaxSeats() > 1000) {
                throw new IllegalArgumentException(
                    String.format("최대 좌석 수(%d)는 1000을 넘을 수 없습니다. (등급: %s)", 
                        seat.getMaxSeats(), 
                        seat.getClassType()));
            }
            
            // 잔여 좌석 수가 최대 좌석 수를 넘지 않도록 검증
            if (seat.getMaxSeats() != null && seat.getAvailableSeats() != null) {
                if (seat.getAvailableSeats() > seat.getMaxSeats()) {
                    throw new IllegalArgumentException(
                        String.format("잔여 좌석 수(%d)는 최대 좌석 수(%d)를 넘을 수 없습니다. (등급: %s)", 
                            seat.getAvailableSeats(), 
                            seat.getMaxSeats(), 
                            seat.getClassType()));
                }
            }
            
            // 가격 1억원 제한 검증
            if (seat.getPriceAdult() != null && seat.getPriceAdult() > MAX_PRICE) {
                throw new IllegalArgumentException(
                    String.format("가격[성인](%d원)은 1억원을 넘을 수 없습니다. (등급: %s)", 
                        seat.getPriceAdult(), 
                        seat.getClassType()));
            }
            if (seat.getPriceYouth() != null && seat.getPriceYouth() > MAX_PRICE) {
                throw new IllegalArgumentException(
                    String.format("가격[청소년](%d원)은 1억원을 넘을 수 없습니다. (등급: %s)", 
                        seat.getPriceYouth(), 
                        seat.getClassType()));
            }
            if (seat.getPriceInfant() != null && seat.getPriceInfant() > MAX_PRICE) {
                throw new IllegalArgumentException(
                    String.format("가격[영유아](%d원)은 1억원을 넘을 수 없습니다. (등급: %s)", 
                        seat.getPriceInfant(), 
                        seat.getClassType()));
            }
        }
    }

}
