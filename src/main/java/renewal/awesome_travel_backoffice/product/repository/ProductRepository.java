package renewal.awesome_travel_backoffice.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import renewal.common.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{
    
    Product findByTourId(Long id);
    
    // 사용 중인 tour_id 목록 조회
    @Query("SELECT p.tour.id FROM Product p WHERE p.tour.id IS NOT NULL")
    List<Long> findUsedTourIds();

}
