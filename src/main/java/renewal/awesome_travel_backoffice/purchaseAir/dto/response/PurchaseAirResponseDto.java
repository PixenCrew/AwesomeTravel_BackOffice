package renewal.awesome_travel_backoffice.purchaseAir.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import renewal.common.entity.PurchaseBase.PurchaseStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseAirResponseDto {

    private Long id;

    private PurchaseStatus status;

    private Long price;

    private Long member_id;

    private String name;

    private String number;

    private String email;

    private LocalDateTime purchaseDate;

    private LocalDateTime paymentDueDate;

    // PurchaseBase 필드들
    private String title; // 구매 제목
    private Long finalPriceAdult; // 확정 성인 가격
    private Long finalPriceYouth; // 확정 청소년 가격
    private Long finalPriceInfant; // 확정 영유아 가격
    private Long adultCount; // 성인 수
    private Long youthCount; // 청소년 수
    private Long infantCount; // 영유아 수
    private boolean isPassengerInfoComplete; // 승객 정보 입력 완료 여부
    private LocalDateTime passengerInfoDeadline; // 승객 정보 입력 기한
    private boolean transactionComplete; // 결제 완료 여부
    private Long completedPassengerCount; // 완료된 승객 수

    // 항공사 정보
    private String airlineCode;
    private String airlineNameKor;
    private String airlineNameEng;

}
