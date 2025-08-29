package renewal.awesome_travel_backoffice.hotel.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import renewal.common.entity.Hotel.HotelType;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd") 
    private LocalDate endDate;

    // 예약검색용
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDate;
    private Long requiredPersons;
}
