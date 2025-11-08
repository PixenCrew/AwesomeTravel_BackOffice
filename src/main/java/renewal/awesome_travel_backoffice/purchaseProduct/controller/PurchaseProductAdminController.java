package renewal.awesome_travel_backoffice.purchaseProduct.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.admin.Admin;
import renewal.awesome_travel_backoffice.admin.AdminService;
import renewal.awesome_travel_backoffice.purchaseProduct.dto.request.ProductPassengerUpdateRequestDto;
import renewal.awesome_travel_backoffice.purchaseProduct.dto.request.PurchaseProductSearchCondition;
import renewal.awesome_travel_backoffice.purchaseProduct.repository.PurchaseProductRepository;
import renewal.awesome_travel_backoffice.purchaseProduct.service.PurchaseProductService;
import renewal.common.entity.Handler;
import renewal.common.entity.PurchaseBase.PurchaseStatus;
import renewal.common.entity.PurchaseProduct;
import renewal.common.repository.HandlerRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/purchase-products")
public class PurchaseProductAdminController {

    private final PurchaseProductService productPurchaseService;
    private final PurchaseProductRepository productPurchaseRepo;
    private final AdminService adminService;
    private final HandlerRepository handlerRepo;

    // 1. 전체 목록 조회 (페이징 + 정렬)
    @GetMapping
    public ResponseEntity<Page<PurchaseProduct>> getAllPurchases(
            @ModelAttribute PurchaseProductSearchCondition condition,
            @PageableDefault(size = 20, sort = "purchaseDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PurchaseProduct> result = productPurchaseService.getAllPurchases(condition, pageable);
        return ResponseEntity.ok(result);
    }

    // 2. 단건 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseProduct> getPurchase(@PathVariable Long id) {
        return ResponseEntity.ok(productPurchaseService.getPurchase(id));
    }

    // 3. 상태 변경 (관리자용)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @RequestParam PurchaseStatus status) {
        productPurchaseService.changePurchaseStatus(id, status);
        return ResponseEntity.ok().build();
    }

    // 4. 주문 취소 (관리자용)
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelPurchase(@PathVariable Long id) {
        productPurchaseService.changePurchaseStatus(id, PurchaseStatus.CANCELLED);
        return ResponseEntity.ok().build();
    }

    // 5. 승객 정보 수정 (관리자용)
    @PatchMapping("/{purchaseId}/passengers/{passengerId}")
    public ResponseEntity<Void> updatePassenger(
            @PathVariable Long purchaseId,
            @PathVariable Long passengerId,
            @RequestBody ProductPassengerUpdateRequestDto updateRequest) {
        productPurchaseService.updatePassenger(purchaseId, passengerId, updateRequest);
        return ResponseEntity.ok().build();
    }

    // 6. 승객 정보 추가 (관리자용)
    @PatchMapping("/{purchaseId}/passengers")
    public ResponseEntity<Void> addPassenger(
            @PathVariable Long purchaseId,
            @RequestBody ProductPassengerUpdateRequestDto passengerDto) {
        productPurchaseService.addPassengerInfo(purchaseId, passengerDto);
        return ResponseEntity.ok().build();
    }

    // 7. 결제 완료 처리 (관리자용)
    @PatchMapping("/{purchaseId}/complete-payment")
    public ResponseEntity<Void> completePayment(@PathVariable Long purchaseId) {
        productPurchaseService.completePayment(purchaseId);
        return ResponseEntity.ok().build();
    }

    // 8. 승객 정보 마감일 체크 (관리자용)
    @PatchMapping("/check-deadline")
    public ResponseEntity<Void> checkPassengerInfoDeadline() {
        productPurchaseService.checkPassengerInfoDeadline();
        return ResponseEntity.ok().build();
    }

    // 9. 국적 코드 목록 조회 (검색 기능 포함)
    @GetMapping("/countries")
    public ResponseEntity<java.util.List<CountryCodeDto>> getCountries(@RequestParam(required = false) String search) {
        java.util.List<CountryCodeDto> countries = productPurchaseService.getCountries(search);
        return ResponseEntity.ok(countries);
    }

    // 8. 담당자 할당 (관리자용)
    @PostMapping("/{purchaseId}/assign-handler")
    public String assignHandler(@PathVariable Long purchaseId, Principal principal) {
        String username = principal.getName();

        Admin admin = adminService.getAdminByUsername(username);

        Handler handler = new Handler();
        handler.setName(admin.getName());
        handler.setPosition(admin.getPosition());
        handler.setEmail(admin.getEmail());
        handler.setFax(admin.getFax());
        handler.setNumber(admin.getNumber());
        handlerRepo.save(handler);

        PurchaseProduct purchaseProduct = productPurchaseService.getPurchase(purchaseId);
        purchaseProduct.setHandler(handler);
        productPurchaseRepo.save(purchaseProduct);

        return "redirect:/product-purchase/" + purchaseId;
    }

    // 국적 코드 DTO
    public static class CountryCodeDto {
        private String code;
        private String nameKor;
        private String nameEng;

        public CountryCodeDto(String code, String nameKor, String nameEng) {
            this.code = code;
            this.nameKor = nameKor;
            this.nameEng = nameEng;
        }

        // Getters
        public String getCode() {
            return code;
        }

        public String getNameKor() {
            return nameKor;
        }

        public String getNameEng() {
            return nameEng;
        }

        public String getDisplayName() {
            return nameKor + " (" + code + ")";
        }
    }
}
