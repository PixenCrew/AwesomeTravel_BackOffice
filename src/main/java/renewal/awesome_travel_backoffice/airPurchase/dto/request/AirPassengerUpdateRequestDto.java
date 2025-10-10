package renewal.awesome_travel_backoffice.airPurchase.dto.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AirPassengerUpdateRequestDto {

    private String name;
    private String number;
    private String email;
    private LocalDate birth;
    private String sex;
    private String nationality;
    private String passportNum;
    private String lastName;
    private String firstName;
    private LocalDate expire;
}

