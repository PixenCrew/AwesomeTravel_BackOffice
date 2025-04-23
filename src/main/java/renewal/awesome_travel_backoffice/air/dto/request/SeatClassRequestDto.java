package renewal.awesome_travel_backoffice.air.dto.request;

import lombok.Getter;
import renewal.awesome_travel_backoffice.air.utiles.SeatClassType;

@Getter
public class SeatClassRequestDto {
    private Long id;
    private SeatClassType classType;
    private Integer price;
    private Integer maxSeats;
    private Integer availableSeats;
}
