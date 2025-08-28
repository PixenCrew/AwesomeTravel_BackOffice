package renewal.awesome_travel_backoffice.air.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightItem {

    @JsonProperty("항공사")
    private String airline;  // 항공사 코드 (예: AAR = 아시아나)

    @JsonProperty("운항편명")
    private String flightNumber;

    @JsonProperty("운항요일")
    private String operatingDays;  // "토" 형식

    //========================================

    @JsonProperty("출발공항")
    private String departureAirport;
    
    @JsonProperty("시작일자")
    private LocalDate startDate;  // "2024-04-27" 형식

    @JsonProperty("출발시간")
    private LocalTime departureTime;  // "16:00" 형식

    //========================================

    @JsonProperty("도착공항")
    private String arrivalAirport;

    @JsonProperty("종료일자")
    private LocalDate endDate;

    @JsonProperty("도착시간")
    private LocalTime arrivalTime;
    
}

