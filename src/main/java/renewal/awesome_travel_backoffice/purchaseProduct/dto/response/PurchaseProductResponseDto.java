package renewal.awesome_travel_backoffice.purchaseProduct.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import renewal.common.dto.PassengerResponseDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseProductResponseDto {

    private Long productPurchaseId; // 구매 ID
    private ProductResponseDto product; // 상품 정보
    private String purchaseStatus; // 구매 상태
    private Long price; // 결제 금액
    private Long memberId; // 회원 ID
    private String name; // 구매자명
    private String number; // 연락처
    private String email; // 이메일
    private LocalDateTime purchaseDate; // 구매일
    private LocalDateTime paymentDueDate; // 결제 기한
    private List<PassengerResponseDto> passengers; // 승객 목록

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

    // PurchaseProduct 전용 필드들
    private String airlineCode; // 항공사 코드 (ID)
    private String airlineNameKor; // 항공사 한글명
    private String airlineNameEng; // 항공사 영문명
    private LocalDateTime departDateTime; // 출발 일시
    private LocalDateTime returnDateTime; // 귀국 일시
    private Long bak; // 백
    private Long il; // 일
    private Long handlerId; // 담당자 ID
    private String handlerName; // 담당자 이름
    private String handlerPosition; // 담당자 직책
    private String handlerEmail; // 담당자 이메일
    private String handlerNumber; // 담당자 연락처
    private String handlerFax; // 담당자 팩스
    private boolean waiting; // 예약 대기 여부

    private Long passengerActualCount;
    private Long passengerExpectedCount;
    private Long passengerEmptyCount;
    private Long passengerDisplayCount;
}
