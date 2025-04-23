package renewal.awesome_travel_backoffice.air.dto.response;

import lombok.Builder;
import lombok.Getter;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;
import renewal.awesome_travel_backoffice.air.utiles.FlightType;

import java.util.List;

@Getter
@Builder
public class AirResponseDto {
    private Long id;
    private String code;

    // 항공사 정보
    private String airlineCode;
    private String airlineNameKor;
    private String airlineNameEng;

    private String depart;
    private String departTime;
    private String arrive;
    private String arriveTime;

    private int stopovers;
    private FlightType flightType;
    private AirStatus status;

    private List<SeatClassResponseDto> seatClasses;
}

