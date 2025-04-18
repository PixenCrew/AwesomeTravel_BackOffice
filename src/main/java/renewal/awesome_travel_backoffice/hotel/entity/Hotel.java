package renewal.awesome_travel_backoffice.hotel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import renewal.awesome_travel_backoffice.hotel.dto.reqeust.HotelRequest;
import renewal.awesome_travel_backoffice.hotel.utiles.HotelType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotels")
@Getter
@NoArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Long id;

    private String name;
    private String description;
    private String address;
    private String number;
    private String email;
    private String website;
    @Enumerated(EnumType.STRING)
    private HotelType hotelType;

    private Integer price;

    private Boolean isActive = true; // 패키지에 사용 가능한 호텔 여부

    // 편의시설 예시
    @ElementCollection
    @CollectionTable(name = "hotel_amenities", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "amenity")
    private List<String> amenities = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelImage> images = new ArrayList<>();

    public Hotel(String name, String description, String address, String number, String email, String website, HotelType hotelType, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.number = number;
        this.email = email;
        this.website = website;
        this.hotelType = hotelType;
        this.isActive = isActive;
    }

    public void updateHotel(HotelRequest request) {
        this.name = request.getName();
        this.description = request.getDescription();
        this.address = request.getAddress();
        this.number = request.getNumber();
        this.email = request.getEmail();
        this.website = request.getWebsite();
        this.hotelType = request.getHotelType();
        this.isActive = request.getIsActive();
    }

    public void updateAmenities(List<String> amenities) {
        this.amenities.clear();
        this.amenities.addAll(amenities);
    }

    public void updateImages(List<String> imageUrls) {
        this.images.clear();
        for (String url : imageUrls) {
            this.images.add(new HotelImage(url, this));
        }
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateNumber(String number) {
        this.number = number;
    }


    public void updateEmail(String email) {
        this.email = email;
    }


    public void updateWebsite(String website) {
        this.website = website;
    }


    public void updateHotelType(HotelType hotelType) {
        this.hotelType = hotelType;
    }

    public void updateIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

