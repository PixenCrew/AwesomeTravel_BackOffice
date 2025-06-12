package renewal.awesome_travel_backoffice.product.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Review {

    @Id
    private Long id;
    private Long user_id;
    private String name;

    private LocalDate date;
    private Integer star;
    private String review;
}
