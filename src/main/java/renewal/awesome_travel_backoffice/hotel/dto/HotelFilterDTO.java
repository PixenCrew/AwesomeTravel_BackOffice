package renewal.awesome_travel_backoffice.hotel.dto;

import lombok.Data;
import renewal.awesome_travel_backoffice.hotel.utils.HotelType;

@Data
public class HotelFilterDTO {
    private String name;
    private String address;
    private String email;
    private Long minPrice;
    private Long maxPrice;
    private HotelType hotelType;
    private Boolean isActive;
}
