package renewal.awesome_travel_backoffice.air.dto.response;

import lombok.Builder;
import lombok.Getter;
import renewal.awesome_travel_backoffice.air.utiles.FlightType;

import java.util.List;

@Getter
@Builder
public class AirWithSeatClassDto {
    private Long airId;
    private String code;
    private String airline;
    private String depart;
    private String departTime;
    private String arrive;
    private String arriveTime;
    private int stopovers;
    private FlightType flightType;
    private List<SeatClassResponseDto> seatClasses;
}
