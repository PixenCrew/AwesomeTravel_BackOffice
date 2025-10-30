package renewal.awesome_travel_backoffice.menuCode.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import renewal.common.entity.MenuCode;
import renewal.common.repository.MenuCodeRepository;

@RequiredArgsConstructor
@RequestMapping("/menu-code")
@Controller
public class MenuCodeController {

    private final MenuCodeRepository menuCodeRepository;

    @GetMapping
    public String getMenuCodeList(Model model) {

        List<MenuCode> menuCodes = menuCodeRepository.findAll();

        model.addAttribute("title", "MenuCode List");
        model.addAttribute("menuCodes", menuCodes);
        model.addAttribute("content", "components/menuCode/menuCode");
        return "layout";
    }

    @GetMapping("/{id}")
    public String getMenuCodeDetail(@PathVariable Long id, Model model) {

        MenuCode menuCode = menuCodeRepository.findByCode2(id).get();

        model.addAttribute("title", "MenuCode Detail");
        model.addAttribute("menuCode", menuCode);
        model.addAttribute("content", "components/menuCode/menuCodeDetail");
        return "layout";
    }

    @GetMapping("/check/{id}")
    public ResponseEntity<Map<String, Object>> checkMenuCodeDupe(@PathVariable Long id) {

        Optional<MenuCode> menuCode = menuCodeRepository.findByCode2(id);

        Map<String, Object> response = new HashMap<>();
        if (menuCode.isPresent()) {
            response.put("duplicate", true);
            response.put("message", "이미 사용 중인 코드입니다.");
            return ResponseEntity.status(409).body(response); // 409 Conflict
        } else {
            response.put("duplicate", false);
            response.put("message", "사용 가능한 코드입니다.");
            return ResponseEntity.ok(response); // 200 OK
        }
    }

    @GetMapping("/new")
    public String newMenuCode(Model model) {

        MenuCode menuCode = new MenuCode();

        model.addAttribute("title", "New MenuCode");
        model.addAttribute("menuCode", menuCode);
        model.addAttribute("content", "components/menuCode/menuCodeDetail");

        return "layout";
    }

    @PostMapping("/new")
    public String submitNewMenuCode(@ModelAttribute MenuCode menuCode) {
        System.out.println(menuCode.getDetails());
        menuCodeRepository.save(menuCode);

        return "redirect:/menu-code";
    }

    @PostMapping("/{id}")
    public String modifyMenuCodeDetail(@ModelAttribute MenuCode menuCode, Model model) {

        menuCodeRepository.save(menuCode);

        return "redirect:/menu-code";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuCode(@PathVariable Long id) {
        menuCodeRepository.deleteByCode2(id);
        return ResponseEntity.ok().build();
    }

}
