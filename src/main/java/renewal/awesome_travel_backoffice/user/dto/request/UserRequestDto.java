package renewal.awesome_travel_backoffice.user.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import renewal.common.entity.User.UserProvider;
import renewal.common.entity.User.UserRole;
import renewal.common.entity.User.UserStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    private String email;
    private String name;
    private String phone;
    private LocalDate birthDate;
    private UserProvider provider;
    private String socialId;
    private UserRole role;
    private UserStatus status;
    private String passportNumber;
    private LocalDate passportIssuedDate;
    private LocalDate passportExpiryDate;
    private String passportCountry;
    private String englishFirstName;
    private String englishLastName;
    private Boolean emailVerified;
}




