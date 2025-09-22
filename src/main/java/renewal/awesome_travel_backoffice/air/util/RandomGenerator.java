package renewal.awesome_travel_backoffice.air.util;

import java.time.*;
import java.util.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.awesome_travel_backoffice.air.repository.AirlineRepository;
import renewal.awesome_travel_backoffice.air.repository.SeatClassRepository;
import renewal.common.entity.Air;
import renewal.common.entity.Airline;
import renewal.common.entity.CityCode;
import renewal.common.entity.SeatClass;
import renewal.common.repository.CityCodeRepository;

@Configuration
public class RandomGenerator {

    @Bean
    CommandLineRunner loadFakeAirWithSeats(
            AirRepository airRepo,
            AirlineRepository airlineRepo,
            CityCodeRepository cityRepo,
            SeatClassRepository seatRepo) {
        return args -> {
            System.out.println(">>> RandomGenerator CommandLineRunner 실행됨!");
            Random random = new Random();
            List<Airline> airlines = airlineRepo.findAll();
            List<CityCode> cities = cityRepo.findAll();

            for (int i = 100; i < 600; i++) {
                Airline airline = airlines.get(random.nextInt(airlines.size()));

                // 출/도착 공항은 다르게 선택
                CityCode depart = cities.get(random.nextInt(cities.size()));
                CityCode arrive;
                do {
                    arrive = cities.get(random.nextInt(cities.size()));
                } while (arrive.equals(depart));

                // 출발/도착 LocalDateTime 생성
                LocalDate departDate = LocalDate.now().plusDays(random.nextInt(60));
                LocalTime departTime = LocalTime.of(random.nextInt(24), random.nextInt(60));
                LocalDateTime departDateTime = LocalDateTime.of(departDate, departTime);

                LocalDate arriveDate = departDate.plusDays(random.nextInt(2));
                LocalTime arriveTime = departTime.plusHours(2 + random.nextInt(10));
                LocalDateTime arriveDateTime = LocalDateTime.of(arriveDate, arriveTime);

                // stopovers 결정
                int stopovers = 0;
                double rand = random.nextDouble();
                if (rand < 0.1)
                    stopovers = 2;
                else if (rand < 0.3)
                    stopovers = 1;

                // FlightSegment 생성
                List<Air.FlightSegment> segments = new ArrayList<>();

                if (stopovers == 0) {
                    Air.FlightSegment segment = new Air.FlightSegment();
                    segment.setDepartAirport(depart);
                    segment.setDepartDateTime(departDateTime);
                    segment.setArriveAirport(arrive);
                    segment.setArriveDateTime(arriveDateTime);
                    segment.calculateFlightDuration(); // 비행시간 계산
                    segments.add(segment);
                } else {
                    // stopovers > 0: 간단히 직항 + 경유 예시
                    LocalDateTime segmentDepart = departDateTime;
                    for (int s = 0; s <= stopovers; s++) {
                        CityCode segDepart = (s == 0) ? depart : cities.get(random.nextInt(cities.size()));
                        CityCode segArrive = (s == stopovers) ? arrive : cities.get(random.nextInt(cities.size()));
                        LocalDateTime segArriveTime = segmentDepart.plusHours(2 + random.nextInt(3));

                        Air.FlightSegment segment = new Air.FlightSegment();
                        segment.setDepartAirport(segDepart);
                        segment.setDepartDateTime(segmentDepart);
                        segment.setArriveAirport(segArrive);
                        segment.setArriveDateTime(segArriveTime);
                        segment.calculateFlightDuration();
                        segments.add(segment);

                        segmentDepart = segArriveTime.plusHours(1); // 대기 1시간 가정
                    }
                }

                // Air 객체 생성 및 필드 설정
                Air air = new Air();
                air.setFlightNumber("FAKE" + i);
                air.setAirline(airline);
                air.setDepartAirport(depart);
                air.setDepartTerminal("T1");
                air.setDepartDateTime(departDateTime);
                air.setArriveAirport(arrive);
                air.setArriveTerminal("T1");
                air.setArriveDateTime(arriveDateTime);
                air.setStopovers(stopovers);
                air.setFlightSegmentList(segments);

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
                    System.out.println(">>> 중복 또는 저장 오류 발생, 건너뜀: " + e.getMessage());
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
}