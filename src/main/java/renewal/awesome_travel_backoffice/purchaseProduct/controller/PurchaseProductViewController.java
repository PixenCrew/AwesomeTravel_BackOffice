package renewal.awesome_travel_backoffice.purchaseProduct.controller;

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

import java.util.List;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.purchaseProduct.dto.request.PurchaseProductSearchCondition;
import renewal.awesome_travel_backoffice.purchaseProduct.service.PurchaseProductService;
import renewal.common.dto.PassengerResponseDto;

@Controller
@RequestMapping("/product-purchase")
@RequiredArgsConstructor
public class PurchaseProductViewController {

    private final PurchaseProductService productPurchaseService;

    @GetMapping
    public String listPurchaseProducts(
            @RequestParam(required = false) String purchaseStatus,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "purchaseDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        // 정렬 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 20, sort);

        // 검색 조건 설정
        PurchaseProductSearchCondition condition = new PurchaseProductSearchCondition();
        condition.setPurchaseStatus(purchaseStatus);
        condition.setMemberId(memberId);
        condition.setProductId(productId);
        condition.setCustomerName(customerName);
        condition.setCustomerEmail(customerEmail);

        // 패키지 상품 구매 목록 조회
        Page<renewal.awesome_travel_backoffice.purchaseProduct.dto.response.PurchaseProductResponseDto> purchasePage =
                productPurchaseService.getAllPurchasesDto(condition, pageable);

        // 모델에 데이터 추가
        model.addAttribute("purchasePage", purchasePage);
        model.addAttribute("condition", condition);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "패키지 상품 주문 목록");
        model.addAttribute("content", "components/purchaseProduct/productPurchaseList");
        model.addAttribute("isSelectionPage", false);

        return "layout";
    }

    @GetMapping("/{id}")
    public String getPurchaseProductDetail(@PathVariable Long id, Model model) {

        renewal.awesome_travel_backoffice.purchaseProduct.dto.response.PurchaseProductResponseDto purchase =
                productPurchaseService.getPurchaseDto(id);

        model.addAttribute("purchase", purchase);
        model.addAttribute("title", "패키지 상품 주문 상세");
        model.addAttribute("content", "components/purchaseProduct/productPurchaseDetail");
        model.addAttribute("isSelectionPage", false);

        return "layout";
    }

    @GetMapping("/{id}/passengers")
    public String viewPassengers(@PathVariable Long id, Model model) {
        renewal.awesome_travel_backoffice.purchaseProduct.dto.response.PurchaseProductResponseDto purchase =
                productPurchaseService.getPurchaseDto(id);
        List<PassengerResponseDto> passengerDtos = productPurchaseService.getPassengers(id);

        model.addAttribute("purchase", purchase);
        model.addAttribute("passengers", passengerDtos);
        model.addAttribute("countries", productPurchaseService.getCountries(null));
        model.addAttribute("title", "패키지 상품 주문 탑승객 목록");
        model.addAttribute("content", "components/purchaseProduct/productPurchasePassengers");
        model.addAttribute("isSelectionPage", false);

        return "layout";
    }
}
