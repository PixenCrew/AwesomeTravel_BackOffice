package renewal.awesome_travel_backoffice.hotel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import renewal.awesome_travel_backoffice.product.entity.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn(name = "product_id", nullable = false)
    // private Product product;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "room_count", nullable = false)
    private Long roomCount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Status status;

    public enum Status {
        BOOKED,
        CANCELLED,
        COMPLETED
    }
}
