package renewal.awesome_travel_backoffice.purchaseProduct.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseProductSearchCondition {

    private Long productPurchaseId; // 구매 ID
    private String purchaseStatus; // 구매 상태 (HOLDING, PAID, CANCELLED)
    private Long memberId; // 회원 ID
    private Long productId; // 상품 ID
    private LocalDateTime purchaseDateFrom; // 구매일 시작
    private LocalDateTime purchaseDateTo; // 구매일 종료
    private Long minPrice; // 최소 가격
    private Long maxPrice; // 최대 가격
    private String customerName; // 고객명
    private String customerEmail; // 고객 이메일
    private String productTitle; // 상품명
}
