package renewal.awesome_travel_backoffice.purchaseProduct.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import renewal.common.entity.PurchaseProduct;

public interface PurchaseProductAdminRepository extends JpaRepository<PurchaseProduct, Long>, JpaSpecificationExecutor<PurchaseProduct> {

    @Override
    @EntityGraph(attributePaths = {"product", "product.tour", "product.tour.country"}, type = EntityGraph.EntityGraphType.LOAD)
    @NonNull
    Page<PurchaseProduct> findAll(@Nullable Specification<PurchaseProduct> spec, @NonNull Pageable pageable);

    @Query("SELECT pp FROM PurchaseProduct pp " +
            "LEFT JOIN FETCH pp.passengers passengers " +
            "WHERE pp.id = :id")
    Optional<PurchaseProduct> findByIdWithPassengers(@Param("id") Long id);

    @Query("SELECT DISTINCT pp FROM PurchaseProduct pp " +
            "LEFT JOIN FETCH pp.finalSeatClasses " +
            "WHERE pp.id = :id")
    Optional<PurchaseProduct> findByIdWithSeatClasses(@Param("id") Long id);

    // 상품별 모든 구매 내역 조회 (List)
    @Query("SELECT pp FROM PurchaseProduct pp WHERE pp.product.id = :productId")
    List<PurchaseProduct> findByProductId(@Param("productId") Long productId);

}

