package renewal.awesome_travel_backoffice.timeDeal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.product.dto.ProductFilterDTO;
import renewal.awesome_travel_backoffice.product.service.ProductService;
import renewal.awesome_travel_backoffice.timeDeal.dto.TimeDealFilterDTO;
import renewal.awesome_travel_backoffice.timeDeal.service.TimeDealService;
import renewal.common.entity.Product;
import renewal.common.entity.TimeDeal;
import renewal.common.entity.TimeDeal.DiscountType;
import renewal.common.repository.ProductRepository;
import renewal.common.repository.TimeDealRepository;

@Controller
@RequestMapping("/timeDeal")
@RequiredArgsConstructor
public class TimeDealController {

    private final TimeDealRepository timeDealRepo;
    private final ProductRepository productRepo;
    private final TimeDealService timeDealService;
    private final ProductService productService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false) String active,
            @RequestParam(required = false) String startDateFrom,
            @RequestParam(required = false) String startDateTo,
            @RequestParam(required = false) String endDateFrom,
            @RequestParam(required = false) String endDateTo,
            @RequestParam(required = false) Long minValue,
            @RequestParam(required = false) Long maxValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "startTime") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        
        // 필터 DTO 생성
        TimeDealFilterDTO filter = new TimeDealFilterDTO();
        
        if (discountType != null && !discountType.isEmpty()) {
            try {
                filter.setDiscountType(DiscountType.valueOf(discountType));
            } catch (IllegalArgumentException e) {
                // 잘못된 값은 무시
            }
        }
        
        if (active != null && !active.isEmpty()) {
            filter.setActive(Boolean.parseBoolean(active));
        }
        
        if (startDateFrom != null && !startDateFrom.isEmpty()) {
            filter.setStartDateFrom(java.time.LocalDate.parse(startDateFrom));
        }
        
        if (startDateTo != null && !startDateTo.isEmpty()) {
            filter.setStartDateTo(java.time.LocalDate.parse(startDateTo));
        }
        
        if (endDateFrom != null && !endDateFrom.isEmpty()) {
            filter.setEndDateFrom(java.time.LocalDate.parse(endDateFrom));
        }
        
        if (endDateTo != null && !endDateTo.isEmpty()) {
            filter.setEndDateTo(java.time.LocalDate.parse(endDateTo));
        }
        
        filter.setMinValue(minValue);
        filter.setMaxValue(maxValue);

        // 정렬 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 20, sort);

        // 필터링된 리스트 조회 (페이징)
        Page<TimeDeal> timeDealPage = timeDealService.searchTimeDeals(filter, pageable);

        model.addAttribute("timeDealPage", timeDealPage);
        model.addAttribute("list", timeDealPage.getContent());
        model.addAttribute("filter", filter);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("discountTypes", DiscountType.values());
        model.addAttribute("title", "타임딜 관리");
        model.addAttribute("content", "components/timeDeal/timeDeal");
        return "layout";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        TimeDeal td = timeDealRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("타임딜 없음"));

        List<Product> products = productRepo.findByTimeDeal(td);

        model.addAttribute("timeDeal", td);
        model.addAttribute("products", products);

        model.addAttribute("title", "타임딜 상세");
        model.addAttribute("content", "components/timeDeal/timeDealDetail");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute("timeDeal", new TimeDeal());
        model.addAttribute("products", new ArrayList<>());

        model.addAttribute("title", "타임딜 등록");
        model.addAttribute("content", "components/timeDeal/timeDealDetail");
        return "layout";
    }

    /** 타임딜 적용 상품 선택용 팝업: 상품 검색 후 행 클릭 시 부모창에 전달 */
    @GetMapping("/product-search")
    public String productSearchPopup(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        ProductFilterDTO filter = new ProductFilterDTO();
        filter.setTitle(title);
        filter.setStatus("all"); // 선택용이므로 활성/비활성 모두 표시

        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("asc")
                ? org.springframework.data.domain.Sort.by(sortField).ascending()
                : org.springframework.data.domain.Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 20, sort);

        Page<Product> productPage = productService.searchProducts(filter, pageable);

        model.addAttribute("productPage", productPage);
        model.addAttribute("filter", filter);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "상품 선택");
        model.addAttribute("isSelectionPage", true);
        model.addAttribute("content", "components/timeDeal/productSearchPopup");
        return "popup";
    }

    @PostMapping
    public String saveTimeDeal(
            @ModelAttribute TimeDeal timeDeal,
            @RequestParam(required = false) List<Long> productIds,
            RedirectAttributes redirectAttributes) {

        // 1) TimeDeal 저장 (신규 & 수정 동일)
        TimeDeal savedTimeDeal = timeDealRepo.save(timeDeal);

        // 2) 현재 이 TimeDeal에 연결된 상품들
        List<Product> oldList = productRepo.findByTimeDeal(savedTimeDeal);

        // 상품을 모두 제거한 경우 productIds가 없을 수 있음 → 빈 리스트로 처리
        if (productIds == null) {
            productIds = java.util.Collections.emptyList();
        }

        // 3) 기존 상품 → productIds에 없으면 연결 해제
        for (Product product : oldList) {
            if (!productIds.contains(product.getId())) {
                product.setTimeDeal(null);
                productRepo.save(product);
            }
        }

        // 4) productIds에 포함된 상품 → TimeDeal 적용
        if (productIds != null) {
            for (Long pid : productIds) {
                productRepo.findById(pid).ifPresent(p -> {
                    p.setTimeDeal(savedTimeDeal);
                    productRepo.save(p);
                });
            }
        }

        // 5) 성공 메시지
        redirectAttributes.addFlashAttribute("successMessage", "타임딜이 저장되었습니다.");

        // 6) 저장 후 상세 페이지로 이동
        return "redirect:/timeDeal/" + savedTimeDeal.getId();
    }

    @PostMapping("/{id}/delete")
    @Transactional
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        TimeDeal timeDeal = timeDealRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("타임딜 없음"));

        // 1) 연결된 Product들 조회
        List<Product> productList = productRepo.findByTimeDeal(timeDeal);

        // 2) 각 Product에서 TimeDeal 연결 끊기
        for (Product p : productList) {
            p.setTimeDeal(null);
        }
        productRepo.saveAll(productList);

        // 3) 타임딜 삭제
        timeDealRepo.delete(timeDeal);

        redirectAttributes.addFlashAttribute("successMessage", "타임딜이 삭제되었습니다.");
        return "redirect:/timeDeal";
    }

}
