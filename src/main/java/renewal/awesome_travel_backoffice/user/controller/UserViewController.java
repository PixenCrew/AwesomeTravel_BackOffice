package renewal.awesome_travel_backoffice.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import renewal.awesome_travel_backoffice.user.dto.request.UserRequestDto;
import renewal.awesome_travel_backoffice.user.dto.response.UserResponseDto;
import renewal.awesome_travel_backoffice.user.service.UserService;
import renewal.common.entity.User.UserProvider;
import renewal.common.entity.User.UserRole;
import renewal.common.entity.User.UserStatus;
import renewal.awesome_travel_backoffice.comment.service.CommentService;
import renewal.awesome_travel_backoffice.comment.dto.response.CommentResponseDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class UserViewController {
    
    private final UserService userService;
    private final CommentService commentService;
    
    // 회원 목록
    @GetMapping
    public String userList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, 10);
        Page<UserResponseDto> userPage;
        
        if (keyword != null && !keyword.isEmpty() || status != null && !status.isEmpty()) {
            UserStatus userStatus = null;
            if (status != null && !status.isEmpty()) {
                try {
                    userStatus = UserStatus.valueOf(status);
                } catch (IllegalArgumentException e) {
                    // 잘못된 상태값은 무시
                }
            }
            userPage = userService.searchUsers(keyword, userStatus, pageable);
        } else {
            userPage = userService.getAllUsers(pageable);
        }
        
        model.addAttribute("title", "회원 정보 관리");
        model.addAttribute("content", "components/user/member");
        model.addAttribute("totalCount", userPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("users", userPage.getContent());
        
        // 검색 조건 유지
        Map<String, Object> param = new HashMap<>();
        param.put("keyword", keyword);
        param.put("status", status);
        model.addAttribute("param", param);
        
        return "layout";
    }
    
    // 회원 등록 폼
    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("title", "회원 등록");
        model.addAttribute("content", "components/user/memberForm");
        model.addAttribute("user", new UserRequestDto());
        model.addAttribute("providers", UserProvider.values());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("statuses", UserStatus.values());
        return "layout";
    }
    
    // 회원 등록 처리
    @PostMapping("/new")
    public String createUser(UserRequestDto userRequestDto, RedirectAttributes redirectAttributes) {
        try {
            Long userId = userService.createUser(userRequestDto);
            redirectAttributes.addFlashAttribute("message", "회원이 성공적으로 등록되었습니다.");
            return "redirect:/member";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/member/new";
        }
    }
    
    // 회원 상세보기
    @GetMapping("/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        try {
            UserResponseDto user = userService.getUserById(id);
            // 최근 댓글 5개 조회
            List<CommentResponseDto> recentComments = commentService.getRecentCommentsByUser(id);
            model.addAttribute("title", "회원 상세 정보");
            model.addAttribute("content", "components/user/memberDetail");
            model.addAttribute("user", user);
            model.addAttribute("recentComments", recentComments);
            return "layout";
        } catch (IllegalArgumentException e) {
            return "redirect:/member?error=notFound";
        }
    }
    
    // 회원 수정 폼
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        try {
            UserResponseDto user = userService.getUserById(id);
            UserRequestDto userRequestDto = new UserRequestDto();
            // UserResponseDto를 UserRequestDto로 변환
            userRequestDto.setEmail(user.getEmail());
            userRequestDto.setName(user.getName());
            userRequestDto.setPhone(user.getPhone());
            userRequestDto.setBirthDate(user.getBirthDate());
            userRequestDto.setProvider(user.getProvider());
            userRequestDto.setSocialId(user.getSocialId());
            userRequestDto.setRole(user.getRole());
            userRequestDto.setStatus(user.getStatus());
            userRequestDto.setPassportNumber(user.getPassportNumber());
            userRequestDto.setPassportIssuedDate(user.getPassportIssuedDate());
            userRequestDto.setPassportExpiryDate(user.getPassportExpiryDate());
            userRequestDto.setPassportCountry(user.getPassportCountry());
            userRequestDto.setEnglishFirstName(user.getEnglishFirstName());
            userRequestDto.setEnglishLastName(user.getEnglishLastName());
            userRequestDto.setEmailVerified(user.getEmailVerified());
            userRequestDto.setMarketingConsent(user.getMarketingConsent());
            
            model.addAttribute("title", "회원 수정");
            model.addAttribute("content", "components/user/memberForm");
            model.addAttribute("user", userRequestDto);
            model.addAttribute("userId", id);
            model.addAttribute("providers", UserProvider.values());
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("statuses", UserStatus.values());
            return "layout";
        } catch (IllegalArgumentException e) {
            return "redirect:/member?error=notFound";
        }
    }
    
    // 회원 수정 처리
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, UserRequestDto userRequestDto, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, userRequestDto);
            redirectAttributes.addFlashAttribute("message", "회원 정보가 성공적으로 수정되었습니다.");
            return "redirect:/member";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/member/edit/" + id;
        }
    }
    
    // 회원 삭제
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "회원이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/member";
    }
}