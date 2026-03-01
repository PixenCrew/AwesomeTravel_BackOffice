package renewal.awesome_travel_backoffice.refund.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.awesome_travel_backoffice.air.repository.SeatClassAdminRepository;
import renewal.common.repository.PurchaseAirRepository;
import renewal.common.repository.PurchaseProductRepository;
import renewal.common.service.ProductServiceCommon;
import renewal.awesome_travel_backoffice.refund.repository.RefundBackOfficeRepository;
import renewal.common.entity.Air;
import renewal.common.entity.PurchaseAir;
import renewal.common.entity.PurchaseBase;
import renewal.common.entity.PurchaseBase.PurchaseStatus;
import renewal.common.entity.PurchaseProduct;
import renewal.common.entity.Refund;
import renewal.common.entity.SeatClass;

@Service
@RequiredArgsConstructor
public class RefundService {

    private static final Logger log = LoggerFactory.getLogger(RefundService.class);

    private final RefundBackOfficeRepository refundRepository;
    private final PurchaseAirRepository purchaseAirRepository;
    private final PurchaseProductRepository productPurchaseRepository;
    private final ProductServiceCommon productServiceCommon;
    private final AirRepository airRepository;
    private final SeatClassAdminRepository seatClassAdminRepository;

    // 환불 요청 (통합)
    @Transactional
    public Refund requestRefund(Long purchaseId, String reason) {
        // 이미 환불 요청이 있는지 확인
        if (refundRepository.existsByPurchaseId(purchaseId)) {
            throw new IllegalStateException("이미 환불 요청이 있습니다.");
        }

        // PurchaseAir에서 먼저 찾기
        PurchaseAir purchaseAir = purchaseAirRepository.findById(purchaseId).orElse(null);
        if (purchaseAir != null) {
            // 환불 가능 여부 확인
            if (purchaseAir.getPurchaseStatus() != PurchaseStatus.PAID) {
                throw new IllegalStateException("결제 완료된 주문만 환불 가능합니다.");
            }
            return refundRepository.save(new Refund(purchaseAir, purchaseAir.getPrice(), reason));
        }

        // PurchaseProduct에서 찾기
        PurchaseProduct productPurchase = productPurchaseRepository.findById(purchaseId).orElse(null);
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

        refund.approve(adminNote);

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

        refund.complete();

        // 주문 상태를 환불 완료로 변경
        if (refund.getRefundType() == Refund.RefundType.AIR) {
            // PurchaseAir 환불 처리
            PurchaseAir airPurchase = purchaseAirRepository.findById(refund.getPurchaseId())
                    .orElseThrow(() -> new IllegalArgumentException("PurchaseAir not found"));
            airPurchase.setPurchaseStatus(PurchaseStatus.CANCELLED); // 환불 완료는 취소로 처리

            // 좌석 수 복구 - finalSeatClasses에서 정보를 가져와서 실제 SeatClass를 찾아 복구
            if (airPurchase.getFinalSeatClasses() != null && !airPurchase.getFinalSeatClasses().isEmpty()) {
                PurchaseBase.ConfirmedSeatClass confirmedSeatClass = airPurchase.getFinalSeatClasses().get(0);
                Air air = airRepository.findById(confirmedSeatClass.getAirId())
                        .orElseThrow(() -> new IllegalArgumentException("Air not found"));
                SeatClass seatClass = seatClassAdminRepository.findByAirAndClassType(air, confirmedSeatClass.getClassType())
                        .orElseThrow(() -> new IllegalArgumentException("SeatClass not found"));
                
                // 좌석 수 복구 (성인, 청소년, 영유아 모두 합산)
                long totalSeats = (confirmedSeatClass.getSeatCountAdult() != null ? confirmedSeatClass.getSeatCountAdult() : 0) +
                                 (confirmedSeatClass.getSeatCountYouth() != null ? confirmedSeatClass.getSeatCountYouth() : 0) +
                                 (confirmedSeatClass.getSeatCountInfant() != null ? confirmedSeatClass.getSeatCountInfant() : 0);
                seatClass.setAvailableSeats(seatClass.getAvailableSeats() + totalSeats);
            }

            log.info("[환불 처리 완료 - 항공권] refundId={}, purchaseId={}, amount={}, processedBy={}",
                    refundId, refund.getPurchaseId(), refund.getAmount(), processedBy);
        } else if (refund.getRefundType() == Refund.RefundType.PRODUCT) {
            // PurchaseProduct 환불 처리: 취소 처리로 좌석 복원(또는 홀드 allocated 감소)까지 수행
            productServiceCommon.cancelPurchase(refund.getPurchaseId());

            log.info("[환불 처리 완료 - 패키지] refundId={}, purchaseId={}, amount={}, processedBy={}",
                    refundId, refund.getPurchaseId(), refund.getAmount(), processedBy);
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
        refund.reject(reason);

        if (refund.getRefundType() == Refund.RefundType.AIR) {
            log.info("[환불 거부 - 항공권] refundId={}, purchaseId={}, reason={}, processedBy={}",
                    refundId, refund.getPurchaseId(), reason, processedBy);
        } else if (refund.getRefundType() == Refund.RefundType.PRODUCT) {
            log.info("[환불 거부 - 패키지] refundId={}, purchaseId={}, reason={}, processedBy={}",
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
        if (log.isDebugEnabled()) {
            log.debug("getRefunds: status={}, refundType={}", status, refundType);
        }

        Page<Refund> result;
        if (status != null && refundType != null) {
            result = refundRepository.findByStatusAndRefundType(status, refundType, pageable);
        } else if (status != null) {
            result = refundRepository.findByStatus(status, pageable);
        } else if (refundType != null) {
            result = refundRepository.findByRefundType(refundType, pageable);
        } else {
            result = refundRepository.findAll(pageable);
        }

        if (log.isDebugEnabled()) {
            log.debug("getRefunds 결과: totalElements={}", result.getTotalElements());
        }

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
        public long getRequestedCount() {
            return requestedCount;
        }

        public void setRequestedCount(long requestedCount) {
            this.requestedCount = requestedCount;
        }

        public long getApprovedCount() {
            return approvedCount;
        }

        public void setApprovedCount(long approvedCount) {
            this.approvedCount = approvedCount;
        }

        public long getProcessedCount() {
            return processedCount;
        }

        public void setProcessedCount(long processedCount) {
            this.processedCount = processedCount;
        }

        public long getRejectedCount() {
            return rejectedCount;
        }

        public void setRejectedCount(long rejectedCount) {
            this.rejectedCount = rejectedCount;
        }
    }
}
