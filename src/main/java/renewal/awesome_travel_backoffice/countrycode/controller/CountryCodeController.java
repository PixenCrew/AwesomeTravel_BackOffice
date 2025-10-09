package renewal.awesome_travel_backoffice.countrycode.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import renewal.awesome_travel_backoffice.common.service.ExcelService;
import renewal.awesome_travel_backoffice.countrycode.service.CountryCodeService;
import renewal.common.entity.CountryCode;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/country-code")
@RequiredArgsConstructor
public class CountryCodeController {

    private final CountryCodeService countryCodeService;
    private final ExcelService excelService;

    // 국가 코드 목록 페이지
    @GetMapping
    public String countryCodeList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword,
            Model model) {

        // 정렬 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CountryCode> countryCodePage;

        // 검색 조건에 따른 조회
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            switch (searchType != null ? searchType : "code") {
                case "nameKor":
                    countryCodePage = countryCodeService.searchByNameKor(searchKeyword, pageable);
                    break;
                case "nameEng":
                    countryCodePage = countryCodeService.searchByNameEng(searchKeyword, pageable);
                    break;
                default:
                    countryCodePage = countryCodeService.searchByCode(searchKeyword, pageable);
            }
        } else {
            countryCodePage = countryCodeService.getAllCountryCodes(pageable);
        }

        model.addAttribute("countryCodePage", countryCodePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", countryCodePage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("title", "국가 코드 관리");
        model.addAttribute("content", "components/countrycode/countrycode");

        return "layout";
    }

    // 국가 코드 상세 페이지
    @GetMapping("/{code}")
    public String countryCodeDetail(@PathVariable String code, Model model) {
        Optional<CountryCode> countryCode = countryCodeService.getCountryCodeByCode(code);
        if (countryCode.isEmpty()) {
            return "redirect:/country-code?error=국가 코드를 찾을 수 없습니다.";
        }

        model.addAttribute("countryCode", countryCode.get());
        model.addAttribute("title", "국가 코드 상세");
        model.addAttribute("content", "components/countrycode/countrycodeDetail");

        return "layout";
    }

    // 새 국가 코드 등록 폼
    @GetMapping("/new")
    public String newCountryCodeForm(Model model) {
        model.addAttribute("countryCode", new CountryCode());
        model.addAttribute("title", "새 국가 코드 등록");
        model.addAttribute("content", "components/countrycode/countrycodeForm");

        return "layout";
    }

    // 국가 코드 수정 폼
    @GetMapping("/edit/{code}")
    public String editCountryCodeForm(@PathVariable String code, Model model) {
        Optional<CountryCode> countryCode = countryCodeService.getCountryCodeByCode(code);
        if (countryCode.isEmpty()) {
            return "redirect:/country-code?error=국가 코드를 찾을 수 없습니다.";
        }

        model.addAttribute("countryCode", countryCode.get());
        model.addAttribute("title", "국가 코드 수정");
        model.addAttribute("content", "components/countrycode/countrycodeForm");

        return "layout";
    }

    // 국가 코드 저장 (생성/수정)
    @PostMapping
    public String saveCountryCode(
            @RequestParam(required = false) String originalCode,
            @RequestParam String code,
            @RequestParam String nameKor,
            @RequestParam String nameEng,
            Model model) {

        try {
            CountryCode countryCode = new CountryCode(code, nameKor, nameEng);

            if (originalCode != null && !originalCode.isEmpty()) {
                // 수정: 코드가 변경되었는지 확인
                if (!originalCode.equals(code)) {
                    // 코드가 변경된 경우, 기존 코드 삭제 후 새 코드로 생성
                    countryCodeService.deleteCountryCode(originalCode);
                    countryCodeService.createCountryCode(countryCode);
                } else {
                    // 코드가 변경되지 않은 경우, 업데이트
                    countryCodeService.updateCountryCode(originalCode, countryCode);
                }
            } else {
                // 생성
                countryCodeService.createCountryCode(countryCode);
            }

            return "redirect:/country-code?message=success";
        } catch (Exception e) {
            model.addAttribute("error", "국가 코드 저장 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("countryCode", new CountryCode(code, nameKor, nameEng));
            model.addAttribute("title", originalCode != null ? "국가 코드 수정" : "새 국가 코드 등록");
            model.addAttribute("content", "components/countrycode/countrycodeForm");
            return "layout";
        }
    }

    // 국가 코드 삭제
    @DeleteMapping("/{code}")
    @ResponseBody
    public ResponseEntity<String> deleteCountryCode(@PathVariable String code) {
        try {
            countryCodeService.deleteCountryCode(code);
            return ResponseEntity.ok("국가 코드가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }

    // Excel 업로드
    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                return "redirect:/country-code?error=파일을 선택해주세요.";
            }

            int count = excelService.uploadCountryCodes(file);
            return "redirect:/country-code?message=" + count + "개의 국가 코드가 업로드되었습니다.";
        } catch (Exception e) {
            return "redirect:/country-code?error=업로드 실패: " + e.getMessage();
        }
    }

    // Excel 다운로드
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() {
        try {
            byte[] excelFile = excelService.downloadCountryCodes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "country_codes.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Excel 템플릿 다운로드
    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            byte[] excelFile = excelService.downloadCountryCodeTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "country_code_template.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
