package renewal.awesome_travel_backoffice.airPurchase.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.airPurchase.dto.request.AirPurchaseSearchCondition;
import renewal.awesome_travel_backoffice.airPurchase.entity.AirPurchase;
import renewal.awesome_travel_backoffice.airPurchase.service.AirPurchaseService;
import renewal.awesome_travel_backoffice.airPurchase.utiles.PurchaseStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/air-purchases")
public class AirPurchaseAdminController {

    private final AirPurchaseService airPurchaseService;

    // 1. 전체 목록 조회 (페이징 + 정렬)
    @GetMapping
    public ResponseEntity<Page<AirPurchase>> getAllPurchases(
            @ModelAttribute AirPurchaseSearchCondition condition,
            @PageableDefault(size = 20, sort = "purchaseDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<AirPurchase> result = airPurchaseService.getAllPurchases(condition, pageable);
        return ResponseEntity.ok(result);
    }


    // 2. 단건 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<AirPurchase> getPurchase(@PathVariable Long id) {
        return ResponseEntity.ok(airPurchaseService.getPurchase(id));
    }

    // 3. 상태 변경 (관리자용)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @RequestParam PurchaseStatus status
    ) {
        airPurchaseService.changePurchaseStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
