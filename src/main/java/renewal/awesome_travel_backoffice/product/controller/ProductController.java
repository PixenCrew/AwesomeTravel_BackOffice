package renewal.awesome_travel_backoffice.product.controller;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import renewal.awesome_travel_backoffice.product.dto.ProductFilterDTO;
import renewal.awesome_travel_backoffice.product.repository.ProductRepository;
import renewal.awesome_travel_backoffice.product.service.ProductService;
import renewal.awesome_travel_backoffice.tour.repository.TourRepository;
import renewal.common.entity.Product;
import renewal.common.entity.Tour;
import renewal.common.entity.Product.Info;
import renewal.common.repository.CityCodeRepository;
import renewal.common.repository.CountryCodeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/product")
@Controller
public class ProductController {

    private final ProductRepository productRepo;
    private final TourRepository tourRepo;
    private final ProductService productService;
    private final CountryCodeRepository countryRepo;
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
        // model.addAttribute("cityCode", cityRepo.findAll());
        model.addAttribute("productPage", productPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Product List");
        model.addAttribute("content", "components/product"); // layout 안에서 이 fragment를 렌더

        return "layout";
    }

    // 새 패키지
    @GetMapping("/new")
    public String newProduct(Model model){

        Product blankProduct = new Product();
        Tour blankTour = new Tour();
        blankProduct.setTour(blankTour);
        blankProduct.setImages(new ArrayList<String>());
        blankProduct.setInfo(new ArrayList<Info>());

        model.addAttribute("countryCode", countryRepo.findAll());
        model.addAttribute("cityCode", cityRepo.findAll());
        model.addAttribute("product", blankProduct);
        model.addAttribute("title", "New Product");
        model.addAttribute("content", "components/productDetail");

        return "layout";
    }

    // 새 패키지 등록
    @PostMapping("/new")
    public String submitProduct(@ModelAttribute Product product) throws Exception {
        
        // 투어 productId 업데이트
        Tour tour = tourRepo.findById(product.getTour().getId()).get();
        tour.setProductId(product.getId());
        tourRepo.save(tour);

        // product 등록
        productRepo.save(product);

        return "redirect:/product";
    }

    // 특정 패키지
    @GetMapping("/{id}")
    public String selectProduct(@PathVariable("id") Long id, Model model) {

        Product product = productRepo.getReferenceById(id);
        model.addAttribute("product", product);
        model.addAttribute("title", "Product " + product.getTitle());
        model.addAttribute("content", "components/productDetail");

        return "layout";
    }

    // 특정 패키지 수정
    @PostMapping("/{id}")
    public String submitSelectedProduct(@ModelAttribute Product product) {
        
        // 기존 Tour productId 삭제
        Long lastTourId = productRepo.findById(product.getId()).get().getTour().getId();
        Tour lastTour = tourRepo.findById(lastTourId).get();
        lastTour.setProductId(null);
        tourRepo.save(lastTour);

        // 투어 productId 업데이트
        Tour tour = tourRepo.findById(product.getTour().getId()).get();
        tour.setProductId(product.getId());
        tourRepo.save(tour);

        // Product 저장
        productRepo.save(product);
        
        return "redirect:/product";
    }

    // 패키지 삭제 처리
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {

        productRepo.deleteById(id);
        
        return ResponseEntity.ok("삭제 완료");
    }

}
