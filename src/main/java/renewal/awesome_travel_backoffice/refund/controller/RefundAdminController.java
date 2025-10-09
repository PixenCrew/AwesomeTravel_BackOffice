package renewal.awesome_travel_backoffice.refund.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.refund.service.RefundService;
import renewal.common.entity.Refund;

@RestController
@RequestMapping("/api/admin/refunds")
@RequiredArgsConstructor
public class RefundAdminController {

    private final RefundService refundService;

    // 환불 목록 조회
    @GetMapping
    public ResponseEntity<Page<Refund>> getRefunds(
            @RequestParam(required = false) Refund.RefundStatus status,
            @PageableDefault(size = 20, sort = "requestDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Refund> refunds = refundService.getRefunds(status, pageable);
        return ResponseEntity.ok(refunds);
    }

    // 구매 ID로 환불 조회
    @GetMapping("/purchase/{purchaseId}")
    public ResponseEntity<Refund> getRefundByPurchaseId(@PathVariable Long purchaseId) {
        Refund refund = refundService.getRefundByPurchaseId(purchaseId);
        if (refund != null) {
            return ResponseEntity.ok(refund);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 환불 승인
    @PatchMapping("/{refundId}/approve")
    public ResponseEntity<Void> approveRefund(
            @PathVariable Long refundId,
            @RequestParam String adminNote,
            @RequestParam(defaultValue = "admin") String processedBy
    ) {
        try {
            refundService.approveRefund(refundId, adminNote, processedBy);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 환불 처리 완료
    @PatchMapping("/{refundId}/process")
    public ResponseEntity<Void> processRefund(
            @PathVariable Long refundId,
            @RequestParam(defaultValue = "admin") String processedBy
    ) {
        try {
            refundService.processRefund(refundId, processedBy);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 환불 거부
    @PatchMapping("/{refundId}/reject")
    public ResponseEntity<Void> rejectRefund(
            @PathVariable Long refundId,
            @RequestParam String reason,
            @RequestParam(defaultValue = "admin") String processedBy
    ) {
        try {
            refundService.rejectRefund(refundId, reason, processedBy);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 환불 통계
    @GetMapping("/stats")
    public ResponseEntity<RefundStats> getRefundStats() {
        RefundStats stats = new RefundStats();
        stats.setRequestedCount(refundService.getRefundCountByStatus(Refund.RefundStatus.REQUESTED));
        stats.setApprovedCount(refundService.getRefundCountByStatus(Refund.RefundStatus.APPROVED));
        stats.setProcessedCount(refundService.getRefundCountByStatus(Refund.RefundStatus.PROCESSED));
        stats.setRejectedCount(refundService.getRefundCountByStatus(Refund.RefundStatus.REJECTED));
        return ResponseEntity.ok(stats);
    }

    // 환불 통계 DTO
    public static class RefundStats {
        private long requestedCount;
        private long approvedCount;
        private long processedCount;
        private long rejectedCount;

        // Getters and Setters
        public long getRequestedCount() { return requestedCount; }
        public void setRequestedCount(long requestedCount) { this.requestedCount = requestedCount; }
        public long getApprovedCount() { return approvedCount; }
        public void setApprovedCount(long approvedCount) { this.approvedCount = approvedCount; }
        public long getProcessedCount() { return processedCount; }
        public void setProcessedCount(long processedCount) { this.processedCount = processedCount; }
        public long getRejectedCount() { return rejectedCount; }
        public void setRejectedCount(long rejectedCount) { this.rejectedCount = rejectedCount; }
    }
}



