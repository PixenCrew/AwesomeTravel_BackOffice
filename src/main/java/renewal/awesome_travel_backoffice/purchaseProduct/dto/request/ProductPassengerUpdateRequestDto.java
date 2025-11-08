package renewal.awesome_travel_backoffice.purchaseProduct.dto.request;

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
public class ProductPassengerUpdateRequestDto {

    private String name; // 이름
    private String number; // 연락처
    private String email; // 이메일
    private LocalDate birth; // 생년월일
    private String sex; // 성별 (MALE, FEMALE)
    private String nationality; // 국적
    private String passportNum; // 여권번호
    private String lastName; // 영문 성
    private String firstName; // 영문 이름
    private LocalDate expire; // 여권 만료일
}
