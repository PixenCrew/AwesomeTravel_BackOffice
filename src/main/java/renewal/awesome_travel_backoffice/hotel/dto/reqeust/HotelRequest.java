package renewal.awesome_travel_backoffice.hotel.dto.reqeust;

import lombok.Builder;
import lombok.Getter;
import renewal.awesome_travel_backoffice.hotel.utils.HotelType;

import java.util.List;

@Getter
@Builder
public class HotelRequest {
    private String name;
    private String description;
    private String address;
    private String number;
    private String email;
    private String website;
    private HotelType hotelType;
    private Boolean isActive;
    private List<String> amenities;          // 편의시설 리스트
    private List<String> imageUrls;          // 이미지 URL 리스트 (s3 또는 static path 등)
}
