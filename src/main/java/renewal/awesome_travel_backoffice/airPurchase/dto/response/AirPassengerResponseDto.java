package renewal.awesome_travel_backoffice.airPurchase.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AirPassengerResponseDto {

    private Long id;
    private String name;
    private String number;
    private String email;
    private LocalDate birth;
    private String sex;
    private String nationality;  // 예: "KOR"
    private String passportNum;
    private String lastName;
    private String firstName;
    private LocalDate expire;

    private List<String> specialRequests; // "Wheelchair", "Vegetarian" 등
}

