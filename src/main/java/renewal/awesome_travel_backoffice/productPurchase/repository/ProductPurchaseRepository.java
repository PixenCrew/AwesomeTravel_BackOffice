package renewal.awesome_travel_backoffice.productPurchase.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.ProductPurchase;
import renewal.common.entity.BasePurchase.PurchaseStatus;

public interface ProductPurchaseRepository extends JpaRepository<ProductPurchase, Long>, ProductPurchaseRepositoryCustom {

    Page<ProductPurchase> findByPurchaseStatusAndPaymentDueDateBefore(
            PurchaseStatus status,
            LocalDateTime time,
            Pageable pageable
    );

    Optional<ProductPurchase> findByProductPurchaseId(Long productPurchaseId);

    @Query("SELECT pp FROM ProductPurchase pp " +
           "LEFT JOIN FETCH pp.productPassengers passengers " +
           "WHERE pp.productPurchaseId = :id")
    Optional<ProductPurchase> findByIdWithPassengers(@Param("id") Long id);

    // 상품별 구매 내역 조회
    @Query("SELECT pp FROM ProductPurchase pp WHERE pp.product.id = :productId")
    Page<ProductPurchase> findByProductId(@Param("productId") Long productId, Pageable pageable);

    // 회원별 구매 내역 조회
    @Query("SELECT pp FROM ProductPurchase pp WHERE pp.member_id = :memberId")
    Page<ProductPurchase> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // 상품별 모든 구매 내역 조회 (List)
    @Query("SELECT pp FROM ProductPurchase pp WHERE pp.product.id = :productId")
    List<ProductPurchase> findByProductId(@Param("productId") Long productId);
    
    // 상품별 구매 내역 삭제
    @Query("DELETE FROM ProductPurchase pp WHERE pp.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
