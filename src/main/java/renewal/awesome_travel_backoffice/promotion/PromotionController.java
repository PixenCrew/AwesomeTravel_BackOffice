package renewal.awesome_travel_backoffice.promotion;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public String list(Model model) {
        List<Promotion> promotions = promotionRepo.findAll();

        model.addAttribute("title", "기획전 관리");
        model.addAttribute("promotions", promotions);
        model.addAttribute("content", "components/promotion/promotion");
        return "layout";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        Promotion promotion = promotionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("타임딜 없음"));
        List<MenuCode> menuCodes = menuCodeRepo.findAll();

        model.addAttribute("promotion", promotion);
        model.addAttribute("menuCodes", menuCodes);

        model.addAttribute("title", "기획전 상세");
        model.addAttribute("content", "components/promotion/promotionDetail");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model) {

        List<MenuCode> menuCodes = menuCodeRepo.findAll();

        model.addAttribute("promotion", new Promotion());
        model.addAttribute("menuCodes", menuCodes);

        model.addAttribute("title", "기획전 등록");
        model.addAttribute("content", "components/promotion/promotionDetail");
        return "layout";
    }

    @PostMapping
    public String savePromotion(@ModelAttribute Promotion promotion, RedirectAttributes redirectAttributes) {

        promotionRepo.save(promotion);
        redirectAttributes.addFlashAttribute("successMessage", "기획전이 저장되었습니다.");

        return "redirect:/promotion";
    }

    @PostMapping("/{id}/delete")
    @Transactional
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("successMessage", "타임딜이 삭제되었습니다.");
        return "redirect:/timeDeal";
    }

}
