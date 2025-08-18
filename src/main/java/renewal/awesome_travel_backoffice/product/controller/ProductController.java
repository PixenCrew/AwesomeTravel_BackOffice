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
import renewal.awesome_travel_backoffice.tour.entity.Tour;

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

        Product blankProduct = new Product();
        Tour blankTour = new Tour();
        blankProduct.setTour(blankTour);

        model.addAttribute("countryCode", countryRepo.findAll());
        model.addAttribute("cityCode", cityRepo.findAll());
        model.addAttribute("product", blankProduct);
        model.addAttribute("content", "components/productDetail");

        return "layout";
    }

}
