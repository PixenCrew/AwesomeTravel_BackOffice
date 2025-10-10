package renewal.awesome_travel_backoffice.productPurchase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductPassengerResponseDto {

    private Long id;                // 승객 ID
    private String name;            // 이름
    private String number;          // 연락처
    private String email;           // 이메일
    private LocalDate birth;        // 생년월일
    private String sex;             // 성별
    private String nationality;     // 국적 코드
    private String passportNum;     // 여권번호
    private String lastName;        // 영문 성
    private String firstName;       // 영문 이름
    private LocalDate expire;       // 여권 만료일
    private List<String> specialRequests;  // 특별 요청사항
}



