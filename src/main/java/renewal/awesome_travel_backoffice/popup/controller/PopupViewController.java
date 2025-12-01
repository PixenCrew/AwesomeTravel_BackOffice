package renewal.awesome_travel_backoffice.popup.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.popup.dto.request.PopupRequestDto;
import renewal.awesome_travel_backoffice.popup.dto.request.PopupSearchRequest;
import renewal.awesome_travel_backoffice.popup.dto.response.PopupResponseDto;
import renewal.awesome_travel_backoffice.popup.service.PopupService;

import java.time.LocalDate;

@Controller
@RequestMapping("/popup")
@RequiredArgsConstructor
public class PopupViewController {
    
    private final PopupService popupService;
    
    // 팝업 목록 조회
    @GetMapping
    public String popupList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDate startDateFrom,
            @RequestParam(required = false) LocalDate startDateTo,
            @RequestParam(required = false) LocalDate endDateFrom,
            @RequestParam(required = false) LocalDate endDateTo,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean includeInactive,
            @PageableDefault(size = 10, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {
        
        PopupSearchRequest searchRequest = new PopupSearchRequest();
        searchRequest.setKeyword(keyword);
        searchRequest.setStartDateFrom(startDateFrom);
        searchRequest.setStartDateTo(startDateTo);
        searchRequest.setEndDateFrom(endDateFrom);
        searchRequest.setEndDateTo(endDateTo);
        searchRequest.setActive(active);
        searchRequest.setIncludeInactive(includeInactive != null ? includeInactive : false);
        
        Page<PopupResponseDto> popups = popupService.searchPopups(searchRequest, pageable);
        
        model.addAttribute("popups", popups);
        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("title", "팝업 관리");
        model.addAttribute("content", "components/popup/popupList");
        
        return "layout";
    }
    
    // 팝업 생성 폼
    @GetMapping("/create")
    public String popupCreateForm(Model model) {
        model.addAttribute("title", "팝업 등록");
        model.addAttribute("content", "components/popup/popupForm");
        return "layout";
    }
    
    // 팝업 상세 조회
    @GetMapping("/{id}")
    public String popupDetail(@PathVariable Long id, Model model) {
        PopupResponseDto popup = popupService.getById(id);
        model.addAttribute("popup", popup);
        model.addAttribute("title", "팝업 상세");
        model.addAttribute("content", "components/popup/popupDetail");
        return "layout";
    }
    
    // 팝업 수정 폼
    @GetMapping("/edit/{id}")
    public String popupEditForm(@PathVariable Long id, Model model) {
        PopupResponseDto popup = popupService.getById(id);
        model.addAttribute("popup", popup);
        model.addAttribute("title", "팝업 수정");
        model.addAttribute("content", "components/popup/popupForm");
        return "layout";
    }
    
    // 팝업 생성
    @PostMapping
    public String createPopup(
            @RequestParam Integer displayOrder,
            @RequestParam String title,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Boolean active,
            @RequestParam String file,
            @RequestParam String url,
            Model model) {
        
        try {
            PopupRequestDto dto = new PopupRequestDto();
            dto.setDisplayOrder(displayOrder);
            dto.setTitle(title);
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setActive(active != null ? active : true);
            dto.setFile(file);
            dto.setUrl(url);
            
            Long popupId = popupService.create(dto);
            
            return "redirect:/popup?message=success";
        } catch (Exception e) {
            model.addAttribute("error", "팝업 등록 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("title", "팝업 등록");
            model.addAttribute("content", "components/popup/popupForm");
            return "layout";
        }
    }
    
    // 팝업 수정
    @PutMapping("/{id}")
    public String updatePopup(
            @PathVariable Long id,
            @RequestParam Integer displayOrder,
            @RequestParam String title,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Boolean active,
            @RequestParam String file,
            @RequestParam String url,
            Model model) {
        
        try {
            PopupRequestDto dto = new PopupRequestDto();
            dto.setDisplayOrder(displayOrder);
            dto.setTitle(title);
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setActive(active != null ? active : true);
            dto.setFile(file);
            dto.setUrl(url);
            
            popupService.update(id, dto);
            
            return "redirect:/popup?message=success";
        } catch (Exception e) {
            model.addAttribute("error", "팝업 수정 중 오류가 발생했습니다: " + e.getMessage());
            PopupResponseDto popup = popupService.getById(id);
            model.addAttribute("popup", popup);
            model.addAttribute("title", "팝업 수정");
            model.addAttribute("content", "components/popup/popupForm");
            return "layout";
        }
    }
    
    // 팝업 삭제
    @DeleteMapping("/{id}")
    public String deletePopup(@PathVariable Long id) {
        try {
            popupService.delete(id);
            return "redirect:/popup?message=success";
        } catch (Exception e) {
            return "redirect:/popup?error=삭제 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
    
    // 팝업 활성화/비활성화 토글
    @PostMapping("/{id}/toggle")
    public String togglePopup(@PathVariable Long id) {
        try {
            popupService.toggleActive(id);
            return "redirect:/popup?message=success";
        } catch (Exception e) {
            return "redirect:/popup?error=상태 변경 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
