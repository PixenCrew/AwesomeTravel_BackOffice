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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import renewal.awesome_travel_backoffice.airport.service.AirportService;
import renewal.awesome_travel_backoffice.citycode.service.CityCodeService;
import renewal.awesome_travel_backoffice.common.service.ExcelService;
import renewal.awesome_travel_backoffice.countrycode.service.CountryCodeService;
import renewal.common.entity.Airport;
import renewal.common.entity.CityCode;
import renewal.common.entity.CountryCode;

import java.io.IOException;
import java.util.List;
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

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Airport> airportPage;

        if (countryCode != null && !countryCode.isEmpty() && cityCode != null && !cityCode.isEmpty()) {
            airportPage = airportService.getAirportsByCountryAndCity(countryCode, cityCode, pageable);
        } else if (countryCode != null && !countryCode.isEmpty()) {
            airportPage = airportService.getAirportsByCountry(countryCode, pageable);
        } else if (cityCode != null && !cityCode.isEmpty()) {
            airportPage = airportService.getAirportsByCity(cityCode, pageable);
        } else if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            switch (searchType != null ? searchType : "code") {
                case "nameKor": airportPage = airportService.searchByNameKor(searchKeyword, pageable); break;
                case "nameEng": airportPage = airportService.searchByNameEng(searchKeyword, pageable); break;
                default: airportPage = airportService.searchByCode(searchKeyword, pageable);
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
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("title", "공항 관리");
        model.addAttribute("content", "components/airport/airport");

        return "layout";
    }

    @GetMapping("/{code}")
    public String airportDetail(@PathVariable String code, Model model) {
        Optional<Airport> airport = airportService.getAirportByCode(code);
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
        model.addAttribute("airport", new Airport());
        model.addAttribute("countryList", countryCodeService.getAllCountryCodesList());
        model.addAttribute("cityList", cityCodeService.getAllCityCodesList());
        model.addAttribute("title", "새 공항 등록");
        model.addAttribute("content", "components/airport/airportForm");
        return "layout";
    }

    @GetMapping("/edit/{code}")
    public String editAirportForm(@PathVariable String code, Model model) {
        Optional<Airport> airport = airportService.getAirportByCode(code);
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
            @RequestParam String code,
            @RequestParam String countryCode,
            @RequestParam String cityCode,
            @RequestParam String nameKor,
            @RequestParam String nameEng,
            @RequestParam String airportType,
            Model model) {

        try {
            Airport airport = new Airport(code, cityCode, countryCode, nameKor, nameEng, Airport.AirportType.valueOf(airportType));

            if (originalCode != null && !originalCode.isEmpty()) {
                if (!originalCode.equals(code)) {
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
            model.addAttribute("error", "공항 저장 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("airport", new Airport(code, cityCode, countryCode, nameKor, nameEng, Airport.AirportType.valueOf(airportType)));
            model.addAttribute("countryList", countryCodeService.getAllCountryCodesList());
            model.addAttribute("cityList", cityCodeService.getAllCityCodesList());
            model.addAttribute("title", originalCode != null ? "공항 수정" : "새 공항 등록");
            model.addAttribute("content", "components/airport/airportForm");
            return "layout";
        }
    }

    @DeleteMapping("/{code}")
    @ResponseBody
    public ResponseEntity<String> deleteAirport(@PathVariable String code) {
        try {
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
