package renewal.awesome_travel_backoffice.air.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.air.utiles.SeatClassType;
@Getter
@RequiredArgsConstructor
public class SeatClassRequestDto {
    private final Long id;
    private final SeatClassType classType;
    private final long price;
    private final long maxSeats;
    private final long availableSeats;
}
