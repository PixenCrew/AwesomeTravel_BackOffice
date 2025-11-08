package renewal.awesome_travel_backoffice.purchaseProduct.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
    private List<ProductPassengerResponseDto> productPassengers; // 승객 목록

    private int expectedPassengerCount;

    private boolean isPassengerInfoComplete;
}
