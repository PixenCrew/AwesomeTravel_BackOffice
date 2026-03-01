package renewal.awesome_travel_backoffice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import renewal.awesome_travel_backoffice.admin.Admin;
import renewal.awesome_travel_backoffice.admin.AdminService;
import renewal.awesome_travel_backoffice.product.repository.ProductAdminRepository;
import renewal.awesome_travel_backoffice.user.repository.UserRepository;
import renewal.awesome_travel_backoffice.purchaseAir.repository.PurchaseAirAdminRepository;
import renewal.awesome_travel_backoffice.purchaseProduct.repository.PurchaseProductAdminRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private final AdminService adminService;
    private final ProductAdminRepository productRepository;
    private final UserRepository userRepository;
    private final PurchaseAirAdminRepository purchaseAirRepository;
    private final PurchaseProductAdminRepository purchaseProductRepository;

    // 로그인 페이지
    @GetMapping("login")
    public String loginPage(@RequestParam(required = false) String error) {
        return "login";
    }
    
    @GetMapping
    public String main(Model model, Authentication authentication) {
        
        // 대시보드 통계 데이터
        long totalProducts = productRepository.count();
        long totalMembers = userRepository.count();
        long totalAirOrders = purchaseAirRepository.count();
        long totalPackageOrders = purchaseProductRepository.count();
        
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("totalAirOrders", totalAirOrders);
        model.addAttribute("totalPackageOrders", totalPackageOrders);
        model.addAttribute("title", "대시보드");
        model.addAttribute("content", "components/main");
        
        return "layout";
    }
    
    // TEST 테스트용 가입 TEST
    @PostMapping("admin")
    public String newAdmin(Admin admin) {
        if (log.isDebugEnabled()) {
            log.debug("테스트 관리자 가입: id={}, role={}", admin.getId(), admin.getRole());
        }
        adminService.createUser(admin);
        return "redirect:/login";
    }
}
