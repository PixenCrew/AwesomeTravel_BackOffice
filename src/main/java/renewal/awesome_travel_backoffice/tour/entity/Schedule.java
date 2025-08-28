package renewal.awesome_travel_backoffice.tour.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Schedule{
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // private Long day; // 1일차, 2일차 ...

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn
    private List<Location> locations = new ArrayList<>();
    
    // // 해당 날짜의 숙소
    // @OneToOne(fetch = FetchType.LAZY, optional = true)
    // @JoinColumn(name = "hotel_id")
    // private Hotel hotel;
}
