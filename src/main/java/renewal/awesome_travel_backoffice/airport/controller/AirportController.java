package renewal.awesome_travel_backoffice.airport.controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import renewal.awesome_travel_backoffice.airport.service.AirportService;
import renewal.awesome_travel_backoffice.citycode.service.CityCodeService;
import renewal.awesome_travel_backoffice.common.service.ExcelService;
import renewal.awesome_travel_backoffice.countrycode.service.CountryCodeService;
import renewal.common.entity.AirportCode;
import renewal.common.entity.CityCode;
import renewal.common.entity.CountryCode;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/airport")
@RequiredArgsConstructor
public class AirportController {

    private final AirportService airportService;
    private final CountryCodeService countryCodeService;
    private final CityCodeService cityCodeService;
    private final ExcelService excelService;

    @GetMapping
    public String airportList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) String cityCode,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword,
            Model model) {

        String resolvedSortField = Objects.requireNonNull(resolveSortField(sortField));
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sort = Sort.by(Sort.Order.by(resolvedSortField).with(direction));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AirportCode> airportPage;

        String normalizedSearchType = (searchType == null || searchType.isBlank()) ? "code" : searchType;

        if (countryCode != null && !countryCode.isEmpty() && cityCode != null && !cityCode.isEmpty()) {
            airportPage = airportService.getAirportsByCountryAndCity(countryCode, cityCode, pageable);
        } else if (countryCode != null && !countryCode.isEmpty()) {
            airportPage = airportService.getAirportsByCountry(countryCode, pageable);
        } else if (cityCode != null && !cityCode.isEmpty()) {
            airportPage = airportService.getAirportsByCity(cityCode, pageable);
        } else if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            switch (normalizedSearchType) {
                case "nameKor":
                    airportPage = airportService.searchByNameKor(searchKeyword, pageable);
                    break;
                case "nameEng":
                    airportPage = airportService.searchByNameEng(searchKeyword, pageable);
                    break;
                default:
                    airportPage = airportService.searchByCode(searchKeyword, pageable);
            }
        } else {
            airportPage = airportService.getAllAirports(pageable);
        }

        List<CountryCode> countryList = countryCodeService.getAllCountryCodesList();
        List<CityCode> cityList = cityCodeService.getAllCityCodesList();

        model.addAttribute("airportPage", airportPage);
        model.addAttribute("countryList", countryList);
        model.addAttribute("cityList", cityList);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("countryCode", countryCode);
        model.addAttribute("cityCode", cityCode);
        model.addAttribute("searchType", normalizedSearchType);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("title", "공항 관리");
        model.addAttribute("content", "components/airport/airport");

        return "layout";
    }

    @GetMapping("/{code}")
    public String airportDetail(@PathVariable String code, Model model) {
        Objects.requireNonNull(code, "공항 코드는 필수입니다.");
        Optional<AirportCode> airport = airportService.getAirportByCode(code);
        if (airport.isEmpty()) {
            return "redirect:/airport?error=공항을 찾을 수 없습니다.";
        }

        model.addAttribute("airport", airport.get());
        model.addAttribute("title", "공항 상세");
        model.addAttribute("content", "components/airport/airportDetail");
        return "layout";
    }

    @GetMapping("/new")
    public String newAirportForm(Model model) {
        model.addAttribute("airport", new AirportCode());
        model.addAttribute("countryList", countryCodeService.getAllCountryCodesList());
        model.addAttribute("cityList", cityCodeService.getAllCityCodesList());
        model.addAttribute("title", "새 공항 등록");
        model.addAttribute("content", "components/airport/airportForm");
        return "layout";
    }

    @GetMapping("/edit/{code}")
    public String editAirportForm(@PathVariable String code, Model model) {
        Objects.requireNonNull(code, "공항 코드는 필수입니다.");
        Optional<AirportCode> airport = airportService.getAirportByCode(code);
        if (airport.isEmpty()) {
            return "redirect:/airport?error=공항을 찾을 수 없습니다.";
        }

        model.addAttribute("airport", airport.get());
        model.addAttribute("countryList", countryCodeService.getAllCountryCodesList());
        model.addAttribute("cityList", cityCodeService.getAllCityCodesList());
        model.addAttribute("title", "공항 수정");
        model.addAttribute("content", "components/airport/airportForm");
        return "layout";
    }

    @PostMapping
    public String saveAirport(
            @RequestParam(required = false) String originalCode,
            @RequestParam String airportCode,
            @RequestParam String cityCode,
            @RequestParam(required = false) String countryCode,
            @RequestParam String airportKor,
            @RequestParam String airportEng,
            Model model) {

        try {
            CityCode city = cityCodeService.getCityCodeByCode(cityCode)
                    .orElseThrow(() -> new IllegalArgumentException("도시 코드를 찾을 수 없습니다: " + cityCode));

            if (countryCode != null && !countryCode.isBlank()) {
                CountryCode expectedCountry = countryCodeService.getCountryCodeByCode(countryCode)
                        .orElseThrow(() -> new IllegalArgumentException("국가 코드를 찾을 수 없습니다: " + countryCode));
                if (city.getCountryCode() == null || !Objects.equals(city.getCountryCode().getCountryCode(),
                        expectedCountry.getCountryCode())) {
                    throw new IllegalArgumentException("선택한 도시가 해당 국가에 속하지 않습니다.");
                }
            }

            AirportCode airport = new AirportCode();
            airport.setAirportCode(airportCode);
            airport.setAirportKor(airportKor);
            airport.setAirportEng(airportEng);
            airport.setCityCode(city);

            if (originalCode != null && !originalCode.isEmpty()) {
                if (!originalCode.equals(airportCode)) {
                    airportService.deleteAirport(originalCode);
                    airportService.createAirport(airport);
                } else {
                    airportService.updateAirport(originalCode, airport);
                }
            } else {
                airportService.createAirport(airport);
            }

            return "redirect:/airport?message=success";
        } catch (Exception e) {
            AirportCode formAirport = new AirportCode();
            formAirport.setAirportCode(airportCode);
            formAirport.setAirportKor(airportKor);
            formAirport.setAirportEng(airportEng);
            cityCodeService.getCityCodeByCode(cityCode).ifPresent(formAirport::setCityCode);

            model.addAttribute("error", "공항 저장 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("airport", formAirport);
            model.addAttribute("countryList", countryCodeService.getAllCountryCodesList());
            model.addAttribute("cityList", cityCodeService.getAllCityCodesList());
            model.addAttribute("selectedCountryCode", countryCode);
            model.addAttribute("selectedCityCode", cityCode);
            model.addAttribute("title", originalCode != null && !originalCode.isBlank() ? "공항 수정" : "새 공항 등록");
            model.addAttribute("content", "components/airport/airportForm");
            return "layout";
        }
    }

    /**
     * UI 정렬 필드를 엔티티 속성명으로 매핑.
     */
    private String resolveSortField(String sortField) {
        if (sortField == null || sortField.isBlank()) {
            return "airportCode";
        }

        return switch (sortField) {
            case "code" -> "airportCode";
            case "nameKor" -> "airportKor";
            case "nameEng" -> "airportEng";
            case "countryCode" -> "cityCode.countryCode.countryCode";
            case "cityCode" -> "cityCode.cityCode";
            default -> "airportCode";
        };
    }

    @DeleteMapping("/{code}")
    @ResponseBody
    public ResponseEntity<String> deleteAirport(@PathVariable String code) {
        try {
            Objects.requireNonNull(code, "공항 코드는 필수입니다.");
            airportService.deleteAirport(code);
            return ResponseEntity.ok("공항이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return "redirect:/airport?error=파일을 선택해주세요.";
            int count = excelService.uploadAirports(file);
            return "redirect:/airport?message=" + count + "개의 공항이 업로드되었습니다.";
        } catch (Exception e) {
            return "redirect:/airport?error=업로드 실패: " + e.getMessage();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() {
        try {
            byte[] excelFile = excelService.downloadAirports();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "airports.xlsx");
            return ResponseEntity.ok().headers(headers).body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            byte[] excelFile = excelService.downloadAirportTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "airport_template.xlsx");
            return ResponseEntity.ok().headers(headers).body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
