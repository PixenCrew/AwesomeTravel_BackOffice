package renewal.awesome_travel_backoffice.product.repository;

import renewal.awesome_travel_backoffice.product.entity.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{
    
    List<Product> findByTourId(Long id);

}
