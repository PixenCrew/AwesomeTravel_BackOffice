package renewal.awesome_travel_backoffice.banner.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.banner.service.BannerService;
import renewal.awesome_travel_backoffice.image.service.FileUploadService;
import renewal.common.entity.Banner;
import renewal.awesome_travel_backoffice.image.entity.UploadedFile;

@Controller
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;
    private final FileUploadService fileUploadService;

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
            @RequestParam(required = false) String fileUrl,
            @RequestParam(required = false) MultipartFile file,
            Model model) {

        try {
            // 파일 업로드 처리 (Google Drive)
            String fileName = fileUrl; // 기존 URL 또는 새로 업로드된 URL
            
            // 새 파일이 업로드된 경우
            if (file != null && !file.isEmpty()) {
                UploadedFile uploadedFile = fileUploadService.uploadFile(file, "banner");
                fileName = uploadedFile.getDriveImageUrl(); // 이미지 직접 표시용 URL 사용
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
                if (fileName == null || fileName.isEmpty()) {
                    throw new RuntimeException("배너 이미지는 필수입니다.");
                }
                banner = new Banner(
                    displayOrder,
                    title,
                    LocalDate.parse(startDate),
                    LocalDate.parse(endDate),
                    fileName,
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

}

