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
import renewal.awesome_travel_backoffice.citycode.repository.CityCodeRepository;
import renewal.awesome_travel_backoffice.countrycode.repository.CountryCodeRepository;
import renewal.awesome_travel_backoffice.productPurchase.repository.ProductPurchaseRepository;
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
    private final ProductPurchaseRepository productPurchaseRepo;

    @GetMapping
    public String listAndFilter(
            @ModelAttribute("filter") ProductFilterDTO filter, // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        
        // 필터가 null인 경우 초기화
        if (filter == null) {
            filter = new ProductFilterDTO();
        }
        
        // 상태 필터가 설정되지 않은 경우 기본값 설정 (활성 상품만)
        if (filter.getStatus() == null || filter.getStatus().isEmpty()) {
            filter.setStatus("active");
        }
        // 1) 정렬 객체 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // 2) 페이징(50개 고정) + 필터링 로직
        Pageable pageable = PageRequest.of(page, 50, sort);
        Page<Product> productPage = productService.searchProducts(filter, pageable);

        // 3) View에서 쓸 속성들
        model.addAttribute("filter", filter); // 필터 객체 추가
        model.addAttribute("countryCode", countryRepo.findAll());
        // model.addAttribute("cityCode", cityRepo.findAll());
        model.addAttribute("productPage", productPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Product List");
        model.addAttribute("content", "components/product/product"); // layout 안에서 이 fragment를 렌더

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
        model.addAttribute("content", "components/product/productDetail");

        return "layout";
    }

    // 새 패키지 등록
    @PostMapping("/new")
    public String submitProduct(@ModelAttribute Product product) throws Exception {
        
        // product 등록 (먼저 저장하여 ID 생성)
        Product savedProduct = productRepo.save(product);
        
        // 투어 productId 업데이트
        Tour tour = tourRepo.findById(product.getTour().getId()).get();
        tour.setProductId(savedProduct.getId());
        tourRepo.save(tour);

        return "redirect:/product";
    }

    // 특정 패키지
    @GetMapping("/{id}")
    public String selectProduct(@PathVariable("id") Long id, Model model) {

        Product product = productRepo.getReferenceById(id);
        model.addAttribute("product", product);
        model.addAttribute("title", "Product " + product.getTitle());
        model.addAttribute("content", "components/product/productDetail");

        return "layout";
    }

    // 특정 패키지 수정
    @PostMapping("/{id}")
    public String submitSelectedProduct(@ModelAttribute Product product) {
        
        // 기존 Product 조회하여 리뷰 데이터 보존
        Product existingProduct = productRepo.findById(product.getId()).get();
        
        // 기존 Tour productId 삭제
        Long lastTourId = existingProduct.getTour().getId();
        Tour lastTour = tourRepo.findById(lastTourId).get();
        lastTour.setProductId(null);
        tourRepo.save(lastTour);

        // 투어 productId 업데이트
        Tour tour = tourRepo.findById(product.getTour().getId()).get();
        tour.setProductId(product.getId());
        tourRepo.save(tour);

        // 리뷰 데이터 보존하면서 Product 업데이트
        product.setStar1(existingProduct.getStar1());
        product.setStar2(existingProduct.getStar2());
        product.setStar3(existingProduct.getStar3());
        product.setStar4(existingProduct.getStar4());
        product.setStar5(existingProduct.getStar5());
        
        // Product 저장
        productRepo.save(product);
        
        return "redirect:/product";
    }

    // 패키지 비활성화 처리
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateProduct(@PathVariable Long id) {
        try {
            Product product = productRepo.findById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.status(404).body("상품을 찾을 수 없습니다.");
            }
            
            // 상품을 비활성화
            product.setIsActive(false);
            productRepo.save(product);
            
            // 연결된 Tour의 productId를 NULL로 설정
            if (product.getTour() != null) {
                Tour tour = product.getTour();
                tour.setProductId(null);
                tourRepo.save(tour);
            }
            
            return ResponseEntity.ok("상품이 비활성화되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("비활성화 실패: " + e.getMessage());
        }
    }
    
    // 패키지 활성화 처리
    @PostMapping("/{id}/activate")
    public ResponseEntity<String> activateProduct(@PathVariable Long id) {
        try {
            Product product = productRepo.findById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.status(404).body("상품을 찾을 수 없습니다.");
            }
            
            // 상품을 활성화
            product.setIsActive(true);
            productRepo.save(product);
            
            // 연결된 Tour의 productId를 다시 설정
            if (product.getTour() != null) {
                Tour tour = product.getTour();
                tour.setProductId(product.getId());
                tourRepo.save(tour);
            }
            
            return ResponseEntity.ok("상품이 활성화되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("활성화 실패: " + e.getMessage());
        }
    }

}
