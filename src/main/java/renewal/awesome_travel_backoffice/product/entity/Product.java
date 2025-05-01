package renewal.awesome_travel_backoffice.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table
@Getter 
@Setter
@NoArgsConstructor
public class Product {
    @Id
    private int id;
}
