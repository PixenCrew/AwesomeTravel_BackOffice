package renewal.awesome_travel_backoffice;

import java.security.Principal;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// 모든 컨트롤러에 적용되는 함수들
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUserName(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("userName", principal.getName());
        }
    }
}