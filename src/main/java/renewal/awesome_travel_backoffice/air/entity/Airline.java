package renewal.awesome_travel_backoffice.air.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Airline {

    @Id
    @Column(name = "airline_code",length = 10)
    private String code; // 예: "KE", "OZ"

    private String nameKor;
    private String nameEng;

    private boolean infantSeatsRequired;
}

