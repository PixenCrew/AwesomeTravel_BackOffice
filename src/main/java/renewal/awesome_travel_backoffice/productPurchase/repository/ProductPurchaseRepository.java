package renewal.awesome_travel_backoffice.productPurchase.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.PurchaseProduct;
import renewal.common.entity.PurchaseBase.PurchaseStatus;

public interface ProductPurchaseRepository extends JpaRepository<PurchaseProduct, Long>, ProductPurchaseRepositoryCustom {

    Page<PurchaseProduct> findByPurchaseStatusAndPaymentDueDateBefore(
            PurchaseStatus status,
            LocalDateTime time,
            Pageable pageable
    );

    // 기존 코드 호환성을 위한 메서드 (실제로는 findById와 동일)
    default Optional<PurchaseProduct> findByPurchaseProductId(Long productPurchaseId) {
        return findById(productPurchaseId);
    }

    @Query("SELECT pp FROM PurchaseProduct pp " +
           "LEFT JOIN FETCH pp.productPassengers passengers " +
           "WHERE pp.id = :id")
    Optional<PurchaseProduct> findByIdWithPassengers(@Param("id") Long id);

    // 상품별 구매 내역 조회
    @Query("SELECT pp FROM PurchaseProduct pp WHERE pp.product.id = :productId")
    Page<PurchaseProduct> findByProductId(@Param("productId") Long productId, Pageable pageable);

    // 회원별 구매 내역 조회
    @Query("SELECT pp FROM PurchaseProduct pp WHERE pp.user.id = :memberId")
    Page<PurchaseProduct> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // 상품별 모든 구매 내역 조회 (List)
    @Query("SELECT pp FROM PurchaseProduct pp WHERE pp.product.id = :productId")
    List<PurchaseProduct> findByProductId(@Param("productId") Long productId);
    
    // 상품별 구매 내역 삭제
    @Query("DELETE FROM PurchaseProduct pp WHERE pp.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
