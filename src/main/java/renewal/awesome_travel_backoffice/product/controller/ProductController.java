package renewal.awesome_travel_backoffice.product.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.common.service.CommonCodeService;
import renewal.awesome_travel_backoffice.product.dto.ProductFilterDTO;
import renewal.awesome_travel_backoffice.product.repository.ProductAdminRepository;
import renewal.awesome_travel_backoffice.product.service.ProductService;
import renewal.awesome_travel_backoffice.tour.repository.TourRepository;
import renewal.common.entity.Product;
import renewal.common.entity.Product.Info;
import renewal.common.entity.Product.ProductType;
import renewal.common.entity.Tour;

@RequiredArgsConstructor
@RequestMapping("/product")
@Controller
public class ProductController {

    private final ProductAdminRepository productAdminRepo;
    private final TourRepository tourRepo;
    private final ProductService productService;

    private final CommonCodeService commonCodeService;

    @GetMapping
    public String listAndFilter(
            @ModelAttribute ProductFilterDTO filter, // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        // 필터가 null인 경우 초기화
        if (filter == null) {
            filter = new ProductFilterDTO();
        }

        // 상태 필터가 설정되지 않은 경우 기본값 설정 (활성 상품만)
        // 빈 문자열("")은 "all"로 간주하여 모든 상품 조회
        if (filter.getStatus() == null) {
            filter.setStatus("active");
        } else if (filter.getStatus().isEmpty()) {
            filter.setStatus("all"); // "전체" 선택 시 모든 상품 조회
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
        model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
        model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
        model.addAttribute("productPage", productPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "여행상품 관리");
        model.addAttribute("content", "components/product/product"); // layout 안에서 이 fragment를 렌더

        return "layout";
    }

    // 새 패키지
    @GetMapping("/new")
    public String newProduct(Model model) {

        Product blankProduct = new Product();
        Tour blankTour = new Tour();
        blankProduct.setTour(blankTour);
        blankProduct.setPhotos(new ArrayList<String>());
        blankProduct.setInclude(new ArrayList<Info>());
        blankProduct.setExclude(new ArrayList<Info>());

        model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
        model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
        model.addAttribute("productTypes", ProductType.values());
        model.addAttribute("product", blankProduct);
        model.addAttribute("title", "새 상품 등록");
        model.addAttribute("content", "components/product/productDetail");

        return "layout";
    }

    // 새 패키지 등록
    @PostMapping("/new")
    public String submitProduct(@ModelAttribute Product product) throws Exception {

        // product 등록 (먼저 저장하여 ID 생성)
        Product savedProduct = productAdminRepo.save(product);

        // 투어 productId 업데이트
        Tour tour = tourRepo.findById(product.getTour().getId()).get();
        tour.setProductId(savedProduct.getId());
        tourRepo.save(tour);

        // 키워드 등록
        // 폼에서 전송된 keywords와 Tour의 keywords를 병합
        Set<String> finalKeywords = new HashSet<>();
        
        // 1. Tour의 keywords 추가
        if (tour.getKeywords() != null) {
            finalKeywords.addAll(tour.getKeywords());
        }
        
        // 2. 폼에서 전송된 keywords 추가 (어드민이 직접 입력한 키워드)
        if (product.getKeywords() != null) {
            finalKeywords.addAll(product.getKeywords());
        }
        
        // 3. Product title 추가
        if (product.getTitle() != null && !product.getTitle().isEmpty()) {
            finalKeywords.add(product.getTitle());
        }
        
        product.setKeywords(finalKeywords);

        // 투어 productId 업데이트
        // Tour tour = tourRepo.findById(product.getTour().getId()).get();
        product.setTour(tour);
        productAdminRepo.save(product);

        // 첫 사진 섬네일로 등록
        if (!product.getPhotos().isEmpty()) {
            product.setThumbnail(product.getPhotos().get(0));
        }

        return "redirect:/product";
    }

    // 특정 패키지
    @GetMapping("/{id}")
    public String selectProduct(@PathVariable Long id, Model model) {

        Product product = productAdminRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + id));
        
        model.addAttribute("product", product);
        model.addAttribute("productTypes", ProductType.values());
        model.addAttribute("title", "상품 상세");
        model.addAttribute("content", "components/product/productDetail");

        return "layout";
    }

    // 특정 패키지 수정
    @PostMapping("/{id}")
    @Transactional
    public String submitSelectedProduct(@ModelAttribute Product product) {

        // 기존 Product 조회하여 리뷰 데이터 보존
        Product existingProduct = productAdminRepo.findById(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + product.getId()));

        Long newTourId = product.getTour().getId();
        Long existingTourId = existingProduct.getTour().getId();

        // Tour가 변경된 경우에만 Tour 업데이트
        if (!newTourId.equals(existingTourId)) {
            // 새로운 Tour의 productId 업데이트
            Tour newTour = tourRepo.findById(newTourId)
                    .orElseThrow(() -> new IllegalArgumentException("새 투어를 찾을 수 없습니다. ID: " + newTourId));
            newTour.setProductId(product.getId());
            tourRepo.save(newTour);
            product.setTour(newTour);
        } else {
            // Tour가 변경되지 않은 경우, 기존 Tour 참조 유지
            product.setTour(existingProduct.getTour());
        }

        // 리뷰 데이터 보존하면서 Product 업데이트
        product.setStar1(existingProduct.getStar1());
        product.setStar2(existingProduct.getStar2());
        product.setStar3(existingProduct.getStar3());
        product.setStar4(existingProduct.getStar4());
        product.setStar5(existingProduct.getStar5());

        // 첫 사진 섬네일로 등록
        if (!product.getPhotos().isEmpty()) {
            product.setThumbnail(product.getPhotos().get(0));
        }

        // 키워드 등록
        // 폼에서 전송된 keywords와 Tour의 keywords를 병합
        Set<String> finalKeywords = new HashSet<>();
        
        // 1. Tour의 keywords 추가
        Tour tour = product.getTour();
        if (tour != null && tour.getKeywords() != null) {
            finalKeywords.addAll(tour.getKeywords());
        }
        
        // 2. 폼에서 전송된 keywords 추가 (어드민이 직접 입력한 키워드)
        if (product.getKeywords() != null) {
            finalKeywords.addAll(product.getKeywords());
        }
        
        // 3. Product title 추가
        if (product.getTitle() != null && !product.getTitle().isEmpty()) {
            finalKeywords.add(product.getTitle());
        }
        
        product.setKeywords(finalKeywords);

        // Product 저장
        productAdminRepo.save(product);

        return "redirect:/product";
    }

    // 패키지 삭제 처리
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateProduct(@PathVariable Long id) {
        try {
            Product product = productAdminRepo.findById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.status(404).body("상품을 찾을 수 없습니다.");
            }

            // 상품을 비활성화
            product.setIsActive(false);
            productAdminRepo.save(product);

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
            Product product = productAdminRepo.findById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.status(404).body("상품을 찾을 수 없습니다.");
            }

            // 상품을 활성화
            product.setIsActive(true);
            productAdminRepo.save(product);

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

    // 패키지 삭제 처리
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {

        // Product와 연결된 Tour가 있으면 연결 해제
        Product product = productAdminRepo.findById(id).get();
        Tour tour = product.getTour();
        if (tour != null) {
            // tour.setProduct(null); // FK를 null로 만들어서 참조 끊기
            tourRepo.save(tour); // 업데이트 필요
        }

        productAdminRepo.deleteById(id);

        return ResponseEntity.ok("삭제 완료");
    }

}
