package renewal.awesome_travel_backoffice.product.repository;

import renewal.awesome_travel_backoffice.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>{
    
}
