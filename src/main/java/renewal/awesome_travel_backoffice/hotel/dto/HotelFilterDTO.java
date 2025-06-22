package renewal.awesome_travel_backoffice.hotel.dto;

import java.time.LocalDate;

import lombok.Data;
import renewal.awesome_travel_backoffice.hotel.utils.HotelType;

@Data
public class HotelFilterDTO {
    private String name;
    private String city;
    private String address;
    private String email;
    private Long minPrice;
    private Long maxPrice;
    private HotelType hotelType;
    private Boolean isActive;

    // 예약검색용
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer requiredPersons;
}
