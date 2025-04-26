package renewal.awesome_travel_backoffice.tour.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Tour {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String company;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate startdate;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate enddate;
  
  private Long price;

  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Point> course = new ArrayList<>();

  // // 양방향 관계 설정용 편의 메서드
  // public void addCoursePoint(Point point) {
  //   course.add(point);
  //   point.setTour(this);
  // }

  // public void removeCoursePoint(Point point) {
  //   course.remove(point);
  //   point.setTour(null);
  // }
}
