package renewal.awesome_travel_backoffice.hotel.dto.response;

import lombok.Builder;
import lombok.Getter;
import renewal.awesome_travel_backoffice.hotel.utils.HotelType;

import java.util.List;

@Getter
@Builder
public class HotelResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String number;
    private String email;
    private String website;
    private HotelType hotelType;
    private List<String> amenities;
    private List<String> imageUrls;
}

