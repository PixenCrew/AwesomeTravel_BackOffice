package renewal.awesome_travel_backoffice.airPurchase.dto.response;

import java.time.LocalDateTime;

import renewal.awesome_travel_backoffice.air.entity.Air.FlightType;
import renewal.awesome_travel_backoffice.air.entity.SeatClass.SeatClassType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AirResponseOneDto {
    private Long airId;
    private String code;

    //항공사정보
    private String airlineCode;
    private String airlineNameKor;
    private String airlineNameEng;

    private String depart;
    private String arrive;
    private LocalDateTime departTime;
    private LocalDateTime arriveTime;
    private int stopovers;
    private FlightType flightType;

    // 단일 좌석 정보
    private Long seatClassId;
    private SeatClassType seatClassType;
    private Long price;
    private Long availableSeats;
}

