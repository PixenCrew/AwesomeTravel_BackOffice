package renewal.awesome_travel_backoffice.citycode.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.citycode.service.CityCodeService;
import renewal.awesome_travel_backoffice.common.service.ExcelService;
import renewal.awesome_travel_backoffice.countrycode.service.CountryCodeService;
import renewal.common.entity.CityCode;
import renewal.common.entity.CountryCode;
import renewal.common.repository.CityCodeRepository;

@Controller
@RequestMapping("/city-code")
@RequiredArgsConstructor
public class CityCodeController {

    private final CityCodeService cityCodeService;
    private final CityCodeRepository cityCodeRepo;
    private final CountryCodeService countryCodeService;
    private final ExcelService excelService;

    // 도시 코드 목록 페이지
    @GetMapping
    public String cityCodeList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword,
            Model model) {

        // 정렬 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CityCode> cityCodePage;

        // 검색 조건에 따른 조회
        if (country != null && !country.isEmpty()) {
            // 국가별 필터링
            if (searchKeyword != null && !searchKeyword.trim().isEmpty() && "kor".equals(searchType)) {
                cityCodePage = cityCodeService.searchByCountryAndKor(country, searchKeyword, pageable);
            } else {
                cityCodePage = cityCodeService.getCitiesByCountry(country, pageable);
            }
        } else if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // 전체 검색
            switch (searchType != null ? searchType : "code") {
                case "kor":
                    cityCodePage = cityCodeService.searchByKor(searchKeyword, pageable);
                    break;
                case "eng":
                    cityCodePage = cityCodeService.searchByEng(searchKeyword, pageable);
                    break;
                default:
                    cityCodePage = cityCodeService.searchByCode(searchKeyword, pageable);
            }
        } else {
            cityCodePage = cityCodeService.getAllCityCodes(pageable);
        }

        // 국가 목록 (필터용)
        List<CountryCode> countryList = countryCodeService.getAllCountryCodesList();

        model.addAttribute("cityCodePage", cityCodePage);
        model.addAttribute("countryList", countryList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cityCodePage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("country", country);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("title", "도시 코드 관리");
        model.addAttribute("content", "components/citycode/citycode");

        return "layout";
    }

    // 도시 코드 상세 페이지
    @GetMapping("/{code}")
    public String cityCodeDetail(@PathVariable String code, Model model) {
        Optional<CityCode> cityCode = cityCodeService.getCityCodeByCode(code);
        if (cityCode.isEmpty()) {
            return "redirect:/city-code?error=도시 코드를 찾을 수 없습니다.";
        }

        // 국가 정보 조회
        Optional<CountryCode> countryCode = countryCodeService.getCountryCodeByCode(
                cityCode.get().getCountryCode() != null ? cityCode.get().getCountryCode().getCountryCode() : null);

        model.addAttribute("cityCode", cityCode.get());
        model.addAttribute("countryCode", countryCode.orElse(null));
        model.addAttribute("title", "도시 코드 상세");
        model.addAttribute("content", "components/citycode/citycodeDetail");

        return "layout";
    }

    // 새 도시 코드 등록 폼
    @GetMapping("/new")
    public String newCityCodeForm(Model model) {
        List<CountryCode> countryList = countryCodeService.getAllCountryCodesList();

        model.addAttribute("cityCode", new CityCode());
        model.addAttribute("countryList", countryList);
        model.addAttribute("title", "새 도시 코드 등록");
        model.addAttribute("content", "components/citycode/citycodeForm");

        return "layout";
    }

    // 도시 코드 수정 폼
    @GetMapping("/edit/{code}")
    public String editCityCodeForm(@PathVariable String code, Model model) {
        Optional<CityCode> cityCode = cityCodeService.getCityCodeByCode(code);
        if (cityCode.isEmpty()) {
            return "redirect:/city-code?error=도시 코드를 찾을 수 없습니다.";
        }

        List<CountryCode> countryList = countryCodeService.getAllCountryCodesList();

        model.addAttribute("cityCode", cityCode.get());
        model.addAttribute("countryList", countryList);
        model.addAttribute("title", "도시 코드 수정");
        model.addAttribute("content", "components/citycode/citycodeForm");

        return "layout";
    }

    // 도시 코드 저장 (생성/수정)
    @PostMapping
    public String saveCityCode(
            @ModelAttribute CityCode cityCode,
            Model model) {
        try {
            cityCodeRepo.save(cityCode);
            // CityCode cityCode = new CityCode(country, code, kor, eng);

            // if (originalCode != null && !originalCode.isEmpty()) {
            // // 수정: 코드가 변경되었는지 확인
            // if (!originalCode.equals(code)) {
            // // 코드가 변경된 경우, 기존 코드 삭제 후 새 코드로 생성
            // cityCodeService.deleteCityCode(originalCode);
            // cityCodeService.createCityCode(cityCode);
            // } else {
            // // 코드가 변경되지 않은 경우, 업데이트
            // cityCodeService.updateCityCode(originalCode, cityCode);
            // }
            // } else {
            // // 생성
            // cityCodeService.createCityCode(cityCode);
            // }

            return "redirect:/city-code?message=success";
        } catch (Exception e) {
            List<CountryCode> countryList = countryCodeService.getAllCountryCodesList();
            model.addAttribute("error", "도시 코드 저장 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("cityCode", cityCode);
            model.addAttribute("countryList", countryList);
            model.addAttribute("title", cityCode.getCityCode() != null ? "도시 코드 수정" : "새 도시 코드 등록");
            model.addAttribute("content", "components/citycode/citycodeForm");
            return "layout";
        }
    }

    // 도시 코드 삭제
    @DeleteMapping("/{code}")
    @ResponseBody
    public ResponseEntity<String> deleteCityCode(@PathVariable String code) {
        try {
            cityCodeService.deleteCityCode(code);
            return ResponseEntity.ok("도시 코드가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }

    // Excel 업로드
    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                return "redirect:/city-code?error=파일을 선택해주세요.";
            }

            int count = excelService.uploadCityCodes(file);
            return "redirect:/city-code?message=" + count + "개의 도시 코드가 업로드되었습니다.";
        } catch (Exception e) {
            return "redirect:/city-code?error=업로드 실패: " + e.getMessage();
        }
    }

    // Excel 다운로드
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() {
        try {
            byte[] excelFile = excelService.downloadCityCodes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "city_codes.xlsx");

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
            byte[] excelFile = excelService.downloadCityCodeTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "city_code_template.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
