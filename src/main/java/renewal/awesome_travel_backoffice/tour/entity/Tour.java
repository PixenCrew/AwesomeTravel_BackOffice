package renewal.awesome_travel_backoffice.tour.entity;

import jakarta.persistence.*;
import lombok.*;
import renewal.awesome_travel_backoffice.config.AuditingFields;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Tour extends AuditingFields{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String company;
  private String name;
  private String country;
  private Long count;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate startdate;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate enddate;

  private Long price;

  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OrderColumn
  private List<Schedule> schedules = new ArrayList<>();
  
  // @OneToMany(mappedBy = "seat_class", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  // private List<AirReservation> airReservations = new ArrayList<>();

}
