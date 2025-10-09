package renewal.awesome_travel_backoffice.airline.controller;

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
import renewal.awesome_travel_backoffice.airline.service.AirlineService;
import renewal.awesome_travel_backoffice.common.service.ExcelService;
import renewal.common.entity.Airline;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/airline")
@RequiredArgsConstructor
public class AirlineController {

    private final AirlineService airlineService;
    private final ExcelService excelService;

    // 항공사 목록 페이지
    @GetMapping
    public String airlineList(
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
        Page<Airline> airlinePage;

        // 검색 조건에 따른 조회
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            switch (searchType != null ? searchType : "code") {
                case "nameKor":
                    airlinePage = airlineService.searchByNameKor(searchKeyword, pageable);
                    break;
                case "nameEng":
                    airlinePage = airlineService.searchByNameEng(searchKeyword, pageable);
                    break;
                default:
                    airlinePage = airlineService.searchByCode(searchKeyword, pageable);
            }
        } else {
            airlinePage = airlineService.getAllAirlines(pageable);
        }

        model.addAttribute("airlinePage", airlinePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", airlinePage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("title", "항공사 관리");
        model.addAttribute("content", "components/airline/airline");

        return "layout";
    }

    // 항공사 상세 페이지
    @GetMapping("/{code}")
    public String airlineDetail(@PathVariable String code, Model model) {
        Optional<Airline> airline = airlineService.getAirlineByCode(code);
        if (airline.isEmpty()) {
            return "redirect:/airline?error=항공사를 찾을 수 없습니다.";
        }

        model.addAttribute("airline", airline.get());
        model.addAttribute("title", "항공사 상세");
        model.addAttribute("content", "components/airline/airlineDetail");

        return "layout";
    }

    // 새 항공사 등록 폼
    @GetMapping("/new")
    public String newAirlineForm(Model model) {
        model.addAttribute("airline", new Airline());
        model.addAttribute("title", "새 항공사 등록");
        model.addAttribute("content", "components/airline/airlineForm");

        return "layout";
    }

    // 항공사 수정 폼
    @GetMapping("/edit/{code}")
    public String editAirlineForm(@PathVariable String code, Model model) {
        Optional<Airline> airline = airlineService.getAirlineByCode(code);
        if (airline.isEmpty()) {
            return "redirect:/airline?error=항공사를 찾을 수 없습니다.";
        }

        model.addAttribute("airline", airline.get());
        model.addAttribute("title", "항공사 수정");
        model.addAttribute("content", "components/airline/airlineForm");

        return "layout";
    }

    // 항공사 저장 (생성/수정)
    @PostMapping
    public String saveAirline(
            @RequestParam(required = false) String originalCode,
            @RequestParam String code,
            @RequestParam String nameKor,
            @RequestParam String nameEng,
            @RequestParam(required = false, defaultValue = "false") boolean infantSeatsRequired,
            Model model) {

        try {
            Airline airline = new Airline(code, nameKor, nameEng, infantSeatsRequired);

            if (originalCode != null && !originalCode.isEmpty()) {
                // 수정: 코드가 변경되었는지 확인
                if (!originalCode.equals(code)) {
                    // 코드가 변경된 경우, 기존 코드 삭제 후 새 코드로 생성
                    airlineService.deleteAirline(originalCode);
                    airlineService.createAirline(airline);
                } else {
                    // 코드가 변경되지 않은 경우, 업데이트
                    airlineService.updateAirline(originalCode, airline);
                }
            } else {
                // 생성
                airlineService.createAirline(airline);
            }

            return "redirect:/airline?message=success";
        } catch (Exception e) {
            model.addAttribute("error", "항공사 저장 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("airline", new Airline(code, nameKor, nameEng, infantSeatsRequired));
            model.addAttribute("title", originalCode != null ? "항공사 수정" : "새 항공사 등록");
            model.addAttribute("content", "components/airline/airlineForm");
            return "layout";
        }
    }

    // 항공사 삭제
    @DeleteMapping("/{code}")
    @ResponseBody
    public ResponseEntity<String> deleteAirline(@PathVariable String code) {
        try {
            airlineService.deleteAirline(code);
            return ResponseEntity.ok("항공사가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }

    // Excel 업로드
    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                return "redirect:/airline?error=파일을 선택해주세요.";
            }

            int count = excelService.uploadAirlines(file);
            return "redirect:/airline?message=" + count + "개의 항공사가 업로드되었습니다.";
        } catch (Exception e) {
            return "redirect:/airline?error=업로드 실패: " + e.getMessage();
        }
    }

    // Excel 다운로드
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() {
        try {
            byte[] excelFile = excelService.downloadAirlines();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "airlines.xlsx");

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
            byte[] excelFile = excelService.downloadAirlineTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "airline_template.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
