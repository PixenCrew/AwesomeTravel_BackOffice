package renewal.awesome_travel_backoffice.user.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import renewal.common.entity.MemberGradeRule;
import renewal.common.repository.MemberGradeRuleRepository;

@Controller
@RequestMapping("/tierRule")
@RequiredArgsConstructor
public class TierGradeController {

    private final MemberGradeRuleRepository memberGradeRuleRepo;

    @GetMapping
    public String getTierRule(Model model) {

        List<MemberGradeRule> rules = memberGradeRuleRepo.findAllByOrderByPriorityAsc();

        MemberGradeRuleList wrapper = new MemberGradeRuleList();
        wrapper.setMemberGradeRules(rules);

        model.addAttribute("memberGradeRuleList", wrapper);

        model.addAttribute("title", "등급 기준 관리");
        model.addAttribute("content", "components/user/memberGradeRule");

        return "layout";
    }

    @PostMapping
    public String postTierRule(@ModelAttribute MemberGradeRuleList memberGradeRuleList,
            RedirectAttributes redirectAttributes) {

        memberGradeRuleRepo.saveAll(memberGradeRuleList.getMemberGradeRules());
        redirectAttributes.addFlashAttribute("successMessage", "등급 기준이 성공적으로 저장되었습니다.");

        return "redirect:/tierRule";
    }

    @Getter
    @Setter
    public class MemberGradeRuleList {
        private List<MemberGradeRule> memberGradeRules;
    }

}