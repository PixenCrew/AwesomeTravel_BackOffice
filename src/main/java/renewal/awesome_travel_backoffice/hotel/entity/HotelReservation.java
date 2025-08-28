package renewal.awesome_travel_backoffice.hotel.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import renewal.awesome_travel_backoffice.config.AuditingFields;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class HotelReservation extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NonNull
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
    
    @NonNull
    private Long tourId;

    @NonNull
    private Long roomCount;

    @NonNull
    private LocalDate startDate;
    
    @NonNull
    private LocalDate endDate;

    // @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    // private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @NonNull
    private Status status;

    public enum Status {
        BOOKED,
        CANCELLED,
        COMPLETED
    }
    
}
