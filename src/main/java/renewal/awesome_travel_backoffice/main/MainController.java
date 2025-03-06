package renewal.awesome_travel_backoffice.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller(value = "/")
public class MainController {

    @GetMapping
    public String main(Model model) {
        return "main";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error) {
        return "login";
    }
    
}
