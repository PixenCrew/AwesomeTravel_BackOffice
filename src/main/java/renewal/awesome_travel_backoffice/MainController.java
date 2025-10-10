package renewal.awesome_travel_backoffice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import renewal.awesome_travel_backoffice.admin.Admin;
import renewal.awesome_travel_backoffice.admin.AdminService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class MainController {

    private final AdminService adminService;

    // 로그인 페이지
    @GetMapping("login")
    public String loginPage(@RequestParam(required = false) String error) {
        return "login";
    }
    
    @GetMapping
    public String main(Model model, Authentication authentication) {
        
        model.addAttribute("title", "Home");
        model.addAttribute("content", "components/main");
        
        return "layout";
    }
    
    // TEST 테스트용 가입 TEST
    @PostMapping("admin")
    public String newAdmin(Admin admin) {
        System.out.println(admin.getId());
        System.out.println(admin.getPassword());
        System.out.println(admin.getRole());
        adminService.createUser(admin);
        return "redirect:/login";
    }
}
