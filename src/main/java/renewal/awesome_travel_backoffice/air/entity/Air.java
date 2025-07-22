package renewal.awesome_travel_backoffice.air.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;
import renewal.awesome_travel_backoffice.air.utiles.FlightType;
import renewal.awesome_travel_backoffice.config.AuditingFields;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Air extends AuditingFields {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String flightNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_code", nullable = false)
    private Airline airline;

    @Column(nullable = false)
    private String departAirport;

    @Column(nullable = false)
    private LocalDate departDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(nullable = false)
    private LocalTime departTime;

    @Column(nullable = false)
    private String arriveAirport;

    @Column(nullable = false)
    private LocalDate arriveDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(nullable = false)
    private LocalTime arriveTime;

    @Column(nullable = false)
    private Integer stopovers = 0; // 경유 횟수 (0 = 직항, 1 이상 = 경유)

    @ElementCollection
    private List<String> stopoverList; // 경유지 없으면 null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AirStatus status = AirStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightType flightType = FlightType.DIRECT;

    @OneToMany(mappedBy = "air", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatClass> seatClasses = new ArrayList<>();

    //공공데이터용 생성자
    public Air(String flightNumber, Airline airline, String departAirport, LocalDate departDate, LocalTime departTime, String arriveAirport, LocalDate arriveDate, LocalTime arriveTime) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.departAirport = departAirport;
        this.departDate = departDate;
        this.departTime = departTime;
        this.arriveAirport = arriveAirport;
        this.arriveDate = arriveDate;
        this.arriveTime = arriveTime;
    }

    public Air updateAir(Integer stopovers, List<SeatClass> seatClasses){
        this.status = AirStatus.ACTIVE;
        this.stopovers = stopovers;
        if(stopovers == 0){
            this.flightType = FlightType.DIRECT;
        } else {
            this.flightType = FlightType.STOP_OVER;
        }
        this.seatClasses = seatClasses;
        return this;
    }
}