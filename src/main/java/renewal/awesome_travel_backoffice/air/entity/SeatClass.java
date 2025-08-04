package renewal.awesome_travel_backoffice.air.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import renewal.awesome_travel_backoffice.air.utiles.SeatClassType;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table
public class SeatClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "air_id", nullable = false)
    private Air air;
    @Enumerated(EnumType.STRING) // Enum을 DB에 문자열로 저장
    private SeatClassType classType; // 좌석 등급
    private Long price; // 해당 등급의 가격
    private Long maxSeats; // 해당 등급의 최대 좌석 수
    private Long availableSeats; // 잔여 좌석 수

    @OneToMany(mappedBy = "seatClass", cascade = CascadeType.ALL)
    private List<AirReservation> airReservations = new ArrayList<>();

    public SeatClass(Air air, SeatClassType classType, long price, long maxSeats, long availableSeats) {
        this.air = air;
        this.classType = classType;
        this.price = price;
        this.maxSeats = maxSeats;
        this.availableSeats = availableSeats;
    }

    public void reserveSeats(Long requiredPersons) throws Exception{
        if (availableSeats>=requiredPersons) {
            availableSeats -= requiredPersons;
        } else{
            throw new Exception("잔여 좌석 에러");
        }
    }

}
