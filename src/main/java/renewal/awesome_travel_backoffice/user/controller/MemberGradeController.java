package renewal.awesome_travel_backoffice.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import renewal.common.entity.User;
import renewal.awesome_travel_backoffice.user.repository.UserRepository;
import renewal.common.entity.User.MemberGrade;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tier")
public class MemberGradeController {
    
    private final UserRepository userRepository;

    @GetMapping
    public String gradeList(
            @RequestParam(name = "grade", required = false) String grade,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, 10);
        Page<User> userPage;
        
        // 검색어가 있는 경우
        if (search != null && !search.trim().isEmpty()) {
            if (grade == null || "전체".equals(grade)) {
                // 검색어만 있는 경우
                userPage = userRepository.findByNameContainingOrEmailContainingOrPhoneContaining(
                    search.trim(), search.trim(), search.trim(), pageable);
                model.addAttribute("selectedGrade", null);
            } else {
                // 검색어 + 등급 필터
                try {
                    MemberGrade memberGrade = MemberGrade.valueOf(grade);
                    userPage = userRepository.findByNameContainingOrEmailContainingOrPhoneContainingAndGrade(
                        search.trim(), search.trim(), search.trim(), memberGrade, pageable);
                    model.addAttribute("selectedGrade", memberGrade);
                } catch (IllegalArgumentException e) {
                    userPage = userRepository.findByNameContainingOrEmailContainingOrPhoneContaining(
                        search.trim(), search.trim(), search.trim(), pageable);
                    model.addAttribute("selectedGrade", null);
                }
            }
            model.addAttribute("searchKeyword", search.trim());
        } else {
            model.addAttribute("searchKeyword", null);
            // 검색어가 없는 경우 (기존 로직)
            if (grade == null || "전체".equals(grade)) {
                userPage = userRepository.findAll(pageable);
                model.addAttribute("selectedGrade", null);
            } else {
                try {
                    MemberGrade memberGrade = MemberGrade.valueOf(grade);
                    userPage = userRepository.findByGrade(memberGrade, pageable);
                    model.addAttribute("selectedGrade", memberGrade);
                } catch (IllegalArgumentException e) {
                    userPage = userRepository.findAll(pageable);
                    model.addAttribute("selectedGrade", null);
                }
            }
        }
        
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("totalCount", userPage.getTotalElements());
        model.addAttribute("currentPage", userPage.getNumber());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("grades", MemberGrade.values());
        model.addAttribute("title", "회원 등급 관리");
        model.addAttribute("content", "components/user/memberGrade");
        
        return "layout";
    }

    @PostMapping("/{id}/update")
    public String updateGrade(
            @PathVariable(name = "id") Long id, 
            @RequestParam(name = "grade") MemberGrade grade, 
            RedirectAttributes redirectAttributes) {
        
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setGrade(grade);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("message", "등급이 변경되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "회원을 찾을 수 없습니다.");
        }
        
        return "redirect:/tier";
    }
}
