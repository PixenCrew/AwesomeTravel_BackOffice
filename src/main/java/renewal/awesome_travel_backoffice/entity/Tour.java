package renewal.awesome_travel_backoffice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Tour {
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @Column
  private String company;

  @Column
  private LocalDate date;

  @Builder
    public Tour(Long id, String name, String company, LocalDate date) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.date = date;
    }

}
