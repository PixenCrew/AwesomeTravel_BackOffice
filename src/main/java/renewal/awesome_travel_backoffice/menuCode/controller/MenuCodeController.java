package renewal.awesome_travel_backoffice.menuCode.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.common.service.CommonCodeService;
import renewal.awesome_travel_backoffice.menuCode.service.MenuCodeService;
import renewal.awesome_travel_backoffice.product.dto.ProductFilterDTO;
import renewal.awesome_travel_backoffice.product.service.ProductService;
import renewal.common.entity.MenuCode;
import renewal.common.entity.Product;
import renewal.common.repository.MenuCodeRepository;

@RequiredArgsConstructor
@RequestMapping("/menu-code")
@Controller
public class MenuCodeController {

    private static final int PRODUCT_PREVIEW_MAX = 10;

    private final MenuCodeRepository menuCodeRepository;
    private final CommonCodeService commonCodeService;
    private final MenuCodeService menuCodeService;
    private final ProductService productService;

    @GetMapping
    public String getMenuCodeList(
        @RequestParam(value = "code", required = false) String codeKeyword,
        @RequestParam(value = "name", required = false) String nameKeyword,
        @RequestParam(value = "value", required = false) String valueKeyword,
        Model model
    ) {

        List<MenuCode> menuCodes = menuCodeRepository.findAll();

        List<MenuCode> filteredMenuCodes = menuCodes.stream()
            .filter(menuCode -> {
                if (codeKeyword == null || codeKeyword.isBlank()) {
                    return true;
                }
                String trimmed = codeKeyword.trim();
                boolean matchesCode = trimmed.equals(menuCode.getCode());
                boolean matchesNumeric = menuCode.getCode2() != null
                    && trimmed.equals(String.valueOf(menuCode.getCode2()));
                return matchesCode || matchesNumeric;
            })
            .filter(menuCode -> {
                if (nameKeyword == null || nameKeyword.isBlank()) {
                    return true;
                }
                String keywordLower = nameKeyword.trim().toLowerCase();
                return menuCode.getName() != null
                    && menuCode.getName().toLowerCase().contains(keywordLower);
            })
            .filter(menuCode -> {
                if (valueKeyword == null || valueKeyword.isBlank()) {
                    return true;
                }
                String keywordLower = valueKeyword.trim().toLowerCase();
                return menuCode.getDetails() != null
                    && menuCode.getDetails().stream()
                        .anyMatch(detail -> detail.getValue() != null
                            && detail.getValue().toLowerCase().contains(keywordLower));
            })
            .collect(Collectors.toList());

        Map<String, String> filterValues = new HashMap<>();
        filterValues.put("code", codeKeyword);
        filterValues.put("name", nameKeyword);
        filterValues.put("value", valueKeyword);

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        if (codeKeyword != null && !codeKeyword.isBlank()) {
            builder.queryParam("code", codeKeyword.trim());
        }
        if (nameKeyword != null && !nameKeyword.isBlank()) {
            builder.queryParam("name", nameKeyword.trim());
        }
        if (valueKeyword != null && !valueKeyword.isBlank()) {
            builder.queryParam("value", valueKeyword.trim());
        }
        String queryString = builder.build().getQuery();

        model.addAttribute("title", "메뉴 코드 관리");
        model.addAttribute("menuCodes", filteredMenuCodes);
        model.addAttribute("filters", filterValues);
        model.addAttribute("hasActiveFilter",
            (codeKeyword != null && !codeKeyword.isBlank())
                || (nameKeyword != null && !nameKeyword.isBlank())
                || (valueKeyword != null && !valueKeyword.isBlank()));
        model.addAttribute("filterQueryString", queryString != null ? "?" + queryString : "");
        model.addAttribute("content", "components/menuCode/menuCode");
        return "layout";
    }

    /** 메뉴코드 상세 항목 ID(상품) 선택용 팝업. rowIndex로 어느 행에 넣을지 부모창에 전달 */
    @GetMapping("/product-search")
    public String productSearchPopup(
            @RequestParam(name = "rowIndex", required = false, defaultValue = "0") int rowIndex,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        ProductFilterDTO filter = new ProductFilterDTO();
        filter.setTitle(title);
        filter.setStatus("all");

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 20, sort);

        Page<Product> productPage = productService.searchProducts(filter, pageable);

        model.addAttribute("productPage", productPage);
        model.addAttribute("filter", filter);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("rowIndex", rowIndex);
        model.addAttribute("title", "상품 선택 (메뉴코드 ID)");
        model.addAttribute("content", "components/menuCode/productSearchPopup");
        return "popup";
    }

    @GetMapping("/{id}")
    public String getMenuCodeDetail(@PathVariable Long id, Model model) {

        MenuCode menuCode = menuCodeRepository.findByCode2(id)
                .orElseThrow(() -> new IllegalArgumentException("메뉴 코드를 찾을 수 없습니다. id=" + id));

        List<Product> matchingProducts = menuCodeService.findProductsByMenuCode(menuCode);
        List<Product> productPreview = matchingProducts.size() > PRODUCT_PREVIEW_MAX
                ? matchingProducts.subList(0, PRODUCT_PREVIEW_MAX)
                : matchingProducts;

        model.addAttribute("title", "메뉴 코드 상세");
        model.addAttribute("menuCode", menuCode);
        model.addAttribute("targetColumns", MenuCode.MenuCodeDetail.TargetColumn.values());
        model.addAttribute("countryCodes", commonCodeService.getAllCountryCodes());
        model.addAttribute("cityCodes", commonCodeService.getAllCityCodes());
        model.addAttribute("matchingProductCount", matchingProducts.size());
        model.addAttribute("matchingProductPreview", productPreview);
        model.addAttribute("content", "components/menuCode/menuCodeDetail");
        return "layout";
    }

    @GetMapping("/check/{id}")
    public ResponseEntity<Map<String, Object>> checkMenuCodeDupe(@PathVariable Long id) {

        Optional<MenuCode> menuCode = menuCodeRepository.findByCode2(id);

        Map<String, Object> response = new HashMap<>();
        if (menuCode.isPresent()) {
            response.put("duplicate", true);
            response.put("message", "이미 사용 중인 코드입니다.");
            return ResponseEntity.status(409).body(response); // 409 Conflict
        } else {
            response.put("duplicate", false);
            response.put("message", "사용 가능한 코드입니다.");
            return ResponseEntity.ok(response); // 200 OK
        }
    }

    @GetMapping("/new")
    public String newMenuCode(Model model) {

        MenuCode menuCode = new MenuCode();

        model.addAttribute("title", "메뉴 코드 등록");
        model.addAttribute("menuCode", menuCode);
        model.addAttribute("targetColumns", MenuCode.MenuCodeDetail.TargetColumn.values());
        model.addAttribute("countryCodes", commonCodeService.getAllCountryCodes());
        model.addAttribute("cityCodes", commonCodeService.getAllCityCodes());
        model.addAttribute("content", "components/menuCode/menuCodeDetail");

        return "layout";
    }

    @PostMapping("/new")
    public String submitNewMenuCode(@ModelAttribute MenuCode menuCode) {
        Objects.requireNonNull(menuCode, "menuCode must not be null");
        menuCodeRepository.save(menuCode);

        return "redirect:/menu-code";
    }

    @PostMapping("/{id}")
    public String modifyMenuCodeDetail(@PathVariable Long id, @ModelAttribute MenuCode menuCode, Model model) {
        Objects.requireNonNull(menuCode, "menuCode must not be null");
        menuCode.setCode2(id);
        menuCodeRepository.save(menuCode);

        return "redirect:/menu-code";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuCode(@PathVariable Long id) {
        menuCodeRepository.deleteByCode2(id);
        return ResponseEntity.ok().build();
    }

}
