package renewal.awesome_travel_backoffice.tour.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import org.springframework.format.annotation.DateTimeFormat;

import renewal.awesome_travel_backoffice.config.AuditingFields;

import lombok.*;

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
  private LocalDate startDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;

  private Long price;

  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OrderColumn
  private List<Schedule> schedules = new ArrayList<>();
  
  // @OneToMany(mappedBy = "seat_class", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  // private List<AirReservation> airReservations = new ArrayList<>();

  private Long airPriceSum;
  private Long hotelPriceSum;

  // 패키지 상품과 연결된 경우 패키지 ID
  private Long productId;

}
