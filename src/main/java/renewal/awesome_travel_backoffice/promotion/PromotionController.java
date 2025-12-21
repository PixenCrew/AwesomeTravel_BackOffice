package renewal.awesome_travel_backoffice.promotion;

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
import renewal.awesome_travel_backoffice.promotion.dto.PromotionFilterDTO;
import renewal.awesome_travel_backoffice.promotion.service.PromotionService;
import renewal.common.entity.MenuCode;
import renewal.common.entity.Promotion;
import renewal.common.repository.MenuCodeRepository;
import renewal.common.repository.PromotionRepository;

@Controller
@RequestMapping("/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionRepository promotionRepo;
    private final MenuCodeRepository menuCodeRepo;
    private final PromotionService promotionService;

    @GetMapping
    @Transactional(readOnly = true)
    public String list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String menuCodeCode,
            @RequestParam(required = false) String active,
            @RequestParam(required = false) String startDateFrom,
            @RequestParam(required = false) String startDateTo,
            @RequestParam(required = false) String endDateFrom,
            @RequestParam(required = false) String endDateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "startTime") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        try {
            // 필터 DTO 생성
            PromotionFilterDTO filter = new PromotionFilterDTO();
            
            filter.setTitle(title);
            filter.setMenuCodeCode(menuCodeCode);
            
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

            // 정렬 설정
            Sort sort = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(sortField).ascending()
                    : Sort.by(sortField).descending();
            Pageable pageable = PageRequest.of(page, 20, sort);

            // 필터링된 리스트 조회 (페이징)
            Page<Promotion> promotionPage = promotionService.searchPromotions(filter, pageable);

            List<MenuCode> menuCodes = menuCodeRepo.findAll();

            model.addAttribute("title", "기획전 관리");
            model.addAttribute("promotionPage", promotionPage);
            model.addAttribute("promotions", promotionPage.getContent());
            model.addAttribute("filter", filter);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("menuCodes", menuCodes);
            model.addAttribute("content", "components/promotion/promotion");
            return "layout";
        } catch (Exception e) {
            model.addAttribute("title", "기획전 관리");
            model.addAttribute("promotions", List.of());
            model.addAttribute("error", "기획전 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("content", "components/promotion/promotion");
            return "layout";
        }
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String detail(@PathVariable Long id, Model model) {

        Promotion promotion = promotionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기획전을 찾을 수 없습니다."));
        List<MenuCode> menuCodes = menuCodeRepo.findAll();

        model.addAttribute("promotion", promotion);
        model.addAttribute("menuCodes", menuCodes);

        model.addAttribute("title", "기획전 상세");
        model.addAttribute("content", "components/promotion/promotionDetail");
        return "layout";
    }

    @GetMapping("/new")
    @Transactional(readOnly = true)
    public String newForm(Model model) {

        List<MenuCode> menuCodes = menuCodeRepo.findAll();

        model.addAttribute("promotion", new Promotion());
        model.addAttribute("menuCodes", menuCodes);

        model.addAttribute("title", "기획전 등록");
        model.addAttribute("content", "components/promotion/promotionDetail");
        return "layout";
    }

    @PostMapping
    public String savePromotion(
            @ModelAttribute Promotion promotion,
            @RequestParam(required = false) String menuCodeCode,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        try {
            // menuCode 바인딩 처리
            if (menuCodeCode != null && !menuCodeCode.isEmpty()) {
                MenuCode menuCode = menuCodeRepo.findByCode(menuCodeCode);
                if (menuCode != null) {
                    promotion.setMenuCode(menuCode);
                }
            }
            
            promotionRepo.save(promotion);
            redirectAttributes.addFlashAttribute("successMessage", "기획전이 저장되었습니다.");
            
            return "redirect:/promotion";
        } catch (Exception e) {
            // 에러 발생 시 폼으로 다시 돌아가기
            List<MenuCode> menuCodes = menuCodeRepo.findAll();
            model.addAttribute("promotion", promotion);
            model.addAttribute("menuCodes", menuCodes);
            model.addAttribute("error", "기획전 저장 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("title", promotion.getId() == null ? "기획전 등록" : "기획전 수정");
            model.addAttribute("content", "components/promotion/promotionDetail");
            return "layout";
        }
    }

    @PostMapping("/{id}/delete")
    @Transactional
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("successMessage", "타임딜이 삭제되었습니다.");
        return "redirect:/timeDeal";
    }

}
