package renewal.awesome_travel_backoffice.product.controller;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import renewal.awesome_travel_backoffice.product.entity.Product;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.product.repository.ProductRepository;

@RequiredArgsConstructor
@RequestMapping("/product")
@Controller
public class ProductController {

    private final ProductRepository productRepo;

    @GetMapping("/test")
    public ResponseEntity<Void> test() {
        Product a = new Product();
        a.setStar1(5L);
        a.setStar2(43L);
        a.setStar3(23L);
        a.setStar4(98L);
        a.setStar5(122L);
        a.setTotalReview(5L+43L+23L+98L+122L);
        a.UpdateAvg(2);
        productRepo.save(a);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test2")
    public ResponseEntity<Void> test2() {
        List<Product> list = productRepo.findAll();
        for (Product product : list) {
            System.out.println("product.getId() = " + product.getId());
            System.out.println("product.getAvgerageReview() = " + product.getAvgerageReview());
            System.out.println("product.getStar1() = " + product.getStar1());
            System.out.println("product.getStar2() = " + product.getStar2());
            System.out.println("product.getStar3() = " + product.getStar3());
            System.out.println("product.getStar4() = " + product.getStar4());
            System.out.println("product.getStar5() = " + product.getStar5());
        }
        return ResponseEntity.ok().build();
    }
}
