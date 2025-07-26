package renewal.awesome_travel_backoffice.air.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;
import renewal.awesome_travel_backoffice.air.utiles.FlightType;
import renewal.awesome_travel_backoffice.air.utiles.SeatClassType;

@Getter
@Setter
public class AirFilterDTO {
    private String code;
    private List<String> airlines;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate departDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate departDateTo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate arriveDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate arriveDateTo;
    private String departAirport;
    private String arriveAirport;
    private Long minStopovers;
    private Long maxStopovers;
    private Boolean infantSeatsRequired;
    private Long startCount;
    private Long endCount;
    private FlightType flightType;
    private AirStatus status;

    // SeatClasses 검색
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long availableSeats;
    private SeatClassType seatClassType;
}
