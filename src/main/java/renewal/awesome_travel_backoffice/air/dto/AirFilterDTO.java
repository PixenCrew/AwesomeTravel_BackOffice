package renewal.awesome_travel_backoffice.air.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import renewal.common.entity.AirportCode;
import renewal.common.entity.CityCode;
import renewal.common.entity.Air.AirStatus;
import renewal.common.entity.Air.FlightType;
import renewal.common.entity.SeatClass.SeatClassType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirFilterDTO {
    private String code;
    private List<String> airlines;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate departDateTimeFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate departDateTimeTo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate arriveDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate arriveDateTo;
    private AirportCode departAirport;
    private AirportCode arriveAirport;
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
    
    // Alias getters/setters for backward compatibility
    public LocalDate getDepartDateFrom() {
        return departDateTimeFrom;
    }
    
    public void setDepartDateFrom(LocalDate departDateFrom) {
        this.departDateTimeFrom = departDateFrom;
    }
    
    public LocalDate getDepartDateTo() {
        return departDateTimeTo;
    }
    
    public void setDepartDateTo(LocalDate departDateTo) {
        this.departDateTimeTo = departDateTo;
    }
    
    public LocalDate getArriveDateFrom() {
        return arriveDateFrom;
    }
    
    public void setArriveDateFrom(LocalDate arriveDateFrom) {
        this.arriveDateFrom = arriveDateFrom;
    }
    
    public LocalDate getArriveDateTo() {
        return arriveDateTo;
    }
    
    public void setArriveDateTo(LocalDate arriveDateTo) {
        this.arriveDateTo = arriveDateTo;
    }
    
    // Additional aliases for other fields that might be referenced
    public List<String> getAirlines() {
        return airlines;
    }
    
    public void setAirlines(List<String> airlines) {
        this.airlines = airlines;
    }
    
    public Boolean getInfantSeatsRequired() {
        return infantSeatsRequired;
    }
    
    public void setInfantSeatsRequired(Boolean infantSeatsRequired) {
        this.infantSeatsRequired = infantSeatsRequired;
    }
    
    public SeatClassType getSeatClassType() {
        return seatClassType;
    }
    
    public void setSeatClassType(SeatClassType seatClassType) {
        this.seatClassType = seatClassType;
    }
}
