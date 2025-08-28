package renewal.awesome_travel_backoffice.hotel.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ElementCollection;

import renewal.awesome_travel_backoffice.config.AuditingFields;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Entity
@Table
@Getter
@Setter
@RequiredArgsConstructor
public class Hotel extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    private String name;
    private String description;
    private String city;
    private String address;
    private String number;
    private String email;
    private String website;
    @Enumerated(EnumType.STRING)
    private HotelType hotelType;
    private Long price;
    private Long maxRoomCount;

    // 패키지에 사용 가능한 호텔 여부
    private Boolean isActive = true;

    // 이미지 URL들
    @ElementCollection
    private List<String> images = new ArrayList<>();

    // 편의시설 목록
    @ManyToMany
    @JoinTable(name = "hotel_amenities", joinColumns = @JoinColumn(name = "hotel_id"), inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private Set<Amenity> amenities = new HashSet<>();

    // 예약 목록
    @OneToMany(fetch = FetchType.LAZY)
    // @JoinTable(
    // name = "hotel_reservations",
    // joinColumns = @JoinColumn(name = "hotel_id")
    // )
    private List<HotelReservation> hotelReservations = new ArrayList<>();

    public enum HotelType {
        HOTEL,
        MOTEL,
        RESORT,
        APARTMENT,
        GUESTHOUSE,
        PENSION,
        HOSTEL,
        HOMESTAY,
        LODGE,
        VILLA,
        ETC
    }

}
