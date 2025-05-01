package renewal.awesome_travel_backoffice.hotel.entity;

import jakarta.persistence.*;
import renewal.awesome_travel_backoffice.product.entity.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "room_count", nullable = false)
    private Long roomCount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // @Enumerated(EnumType.STRING)
    // @Column(length = 10, nullable = false)
    // private Status status;

    // public enum Status {
    //     BOOKED,
    //     CANCELLED,
    //     COMPLETED
    // }
}
