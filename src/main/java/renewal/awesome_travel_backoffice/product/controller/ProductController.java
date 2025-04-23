package renewal.awesome_travel_backoffice.product.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    // 상품 목록
    @GetMapping
    public String productIndex(Model model) {
        return "product/index";
    }

    // 새 여행상품
    @GetMapping("/new")
    public String newTravel(Model model) {
        return "product/travel";
    }
    // 새 여행상품 등록
    @PostMapping("/new")
    public String submitTravel(Model model) {
        return "product/index";
    }

    // 특정 여행상품
    @GetMapping("/{id}")
    public String selectTravel(@PathVariable Long id, Model model) {
        return "product/travel";
    }
    // 특정 여행상품 수정
    @PostMapping("/{id}")
    public String submitSelectedTravel(@PathVariable Long id, Model model) {
        return "product/index";
    }
}
