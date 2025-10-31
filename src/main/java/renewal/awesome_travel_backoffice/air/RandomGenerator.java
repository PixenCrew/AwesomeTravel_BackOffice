package renewal.awesome_travel_backoffice.air;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.awesome_travel_backoffice.air.repository.AirlineRepository;
import renewal.awesome_travel_backoffice.air.repository.SeatClassRepository;
import renewal.awesome_travel_backoffice.air.service.AirService;
import renewal.awesome_travel_backoffice.airport.repository.AirportCodeRepository;
import renewal.common.entity.Air;
import renewal.common.entity.Air.FlightType;
import renewal.common.entity.Airline;
import renewal.common.entity.AirportCode;
import renewal.common.entity.SeatClass;
import renewal.common.repository.CountryCodeRepository;

@Configuration
public class RandomGenerator {

    @Bean
    CommandLineRunner loadFakeAirWithSeats(
            AirRepository airRepo,
            AirlineRepository airlineRepo,
            AirportCodeRepository airportRepo,
            CountryCodeRepository countryRepo,
            SeatClassRepository seatRepo,
            AirService airService) {
        return args -> {
            setDummyAuthentication();
            System.out.println(">>> RandomGenerator CommandLineRunner 실행됨!");
            Random random = new Random();
            List<Airline> airlines = airlineRepo.findAll();
            // List<AirportCode> cities =
            // airportRepo.findByCityCodeCountryCodeCountryCode("KR");
            List<AirportCode> cities = airportRepo.findAllByMenuCodeCities();

            for (int i = 100; i < 100000; i++) {
                Airline airline = airlines.get(random.nextInt(airlines.size()));

                // 출/도착 공항은 다르게 선택
                AirportCode depart = cities.get(random.nextInt(cities.size()));
                AirportCode arrive;
                do {
                    arrive = cities.get(random.nextInt(cities.size()));
                } while (arrive.equals(depart));

                // 출발시간
                LocalDateTime departDateTime = LocalDateTime.now()
                        .plusDays(random.nextInt(100))
                        .withHour(random.nextInt(24))
                        .withMinute(random.nextInt(60));

                ZoneOffset departOffset = ZoneOffset
                        .ofTotalSeconds((int) (depart.getCityCode().getUtcOffsetMins() * 60));
                ZoneOffset arriveOffset = ZoneOffset
                        .ofTotalSeconds((int) (arrive.getCityCode().getUtcOffsetMins() * 60));

                // 목표 비행 시간 (분)
                int flightMinutes = 120 + random.nextInt(600); // 2~12시간

                // UTC 기준 출발시간
                Instant departUtc = departDateTime.toInstant(departOffset);

                // 도착 UTC 시간 = 출발 UTC + flightMinutes
                Instant arriveUtc = departUtc.plus(flightMinutes, ChronoUnit.MINUTES);

                // 도착 LocalDateTime = 도착 UTC 시간 + 도착 공항 offset
                LocalDateTime arriveDateTime = LocalDateTime.ofInstant(arriveUtc, arriveOffset);

                // stopovers 결정
                int stopovers = 0;
                double rand = random.nextDouble();
                if (rand < 0.3)
                    stopovers = 2;
                else if (rand < 0.6)
                    stopovers = 1;

                // Air 객체 생성 및 필드 설정
                Air air = new Air();
                // FlightSegment 생성
                List<Air.FlightSegment> segments = new ArrayList<>();

                if (stopovers == 0) {
                    air.setFlightType(FlightType.DIRECT);
                    Air.FlightSegment segment = new Air.FlightSegment();
                    segment.setDepartAirport(depart);
                    segment.setDepartTerminal("T2");
                    segment.setDepartDateTime(departDateTime);
                    segment.setArriveAirport(arrive);
                    segment.setArriveTerminal("T3");
                    segment.setArriveDateTime(arriveDateTime);
                    segment.setFlightDuration(airService.calcDuration(departDateTime, depart.getCityCode(),
                            arriveDateTime, arrive.getCityCode()));
                    segments.add(segment);
                } else {

                    air.setFlightType(FlightType.STOP_OVER);

                    // stopovers > 0: 간단히 직항 + 경유 예시
                    AirportCode lastArrive = depart; // 이전 segment의 도착 공항, 첫 segment는 출발 공항
                    LocalDateTime segmentDepart = departDateTime;
                    for (int s = 0; s <= stopovers; s++) {
                        AirportCode segDepart = lastArrive; // 이전 segment 도착 = 현재 segment 출발
                        AirportCode segArrive;

                        if (s == stopovers) {
                            segArrive = arrive; // 마지막 segment 도착은 최종 도착
                        } else {
                            // 랜덤 경유 공항 선택, 이전 segment 도착와 중복되지 않게
                            do {
                                segArrive = cities.get(random.nextInt(cities.size()));
                            } while (segArrive.equals(segDepart));
                        }

                        LocalDateTime segArriveTime = segmentDepart.plusMinutes(random.nextLong(60, 360 + 1));

                        Air.FlightSegment segment = new Air.FlightSegment();
                        segment.setDepartAirport(segDepart);
                        segment.setDepartTerminal("T2");
                        segment.setDepartDateTime(segmentDepart);
                        segment.setArriveAirport(segArrive);
                        segment.setArriveTerminal("T3");
                        segment.setArriveDateTime(segArriveTime);
                        segment.setFlightDuration(
                                airService.calcDuration(departDateTime, depart.getCityCode(), arriveDateTime,
                                        arrive.getCityCode()));

                        if (s != 0) {
                            Air.FlightSegment lastSegment = segments.get(segments.size() - 1); // getLast() 대신
                            lastSegment.setWaitDuration(airService.calcDuration(departDateTime, depart.getCityCode(),
                                    arriveDateTime, arrive.getCityCode()));
                        }

                        segments.add(segment);

                        // 다음 segment를 위해 준비
                        segmentDepart = segArriveTime.plusMinutes(random.nextLong(60, 180 + 1)); // 대기 ~3시간 가정
                        lastArrive = segArrive; // 이번 segment 도착을 다음 segment 출발로
                    }
                }

                air.setFlightNumber("FAKE" + i);
                air.setAirline(airline);
                air.setDepartAirport(depart);
                air.setDepartTerminal("T1");
                air.setDepartDateTime(departDateTime);
                air.setArriveAirport(arrive);
                air.setArriveTerminal("T1");
                air.setArriveDateTime(arriveDateTime);
                air.setStopovers(stopovers);
                air.setFlightSegments(segments);

                // 총 비행시간 계산
                long totalDuration = segments.stream()
                        .mapToLong(Air.FlightSegment::getFlightDuration)
                        .sum();
                air.setFlightDuration(totalDuration);

                // 좌석 등급 생성
                List<SeatClass> seatClasses = new ArrayList<>();
                Long base = 50000L + random.nextLong(50000);
                seatClasses.add(makeSeat(air, SeatClass.SeatClassType.ECONOMY, base, 200L, random));
                seatClasses.add(makeSeat(air, SeatClass.SeatClassType.PREMIUMECONOMY, base + 30000L, 80L, random));
                seatClasses.add(makeSeat(air, SeatClass.SeatClassType.BUSINESS, base + 150000L, 30L, random));
                seatClasses.add(makeSeat(air, SeatClass.SeatClassType.FIRST, base + 300000L, 10L, random));

                seatClasses.forEach(s -> s.setAir(air));
                air.setSeatClasses(seatClasses);

                try {
                    airRepo.save(air);
                } catch (Exception e) {
                    System.out.println(">>> 중복 또는 저장 오류 발생, break: " + e.getMessage());
                    break;
                }
            }
        };
    }

    private SeatClass makeSeat(Air air, SeatClass.SeatClassType type, Long basePrice, Long maxSeats, Random r) {
        SeatClass seat = new SeatClass();
        seat.setClassType(type);
        seat.setPriceAdult(basePrice);
        seat.setPriceYouth(Math.round(basePrice * 0.8));
        seat.setPriceInfant(Math.round(basePrice * 0.2));
        seat.setMaxSeats(maxSeats);
        seat.setAvailableSeats(maxSeats - r.nextLong(maxSeats / 4 + 1));
        return seat;
    }

    public void setDummyAuthentication() {
        // 1. 권한 리스트 생성 (필요한 역할만 넣으면 됨)
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"));

        // 2. UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("system", "N/A",
                authorities);

        // 3. SecurityContextHolder에 세팅
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}