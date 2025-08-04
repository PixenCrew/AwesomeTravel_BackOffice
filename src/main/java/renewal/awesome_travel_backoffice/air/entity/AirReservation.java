package renewal.awesome_travel_backoffice.air.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.config.AuditingFields;

@Entity
@Table
@Getter
@Setter
@RequiredArgsConstructor
public class AirReservation extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "seat_class_id")
    private SeatClass seatClass;

    // @NonNull
    // @ManyToOne
    // @JoinColumn(name = "tour_id")
    // private Tour tour;
    @NonNull
    private Long tourId;

    @NonNull
    private Long seatCount;

    @Enumerated(EnumType.STRING)
    @NonNull
    private Status status;

    public enum Status {
        BOOKED,
        CANCELLED,
        COMPLETED
    }
    
}
