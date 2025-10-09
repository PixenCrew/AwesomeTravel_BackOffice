package renewal.awesome_travel_backoffice.refund.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.airPurchase.repository.AirPurchaseRepository;
import renewal.awesome_travel_backoffice.productPurchase.repository.ProductPurchaseRepository;
import renewal.awesome_travel_backoffice.refund.repository.RefundRepository;
import renewal.common.entity.AirPurchase;
import renewal.common.entity.BasePurchase.PurchaseStatus;
import renewal.common.entity.ProductPurchase;
import renewal.common.entity.Refund;
import renewal.common.entity.SeatClass;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final AirPurchaseRepository airPurchaseRepository;
    private final ProductPurchaseRepository productPurchaseRepository;

    // 환불 요청 (통합)
    @Transactional
    public Refund requestRefund(Long purchaseId, String reason) {
        // 이미 환불 요청이 있는지 확인
        if (refundRepository.existsByPurchaseId(purchaseId)) {
            throw new IllegalStateException("이미 환불 요청이 있습니다.");
        }

        // AirPurchase에서 먼저 찾기
        AirPurchase airPurchase = airPurchaseRepository.findByProductPurchaseId(purchaseId).orElse(null);
        if (airPurchase != null) {
            // 환불 가능 여부 확인
            if (airPurchase.getPurchaseStatus() != PurchaseStatus.PAID) {
                throw new IllegalStateException("결제 완료된 주문만 환불 가능합니다.");
            }
            return refundRepository.save(new Refund(airPurchase, airPurchase.getPrice(), reason));
        }

        // ProductPurchase에서 찾기
        ProductPurchase productPurchase = productPurchaseRepository.findByProductPurchaseId(purchaseId).orElse(null);
        if (productPurchase != null) {
            // 환불 가능 여부 확인
            if (productPurchase.getPurchaseStatus() != PurchaseStatus.PAID) {
                throw new IllegalStateException("결제 완료된 주문만 환불 가능합니다.");
            }
            return refundRepository.save(new Refund(productPurchase, productPurchase.getPrice(), reason));
        }

        throw new IllegalArgumentException("구매 내역을 찾을 수 없습니다.");
    }

    // 환불 승인 (관리자)
    @Transactional
    public void approveRefund(Long refundId, String adminNote, String processedBy) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 내역을 찾을 수 없습니다."));

        if (refund.getStatus() != Refund.RefundStatus.REQUESTED) {
            throw new IllegalStateException("환불 요청 상태가 아닙니다.");
        }

        refund.setStatus(Refund.RefundStatus.APPROVED);
        refund.setAdminNote(adminNote);
        refund.setProcessedBy(processedBy);

        // 주문 상태는 그대로 PAID 유지 (환불 완료 시에만 변경)
    }

    // 환불 처리 완료 (관리자)
    @Transactional
    public void processRefund(Long refundId, String processedBy) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 내역을 찾을 수 없습니다."));

        if (refund.getStatus() != Refund.RefundStatus.APPROVED) {
            throw new IllegalStateException("환불 승인 상태가 아닙니다.");
        }

        refund.setStatus(Refund.RefundStatus.PROCESSED);
        refund.setProcessDate(LocalDateTime.now());
        refund.setProcessedBy(processedBy);

        // 주문 상태를 환불 완료로 변경
        if (refund.getRefundType() == Refund.RefundType.AIR_PURCHASE) {
            // AirPurchase 환불 처리
            AirPurchase airPurchase = airPurchaseRepository.findByProductPurchaseId(refund.getPurchaseId())
                    .orElseThrow(() -> new IllegalArgumentException("AirPurchase not found"));
            airPurchase.setPurchaseStatus(PurchaseStatus.CANCELLED); // 환불 완료는 취소로 처리

            // 좌석 수 복구
            SeatClass seatClass = airPurchase.getSeatClass();
            seatClass.setAvailableSeats(seatClass.getAvailableSeats() + 
                airPurchase.getAirPassengers().size());

            System.out.printf("[환불 처리 완료 - 항공권] 환불ID=%d, 주문ID=%d, 금액=%d, 처리자=%s\n",
                    refundId, refund.getPurchaseId(), refund.getRefundAmount(), processedBy);
        } else if (refund.getRefundType() == Refund.RefundType.PRODUCT_PURCHASE) {
            // ProductPurchase 환불 처리
            ProductPurchase productPurchase = productPurchaseRepository.findByProductPurchaseId(refund.getPurchaseId())
                    .orElseThrow(() -> new IllegalArgumentException("ProductPurchase not found"));
            productPurchase.setPurchaseStatus(PurchaseStatus.CANCELLED); // 환불 완료는 취소로 처리

            System.out.printf("[환불 처리 완료 - 패키지] 환불ID=%d, 주문ID=%d, 금액=%d, 처리자=%s\n",
                    refundId, refund.getPurchaseId(), refund.getRefundAmount(), processedBy);
        }
    }

    // 환불 거부 (관리자)
    @Transactional
    public void rejectRefund(Long refundId, String reason, String processedBy) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 내역을 찾을 수 없습니다."));

        if (refund.getStatus() != Refund.RefundStatus.REQUESTED) {
            throw new IllegalStateException("환불 요청 상태가 아닙니다.");
        }

        refund.setStatus(Refund.RefundStatus.REJECTED);
        refund.setAdminNote(reason);
        refund.setProcessedBy(processedBy);
        refund.setProcessDate(LocalDateTime.now());

        // 로그 출력
        if (refund.getRefundType() == Refund.RefundType.AIR_PURCHASE) {
            System.out.printf("[환불 거부 - 항공권] 환불ID=%d, 주문ID=%d, 사유=%s, 처리자=%s\n",
                    refundId, refund.getPurchaseId(), reason, processedBy);
        } else if (refund.getRefundType() == Refund.RefundType.PRODUCT_PURCHASE) {
            System.out.printf("[환불 거부 - 패키지] 환불ID=%d, 주문ID=%d, 사유=%s, 처리자=%s\n",
                    refundId, refund.getPurchaseId(), reason, processedBy);
        }
    }

    // 환불 목록 조회
    public Page<Refund> getRefunds(Refund.RefundStatus status, Pageable pageable) {
        if (status != null) {
            return refundRepository.findByStatus(status, pageable);
        }
        return refundRepository.findAll(pageable);
    }

    // 환불 목록 조회 (상태 + 주문유형)
    public Page<Refund> getRefunds(Refund.RefundStatus status, Refund.RefundType refundType, Pageable pageable) {
        System.out.println("=== RefundService.getRefunds 디버깅 ===");
        System.out.println("status: " + status);
        System.out.println("refundType: " + refundType);
        
        Page<Refund> result;
        if (status != null && refundType != null) {
            System.out.println("상태 + 주문유형 필터 적용");
            result = refundRepository.findByStatusAndRefundType(status, refundType, pageable);
        } else if (status != null) {
            System.out.println("상태 필터만 적용");
            result = refundRepository.findByStatus(status, pageable);
        } else if (refundType != null) {
            System.out.println("주문유형 필터만 적용");
            result = refundRepository.findByRefundType(refundType, pageable);
        } else {
            System.out.println("필터 없음 - 전체 조회");
            result = refundRepository.findAll(pageable);
        }
        
        System.out.println("조회된 결과 수: " + result.getTotalElements());
        result.getContent().forEach(refund -> {
            System.out.println("  - 환불ID: " + refund.getId() + ", 상태: " + refund.getStatus() + ", 주문유형: " + refund.getRefundType());
        });
        System.out.println("=====================================");
        
        return result;
    }

    // 구매 ID로 환불 조회
    public Refund getRefundByPurchaseId(Long purchaseId) {
        return refundRepository.findByPurchaseId(purchaseId)
                .orElse(null);
    }

    // 환불 통계
    public long getRefundCountByStatus(Refund.RefundStatus status) {
        return refundRepository.countByStatus(status);
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
