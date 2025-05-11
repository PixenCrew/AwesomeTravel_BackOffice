package renewal.awesome_travel_backoffice.hotel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import renewal.awesome_travel_backoffice.hotel.utils.HotelType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@RequiredArgsConstructor
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
    private Integer maxRoomCount;

    // 패키지에 사용 가능한 호텔 여부
    private Boolean isActive = true; 

    // 이미지 URL들
    private List<String> images = new ArrayList<>();

    // 편의시설 목록
    @ManyToMany
    @JoinTable(
        name = "hotel_amenities",
        joinColumns = @JoinColumn(name = "hotel_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();

}

