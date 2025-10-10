package renewal.awesome_travel_backoffice.banner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import renewal.awesome_travel_backoffice.banner.service.BannerService;
import renewal.common.entity.Banner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;
    private static final String UPLOAD_DIR = "images/banners/";

    // 배너 목록 페이지
    @GetMapping
    public String bannerList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {

        // 정렬 설정
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortField).ascending() 
            : Sort.by(sortField).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Banner> bannerPage;

        // 검색 조건에 따른 조회
        if (title != null && !title.trim().isEmpty()) {
            bannerPage = bannerService.searchBannersByTitle(title, pageable);
        } else if (active != null) {
            bannerPage = bannerService.searchBannersByActive(active, pageable);
        } else if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            bannerPage = bannerService.searchBannersByDateRange(
                LocalDate.parse(startDate), 
                LocalDate.parse(endDate), 
                pageable
            );
        } else {
            bannerPage = bannerService.getAllBanners(pageable);
        }

        model.addAttribute("bannerPage", bannerPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bannerPage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "배너 관리");
        model.addAttribute("content", "components/banner/banner");

        return "layout";
    }

    // 배너 상세 페이지
    @GetMapping("/{id}")
    public String bannerDetail(@PathVariable Long id, Model model) {
        Optional<Banner> banner = bannerService.getBannerById(id);
        if (banner.isEmpty()) {
            return "redirect:/banner?error=배너를 찾을 수 없습니다.";
        }

        model.addAttribute("banner", banner.get());
        model.addAttribute("title", "배너 상세");
        model.addAttribute("content", "components/banner/bannerDetail");

        return "layout";
    }

    // 새 배너 등록 폼
    @GetMapping("/new")
    public String newBannerForm(Model model) {
        model.addAttribute("banner", new Banner());
        model.addAttribute("title", "새 배너 등록");
        model.addAttribute("content", "components/banner/bannerForm");

        return "layout";
    }

    // 배너 수정 폼
    @GetMapping("/edit/{id}")
    public String editBannerForm(@PathVariable Long id, Model model) {
        Optional<Banner> banner = bannerService.getBannerById(id);
        if (banner.isEmpty()) {
            return "redirect:/banner?error=배너를 찾을 수 없습니다.";
        }

        model.addAttribute("banner", banner.get());
        model.addAttribute("title", "배너 수정");
        model.addAttribute("content", "components/banner/bannerForm");

        return "layout";
    }

    // 배너 저장 (생성/수정)
    @PostMapping
    public String saveBanner(
            @RequestParam(required = false) Long id,
            @RequestParam String title,
            @RequestParam Integer displayOrder,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Boolean active,
            @RequestParam String url,
            @RequestParam("file") MultipartFile file,
            Model model) {

        try {
            // 파일 업로드 처리
            String fileName = null;
            if (!file.isEmpty()) {
                fileName = uploadFile(file);
            }

            Banner banner;
            if (id != null) {
                // 수정
                banner = bannerService.getBannerById(id)
                    .orElseThrow(() -> new RuntimeException("배너를 찾을 수 없습니다."));
                
                banner.setTitle(title);
                banner.setDisplayOrder(displayOrder);
                banner.setStartDate(LocalDate.parse(startDate));
                banner.setEndDate(LocalDate.parse(endDate));
                banner.setActive(active != null ? active : true);
                banner.setUrl(url);
                
                if (fileName != null) {
                    banner.setFile(fileName);
                }
                
                bannerService.updateBanner(id, banner);
            } else {
                // 생성
                banner = new Banner(
                    displayOrder,
                    title,
                    LocalDate.parse(startDate),
                    LocalDate.parse(endDate),
                    fileName != null ? fileName : "",
                    url
                );
                banner.setActive(active != null ? active : true);
                bannerService.createBanner(banner);
            }

            return "redirect:/banner?message=success";
        } catch (Exception e) {
            model.addAttribute("error", "배너 저장 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("banner", new Banner());
            model.addAttribute("title", id != null ? "배너 수정" : "새 배너 등록");
            model.addAttribute("content", "components/banner/bannerForm");
            return "layout";
        }
    }

    // 배너 삭제
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteBanner(@PathVariable Long id) {
        try {
            bannerService.deleteBanner(id);
            return ResponseEntity.ok("배너가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }

    // 배너 활성화/비활성화 토글
    @PostMapping("/{id}/toggle")
    @ResponseBody
    public ResponseEntity<String> toggleBannerActive(@PathVariable Long id) {
        try {
            bannerService.toggleBannerActive(id);
            return ResponseEntity.ok("배너 상태가 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("상태 변경 실패: " + e.getMessage());
        }
    }

    // 배너 순서 변경
    @PostMapping("/{id}/order")
    @ResponseBody
    public ResponseEntity<String> updateBannerOrder(@PathVariable Long id, @RequestParam Integer order) {
        try {
            bannerService.updateBannerOrder(id, order);
            return ResponseEntity.ok("배너 순서가 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("순서 변경 실패: " + e.getMessage());
        }
    }

    // 파일 업로드 처리
    private String uploadFile(MultipartFile file) throws IOException {
        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일명 생성 (타임스탬프 + 원본 파일명)
        String originalFileName = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFileName;
        
        // 파일 저장
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return UPLOAD_DIR + fileName;
    }
}

