package renewal.awesome_travel_backoffice.hotel.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Entity
@Table
@Getter
@Setter
@RequiredArgsConstructor
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @ManyToMany(mappedBy = "amenities")
    private Set<Hotel> hotels = new HashSet<>();

}
