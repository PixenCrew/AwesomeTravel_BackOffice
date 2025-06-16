package renewal.awesome_travel_backoffice.air.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;
import renewal.awesome_travel_backoffice.air.utiles.FlightType;
import renewal.awesome_travel_backoffice.config.AuditingFields;

import java.util.ArrayList;
import java.util.List;

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
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_code", nullable = false)
    private Airline airline;

    @Column(nullable = false)
    private String depart;

    @Column(nullable = false)
    private String depart_time;

    @Column(nullable = false)
    private String arrive;

    @Column(nullable = false)
    private String arrive_time;

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
    public Air(String code, Airline airline, String depart, String depart_time, String arrive, String arrive_time) {
        this.code = code;
        this.airline = airline;
        this.depart = depart;
        this.depart_time = depart_time;
        this.arrive = arrive;
        this.arrive_time = arrive_time;
        this.status = AirStatus.ACTIVE; // 예시: 기본 상태
        this.stopovers = 0;              // 예시: 기본 경유 없음
        this.flightType = FlightType.DIRECT; // 예시: 기본 직항 처리
    }
    //수동생성 생성자
    public Air(String code, Airline airline, String depart, String depart_time, String arrive, String arrive_time, Integer stopovers, FlightType flightType) {
        this.code = code;
        this.airline = airline;
        this.depart = depart;
        this.depart_time = depart_time;
        this.arrive = arrive;
        this.arrive_time = arrive_time;
        this.stopovers = stopovers;
        this.flightType = flightType;
    }

    public void updateAir(String code, Airline airline, String depart, String depart_time, String arrive, String arrive_time, Integer stopovers, FlightType flightType) {
        this.code = code;
        this.airline = airline;
        this.depart = depart;
        this.depart_time = depart_time;
        this.arrive = arrive;
        this.arrive_time = arrive_time;
        this.stopovers = stopovers;
        this.flightType = flightType;
    }

    // public void setCode(String code) {
    //     this.code = code;
    // }

    // public void setAirline(Airline airline) {
    //     this.airline = airline;
    // }

    // public void setDepart(String depart) {
    //     this.depart = depart;
    // }

    // public void setDepart_time(String depart_time) {
    //     this.depart_time = depart_time;
    // }

    // public void setArrive(String arrive) {
    //     this.arrive = arrive;
    // }

    // public void setArrive_time(String arrive_time) {
    //     this.arrive_time = arrive_time;
    // }

    // public void setStopovers(Integer stopovers) {
    //     this.stopovers = stopovers;
    // }

    // public void setFlightType(FlightType flightType) {
    //     this.flightType = flightType;
    // }

    // public void updateStatus(AirStatus newStatus) {
    //     this.status = newStatus;
    // }

    public void addSeatClass(SeatClass seatClass) {
        this.seatClasses.add(seatClass);
    }

    public void clearSeatClasses() {
        this.seatClasses.clear();
    }

    public void removeSeatClass(SeatClass seatClass) {
        this.seatClasses.remove(seatClass);
        seatClass.setAir(null);
    }

}