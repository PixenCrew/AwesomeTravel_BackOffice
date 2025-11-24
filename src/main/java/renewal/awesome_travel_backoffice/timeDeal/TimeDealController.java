package renewal.awesome_travel_backoffice.timeDeal;

import java.util.ArrayList;
import java.util.List;

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
import renewal.common.entity.Product;
import renewal.common.entity.TimeDeal;
import renewal.common.repository.ProductRepository;
import renewal.common.repository.TimeDealRepository;

@Controller
@RequestMapping("/timeDeal")
@RequiredArgsConstructor
public class TimeDealController {

    private final TimeDealRepository timeDealRepo;
    private final ProductRepository productRepo;

    @GetMapping
    public String list(Model model) {
        List<TimeDeal> list = timeDealRepo.findAll(Sort.by("startTime").descending());
        model.addAttribute("list", list);

        model.addAttribute("title", "타임딜 관리");
        model.addAttribute("content", "components/timeDeal/timeDeal");
        return "layout";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        TimeDeal td = timeDealRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("타임딜 없음"));

        List<Product> products = productRepo.findByTimeDeal(td);

        model.addAttribute("timeDeal", td);
        model.addAttribute("products", products);

        model.addAttribute("title", "타임딜 상세");
        model.addAttribute("content", "components/timeDeal/timeDealDetail");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute("timeDeal", new TimeDeal());
        model.addAttribute("products", new ArrayList<>());

        model.addAttribute("title", "타임딜 등록");
        model.addAttribute("content", "components/timeDeal/timeDealDetail");
        return "layout";
    }

    @PostMapping
    public String saveTimeDeal(
            @ModelAttribute TimeDeal timeDeal,
            @RequestParam List<Long> productIds,
            RedirectAttributes redirectAttributes) {

        // 1) TimeDeal 저장 (신규 & 수정 동일)
        TimeDeal savedTimeDeal = timeDealRepo.save(timeDeal);

        // 2) 현재 이 TimeDeal에 연결된 상품들
        List<Product> oldList = productRepo.findByTimeDeal(savedTimeDeal);

        // 3) 기존 상품 → productIds에 없으면 연결 해제
        for (Product product : oldList) {
            if (productIds == null || !productIds.contains(product.getId())) {
                product.setTimeDeal(null);
                productRepo.save(product);
            }
        }

        // 4) productIds에 포함된 상품 → TimeDeal 적용
        if (productIds != null) {
            for (Long pid : productIds) {
                productRepo.findById(pid).ifPresent(p -> {
                    p.setTimeDeal(savedTimeDeal);
                    productRepo.save(p);
                });
            }
        }

        // 5) 성공 메시지
        redirectAttributes.addFlashAttribute("successMessage", "타임딜이 저장되었습니다.");

        // 6) 저장 후 상세 페이지로 이동
        return "redirect:/timeDeal/" + savedTimeDeal.getId();
    }

    @PostMapping("/{id}/delete")
    @Transactional
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        TimeDeal timeDeal = timeDealRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("타임딜 없음"));

        // 1) 연결된 Product들 조회
        List<Product> productList = productRepo.findByTimeDeal(timeDeal);

        // 2) 각 Product에서 TimeDeal 연결 끊기
        for (Product p : productList) {
            p.setTimeDeal(null);
        }
        productRepo.saveAll(productList);

        // 3) 타임딜 삭제
        timeDealRepo.delete(timeDeal);

        redirectAttributes.addFlashAttribute("successMessage", "타임딜이 삭제되었습니다.");
        return "redirect:/timeDeal";
    }

}
