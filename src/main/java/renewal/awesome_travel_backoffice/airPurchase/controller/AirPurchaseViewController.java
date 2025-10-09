package renewal.awesome_travel_backoffice.airPurchase.controller;

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
import renewal.awesome_travel_backoffice.airPurchase.dto.request.AirPurchaseSearchCondition;
import renewal.awesome_travel_backoffice.airPurchase.dto.response.AirPurchaseResponseDto;
import renewal.awesome_travel_backoffice.airPurchase.service.AirPurchaseService;
import renewal.common.entity.AirPurchase;
import renewal.common.entity.BasePurchase.PurchaseStatus;

@Controller
@RequiredArgsConstructor
@RequestMapping("/air-purchase")
public class AirPurchaseViewController {

    private final AirPurchaseService airPurchaseService;

    @GetMapping
    public String listAirPurchases(
            @RequestParam(required = false) PurchaseStatus status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "purchaseDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model
    ) {
        // 검색 조건 설정
        AirPurchaseSearchCondition condition = new AirPurchaseSearchCondition();
        condition.setStatus(status);
        condition.setName(name);
        condition.setEmail(email);
        if (startDate != null && !startDate.isEmpty()) {
            condition.setStartDate(java.time.LocalDate.parse(startDate));
        }
        if (endDate != null && !endDate.isEmpty()) {
            condition.setEndDate(java.time.LocalDate.parse(endDate));
        }

        // 정렬 설정
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortField).ascending() 
            : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 20, sort);

        // 데이터 조회
        Page<AirPurchaseResponseDto> airPurchasePage = airPurchaseService.getAllPurchasesDto(condition, pageable);

        // 모델에 데이터 추가
        model.addAttribute("airPurchasePage", airPurchasePage);
        model.addAttribute("status", status);
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("purchaseStatuses", PurchaseStatus.values());
        model.addAttribute("title", "항공 주문 관리");
        model.addAttribute("content", "components/airPurchase/airPurchaseList");

        return "layout";
    }

    @GetMapping("/{id}")
    public String viewAirPurchase(@PathVariable Long id, Model model) {
        AirPurchaseResponseDto airPurchase = airPurchaseService.getPurchaseDto(id);
        
        model.addAttribute("airPurchase", airPurchase);
        model.addAttribute("purchaseStatuses", PurchaseStatus.values());
        model.addAttribute("title", "항공 주문 상세");
        model.addAttribute("content", "components/airPurchase/airPurchaseDetail");

        return "layout";
    }
}
