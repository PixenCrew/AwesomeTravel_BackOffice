package renewal.awesome_travel_backoffice.faq.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import renewal.awesome_travel_backoffice.faq.dto.request.FaqRequestDto;
import renewal.awesome_travel_backoffice.faq.dto.response.FaqResponseDto;
import renewal.awesome_travel_backoffice.faq.service.FaqService;
import renewal.common.entity.Faq.FaqCategory;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/faq")
public class FaqViewController {
    private final FaqService faqService;

    // 목록
    @GetMapping
    public String faqList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "searchType", required = false) String searchType,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "isVisible", required = false) Boolean isVisible,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<FaqResponseDto> faqPage;
        
        // 검색 조건이 있으면 검색, 없으면 전체 조회
        if (keyword != null || searchType != null || category != null || isVisible != null || startDate != null || endDate != null) {
            faqPage = faqService.searchFaqs(keyword, searchType, category, isVisible, startDate, endDate, pageable);
        } else {
            faqPage = faqService.getAllFaqs(pageable);
        }
        
        model.addAttribute("title", "FAQ 관리");
        model.addAttribute("content", "components/faq/faq");
        model.addAttribute("totalCount", faqPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", faqPage.getTotalPages());
        model.addAttribute("faqs", faqPage.getContent());
        Map<String, Object> param = new HashMap<>();
        param.put("keyword", keyword);
        param.put("searchType", searchType);
        param.put("category", category);
        param.put("isVisible", isVisible);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        model.addAttribute("param", param);
        return "layout";
    }

    // 등록 폼
    @GetMapping("/new")
    public String faqForm(Model model, HttpServletRequest request) {
        model.addAttribute("faq", new FaqRequestDto());
        model.addAttribute("title", "FAQ 등록");
        model.addAttribute("content", "components/faq/faqForm");
        // CSRF 토큰 추가
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        if (csrfToken != null) model.addAttribute("_csrf", csrfToken);
        return "layout";
    }

    // 등록 처리
    @PostMapping("/new")
    public String createFaq(FaqRequestDto dto, RedirectAttributes redirectAttributes) {
        faqService.createFaq(dto);
        redirectAttributes.addFlashAttribute("message", "FAQ가 등록되었습니다.");
        return "redirect:/faq";
    }

    // 상세
    @GetMapping("/{id}")
    public String faqDetail(@PathVariable(name = "id") Long id, Model model, jakarta.servlet.http.HttpServletRequest request) {
        FaqResponseDto faq = faqService.getFaq(id);
        model.addAttribute("faq", faq);
        model.addAttribute("title", "FAQ 상세");
        model.addAttribute("content", "components/faq/faqDetail");
        // CSRF 토큰 추가
        org.springframework.security.web.csrf.CsrfToken csrfToken = (org.springframework.security.web.csrf.CsrfToken) request.getAttribute("_csrf");
        if (csrfToken != null) model.addAttribute("_csrf", csrfToken);
        return "layout";
    }

    // 수정 폼
    @GetMapping("/edit/{id}")
    public String editFaqForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        FaqResponseDto faq = faqService.getFaq(id);
        FaqRequestDto dto = new FaqRequestDto();
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        dto.setCategory(faq.getCategory());
        dto.setVisible(faq.getVisible());
        model.addAttribute("faq", dto);
        model.addAttribute("faqId", id);
        model.addAttribute("title", "FAQ 수정");
        model.addAttribute("content", "components/faq/faqForm");
        // CSRF 토큰 추가
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        if (csrfToken != null) model.addAttribute("_csrf", csrfToken);
        return "layout";
    }

    // 수정 처리
    @PostMapping("/edit/{id}")
    public String updateFaq(@PathVariable Long id, FaqRequestDto dto, RedirectAttributes redirectAttributes) {
        faqService.updateFaq(id, dto);
        redirectAttributes.addFlashAttribute("message", "FAQ가 수정되었습니다.");
        return "redirect:/faq";
    }

    // 삭제
    @PostMapping("/delete/{id}")
    public String deleteFaq(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        faqService.deleteFaq(id);
        redirectAttributes.addFlashAttribute("message", "FAQ가 삭제되었습니다.");
        return "redirect:/faq";
    }

    // 상태 토글
    @PostMapping("/{id}/status/toggle")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        faqService.toggleStatus(id);
        redirectAttributes.addFlashAttribute("message", "FAQ 상태가 변경되었습니다.");
        return "redirect:/faq";
    }
}
