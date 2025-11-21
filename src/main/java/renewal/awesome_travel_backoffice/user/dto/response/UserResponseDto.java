package renewal.awesome_travel_backoffice.user.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import renewal.common.entity.User.UserProvider;
import renewal.common.entity.User.UserRole;
import renewal.common.entity.User.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
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
    private Map<String, Boolean> terms;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
