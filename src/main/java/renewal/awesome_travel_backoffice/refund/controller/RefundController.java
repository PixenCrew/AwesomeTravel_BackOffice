package renewal.awesome_travel_backoffice.refund.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.refund.service.RefundService;
import renewal.common.entity.Refund;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    // 환불 요청 (통합)
    @PostMapping("/request/{purchaseId}")
    public ResponseEntity<Refund> requestRefund(
            @PathVariable Long purchaseId,
            @RequestParam String reason
    ) {
        try {
            Refund refund = refundService.requestRefund(purchaseId, reason);
            return ResponseEntity.ok(refund);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 환불 상태 조회 (AirPurchase)
    @GetMapping("/purchase/{purchaseId}")
    public ResponseEntity<Refund> getRefundStatus(@PathVariable Long purchaseId) {
        Refund refund = refundService.getRefundByPurchaseId(purchaseId);
        if (refund != null) {
            return ResponseEntity.ok(refund);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
