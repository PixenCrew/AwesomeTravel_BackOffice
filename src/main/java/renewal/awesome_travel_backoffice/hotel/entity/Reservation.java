package renewal.awesome_travel_backoffice.hotel.entity;
import renewal.awesome_travel_backoffice.config.AuditingFields;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Reservation extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn(name = "product_id", nullable = false)
    // private Product product;
    @NonNull
    @Column(nullable = false)
    private Long hotelId; // 호텔 1개에 대해 지나치게 많은 Reservation @ManyToOne - LAZY 로딩 대신 분리방식

    @NonNull
    @Column(nullable = false)
    private Long roomCount;

    @NonNull
    @Column(nullable = false)
    private LocalDate startDate;
    
    @NonNull
    @Column(nullable = false)
    private LocalDate endDate;

    // @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    // private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @NonNull
    @Column(length = 10)
    private Status status;

    public enum Status {
        BOOKED,
        CANCELLED,
        COMPLETED
    }
}
