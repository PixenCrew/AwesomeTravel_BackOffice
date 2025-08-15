package renewal.awesome_travel_backoffice.product.controller;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import renewal.awesome_travel_backoffice.code.CityCodeRepository;
import renewal.awesome_travel_backoffice.country.repository.CountryRepository;
import renewal.awesome_travel_backoffice.product.dto.ProductFilterDTO;
import renewal.awesome_travel_backoffice.product.entity.Product;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.product.repository.ProductRepository;
import renewal.awesome_travel_backoffice.product.service.ProductService;

import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@RequestMapping("/product")
@Controller
public class ProductController {

    private final ProductRepository productRepo;
    private final ProductService productService;
    private final CountryRepository countryRepo;
    private final CityCodeRepository cityRepo;

    @GetMapping
    public String listAndFilter(
            @ModelAttribute("filter") ProductFilterDTO filter, // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        // 1) 정렬 객체 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // 2) 페이징(50개 고정) + 필터링 로직
        Pageable pageable = PageRequest.of(page, 50, sort);
        Page<Product> productPage = productService.searchProducts(filter, pageable);

        // 3) View에서 쓸 속성들
        model.addAttribute("countryCode", countryRepo.findAll());
        model.addAttribute("cityCode", cityRepo.findAll());
        model.addAttribute("productPage", productPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Product List");
        model.addAttribute("content", "components/product"); // layout 안에서 이 fragment를 렌더

        return "layout";
    }
    
    @GetMapping("/new")
    public String newProduct(Model model){
        model.addAttribute("countryCode", countryRepo.findAll());
        model.addAttribute("cityCode", cityRepo.findAll());
        model.addAttribute("content", "components/productDetail");

        return "layout";
    }

    @GetMapping("/test")
    public ResponseEntity<Void> test() {
        Product a = new Product();
        a.setStar1(5L);
        a.setStar2(43L);
        a.setStar3(23L);
        a.setStar4(98L);
        a.setStar5(122L);
        // a.setTotalReview(5L+43L+23L+98L+122L);
        a.UpdateAvg(2);
        productRepo.save(a);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test2")
    public ResponseEntity<Void> test2() {
        List<Product> list = productRepo.findAll();
        for (Product product : list) {
            System.out.println("product.getId() = " + product.getId());
            // System.out.println("product.getAvgerageReview() = " + product.getAvgerageReview());
            System.out.println("product.getStar1() = " + product.getStar1());
            System.out.println("product.getStar2() = " + product.getStar2());
            System.out.println("product.getStar3() = " + product.getStar3());
            System.out.println("product.getStar4() = " + product.getStar4());
            System.out.println("product.getStar5() = " + product.getStar5());
        }
        return ResponseEntity.ok().build();
    }
}
