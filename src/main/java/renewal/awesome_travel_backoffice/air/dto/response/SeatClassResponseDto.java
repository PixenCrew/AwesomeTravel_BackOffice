package renewal.awesome_travel_backoffice.air.dto.response;

import lombok.Builder;
import lombok.Getter;
import renewal.awesome_travel_backoffice.air.utiles.SeatClassType;

@Getter
@Builder
public class SeatClassResponseDto {
    private Long id;
    private SeatClassType classType;
    private Integer price;
    private Integer maxSeats;
    private Integer availableSeats;
}

