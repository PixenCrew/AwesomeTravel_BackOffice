package renewal.awesome_travel_backoffice.product.dto.request;

import lombok.Builder;
import lombok.Getter;
import renewal.awesome_travel_backoffice.hotel.utils.RoomType;

import java.time.LocalDate;

@Getter
@Builder
public class PackageHotelRequest {
    private Long packageId;
    private Long hotelId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private RoomType roomType;
    private Integer price;
    private Integer reservedRooms;
}
