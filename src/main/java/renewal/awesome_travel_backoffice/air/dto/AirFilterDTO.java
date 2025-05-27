package renewal.awesome_travel_backoffice.air.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;
import renewal.awesome_travel_backoffice.air.utiles.FlightType;

@Getter
@Setter
public class AirFilterDTO {
    private String code;
    private List<String> arilines;
    private LocalDate departDateFrom;
    private LocalDate departDateTo;
    private LocalDate arriveDateFrom;
    private LocalDate arriveDateTo;
    private String depart;
    private String arrive;
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
}
