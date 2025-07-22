package renewal.awesome_travel_backoffice.air.entity;

import org.springframework.lang.NonNull;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import renewal.awesome_travel_backoffice.air.utiles.SeatClassType;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table
public class SeatClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "air_id", nullable = false)
    @NonNull
    private Air air;

    @Enumerated(EnumType.STRING) // Enum을 DB에 문자열로 저장
    @NonNull
    private SeatClassType classType; // 좌석 등급

    @NonNull
    private Long price; // 해당 등급의 가격

    @NonNull
    private Long maxSeats; // 해당 등급의 최대 좌석 수
    
    @NonNull
    private Long availableSeats; // 잔여 좌석 수

}
