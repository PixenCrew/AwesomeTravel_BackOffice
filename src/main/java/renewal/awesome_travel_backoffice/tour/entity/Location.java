package renewal.awesome_travel_backoffice.tour.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import renewal.awesome_travel_backoffice.tour.utiles.Type;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Location{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    // Type이 AIR면 사용할 필드
    private Long air;

    // private String country;
    private String city;
    private String description;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
