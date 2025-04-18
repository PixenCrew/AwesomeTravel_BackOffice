package renewal.awesome_travel_backoffice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class MainController {

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error) {
        return "login";
    }
    
    @GetMapping
    public String main(Model model, Authentication authentication) {
        
        model.addAttribute("title", "Home");
        model.addAttribute("content", "components/main");
        
        return "layout";
    }
    
}
