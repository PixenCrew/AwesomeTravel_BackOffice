package renewal.awesome_travel_backoffice.purchaseAir.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import renewal.common.entity.Air.AirStatus;
import renewal.common.entity.Air.FlightSegment;
import renewal.common.entity.Air.FlightType;
import renewal.common.entity.AirportCode;
import renewal.common.entity.SeatClass;
import renewal.common.entity.SeatClass.SeatClassType;

@Getter
@Builder
public class AirResponseOneDto {

    private Long airId;
    private String code;
    private String flightNumber;

    // 항공사 정보
    private String airlineCode;
    private String airlineNameKor;
    private String airlineNameEng;

    private AirportCode depart;
    private String departAirportCode; // 공항 코드 (String)
    private String departTerminal;
    private AirportCode arrive;
    private String arriveAirportCode; // 공항 코드 (String)
    private String arriveTerminal;
    private LocalDateTime departTime;
    private LocalDateTime arriveTime;
    private Long flightDuration;
    private int stopovers;
    private FlightType flightType;
    private AirStatus status;

    private List<FlightSegment> flightSegments;
    private List<SeatClass> seatClasses;

    // 단일 좌석 정보 스냅샷
    private Long seatClassId;
    private SeatClassType seatClassType;
    private Long price;
    private Long availableSeats;
}
