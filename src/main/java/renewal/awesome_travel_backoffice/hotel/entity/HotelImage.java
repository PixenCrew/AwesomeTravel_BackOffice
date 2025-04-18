package renewal.awesome_travel_backoffice.hotel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class HotelImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    public HotelImage(String url, Hotel hotel) {
        this.url = url;
        this.hotel = hotel;
    }
}

