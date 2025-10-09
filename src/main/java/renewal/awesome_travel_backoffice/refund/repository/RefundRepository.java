package renewal.awesome_travel_backoffice.refund.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import renewal.common.entity.Refund;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    // 구매 ID로 환불 조회
    Optional<Refund> findByPurchaseId(Long purchaseId);

    // 상태별 환불 목록 조회
    Page<Refund> findByStatus(Refund.RefundStatus status, Pageable pageable);

    // 기간별 환불 목록 조회
    @Query("SELECT r FROM Refund r WHERE r.requestDate BETWEEN :startDate AND :endDate")
    Page<Refund> findByRequestDateBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate, 
                                         Pageable pageable);

    // 환불 요청이 있는지 확인
    boolean existsByPurchaseId(Long purchaseId);

    // 주문유형별 환불 목록 조회
    Page<Refund> findByRefundType(Refund.RefundType refundType, Pageable pageable);

    // 상태 + 주문유형별 환불 목록 조회
    Page<Refund> findByStatusAndRefundType(Refund.RefundStatus status, Refund.RefundType refundType, Pageable pageable);

    // 환불 상태별 통계
    @Query("SELECT COUNT(r) FROM Refund r WHERE r.status = :status")
    long countByStatus(@Param("status") Refund.RefundStatus status);
}
