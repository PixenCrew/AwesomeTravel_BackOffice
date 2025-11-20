package renewal.awesome_travel_backoffice.purchaseAir.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import renewal.common.entity.PurchaseAir;
import renewal.common.entity.PurchaseBase.PurchaseStatus;
import renewal.awesome_travel_backoffice.purchaseAir.dto.request.PurchaseAirSearchCondition;
import renewal.awesome_travel_backoffice.purchaseAir.dto.response.PurchaseAirResponseDto;
import renewal.awesome_travel_backoffice.purchaseAir.service.PurchaseAirService;
import renewal.common.dto.PassengerUpdateRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/air-purchases")
public class PurchaseAirAdminController {

    private final PurchaseAirService airPurchaseService;

    // 1. 전체 목록 조회 (페이징 + 정렬)
    @GetMapping
    public ResponseEntity<Page<PurchaseAir>> getAllPurchases(
            @ModelAttribute PurchaseAirSearchCondition condition,
            @PageableDefault(size = 20, sort = "purchaseDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<PurchaseAir> result = airPurchaseService.getAllPurchases(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseAirResponseDto> getPurchase(@PathVariable Long id) {
        return ResponseEntity.ok(airPurchaseService.getPurchaseDetail(id).getPurchase());
    }


    // 3. 구매 상태 변경 (관리자용)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @RequestParam PurchaseStatus status
    ) {
        airPurchaseService.changePurchaseStatus(id, status);
        return ResponseEntity.ok().build();
    }

    // 4. 탑승객 정보 수정 (관리자용)
    @PatchMapping("/{purchaseId}/passengers/{passengerId}")
    public ResponseEntity<Void> updatePassenger(
            @PathVariable Long purchaseId,
            @PathVariable Long passengerId,
            @RequestBody PassengerUpdateRequestDto updateRequest
    ) {
        airPurchaseService.updatePassenger(purchaseId, passengerId, updateRequest);
        return ResponseEntity.ok().build();
    }

    // 4-1. 탑승객 정보 추가 (관리자용)
    @PatchMapping("/{purchaseId}/passengers")
    public ResponseEntity<Void> addPassenger(
            @PathVariable Long purchaseId,
            @RequestBody PassengerUpdateRequestDto passengerDto
    ) {
        airPurchaseService.addPassengerInfo(purchaseId, passengerDto);
        return ResponseEntity.ok().build();
    }

    // 5. 주문 취소 (공통 로직 호출)
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelPurchase(@PathVariable Long id) {
        airPurchaseService.cancelPurchase(id);
        return ResponseEntity.ok().build();
    }

    // 6. 승객 수 수정 (관리자용)
    @PatchMapping("/{id}/passenger-count")
    public ResponseEntity<Void> updatePassengerCount(
            @PathVariable Long id,
            @RequestBody PassengerCountUpdateRequest request
    ) {
        airPurchaseService.updatePassengerCount(id, request.getAdultCount(), request.getYouthCount(), request.getInfantCount());
        return ResponseEntity.ok().build();
    }

    // 7. 국적 코드 목록 조회 (검색 기능 포함)
    @GetMapping("/countries")
    public ResponseEntity<java.util.List<PurchaseAirService.CountryCodeDto>> getCountries(@RequestParam(required = false) String search) {
        java.util.List<PurchaseAirService.CountryCodeDto> countries = airPurchaseService.getCountries(search);
        return ResponseEntity.ok(countries);
    }

    // 승객 수 수정 요청 DTO
    public static class PassengerCountUpdateRequest {
        private Long adultCount;
        private Long youthCount;
        private Long infantCount;

        public Long getAdultCount() {
            return adultCount;
        }

        public void setAdultCount(Long adultCount) {
            this.adultCount = adultCount;
        }

        public Long getYouthCount() {
            return youthCount;
        }

        public void setYouthCount(Long youthCount) {
            this.youthCount = youthCount;
        }

        public Long getInfantCount() {
            return infantCount;
        }

        public void setInfantCount(Long infantCount) {
            this.infantCount = infantCount;
        }
    }

}
