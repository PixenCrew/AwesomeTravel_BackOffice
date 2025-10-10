package renewal.awesome_travel_backoffice.productPurchase.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import renewal.awesome_travel_backoffice.productPurchase.dto.request.ProductPurchaseSearchCondition;
import renewal.awesome_travel_backoffice.productPurchase.dto.response.ProductPurchaseResponseDto;
import renewal.awesome_travel_backoffice.productPurchase.service.ProductPurchaseService;

@Controller
@RequestMapping("/product-purchase")
@RequiredArgsConstructor
public class ProductPurchaseViewController {

    private final ProductPurchaseService productPurchaseService;

    @GetMapping
    public String listProductPurchases(
            @RequestParam(required = false) String purchaseStatus,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "purchaseDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model
    ) {
        // 정렬 설정
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortField).ascending() 
            : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 20, sort);

        // 검색 조건 설정
        ProductPurchaseSearchCondition condition = new ProductPurchaseSearchCondition();
        condition.setPurchaseStatus(purchaseStatus);
        condition.setMemberId(memberId);
        condition.setProductId(productId);
        condition.setCustomerName(customerName);
        condition.setCustomerEmail(customerEmail);

        // 패키지 상품 구매 목록 조회
        Page<ProductPurchaseResponseDto> purchasePage = productPurchaseService.getAllPurchasesDto(condition, pageable);

        // 모델에 데이터 추가
        model.addAttribute("purchasePage", purchasePage);
        model.addAttribute("condition", condition);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "패키지 상품 주문 목록");
        model.addAttribute("content", "components/productPurchase/productPurchaseList");
        model.addAttribute("isSelectionPage", false);

        return "layout";
    }

    @GetMapping("/{id}")
    public String getProductPurchaseDetail(@PathVariable Long id, Model model) {
        ProductPurchaseResponseDto purchase = productPurchaseService.getPurchaseDto(id);
        
        model.addAttribute("purchase", purchase);
        model.addAttribute("title", "패키지 상품 주문 상세");
        model.addAttribute("content", "components/productPurchase/productPurchaseDetail");
        model.addAttribute("isSelectionPage", false);

        return "layout";
    }
}
