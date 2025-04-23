package renewal.awesome_travel_backoffice.air.dto.request;

import lombok.Getter;
import lombok.Setter;
import renewal.awesome_travel_backoffice.air.utiles.FlightType;

import java.util.List;

@Getter
@Setter
public class AirRequestDto {

    private String code;
    private String airlineCode; // 항공사 코드로 Airline 객체 매핑 예정

    private String depart;
    private String departTime;
    private String arrive;
    private String arriveTime;

    private Integer stopovers;
    private FlightType flightType;

    private List<SeatClassRequestDto> seatClasses;
}
