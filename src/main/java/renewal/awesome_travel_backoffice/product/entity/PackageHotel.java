package renewal.awesome_travel_backoffice.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import renewal.awesome_travel_backoffice.hotel.entity.Hotel;
import renewal.awesome_travel_backoffice.hotel.utiles.RoomType;

import java.time.LocalDate;

@Entity
@Table(name = "package_hotels")
@Getter
@NoArgsConstructor
public class PackageHotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_hotel_id")
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "package_id")
//    private Package pack;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private LocalDate checkIn;
    private LocalDate checkOut;

    private Integer reservedRooms;  // 확보 객실 수
    private Integer bookedRooms = 0; // 현재까지 예약된 수

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    private Integer price;  // 이 호텔이 해당 패키지에서 적용되는 가격

    public PackageHotel(Package pack, Hotel hotel, LocalDate checkIn, LocalDate checkOut,
                        RoomType roomType, Integer price, Integer reservedRooms) {
        //this.pack = pack;
        this.hotel = hotel;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomType = roomType;
        this.price = price;
        this.reservedRooms = reservedRooms;
        this.bookedRooms = 0;
    }

}

