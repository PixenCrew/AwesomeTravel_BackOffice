package renewal.awesome_travel_backoffice.air.controller;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import renewal.awesome_travel_backoffice.air.dto.AirFilterDTO;
import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.air.entity.Airline;
import renewal.awesome_travel_backoffice.air.entity.SeatClass;
import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.awesome_travel_backoffice.air.service.AirService;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;
import renewal.awesome_travel_backoffice.air.utiles.FlightType;
import renewal.awesome_travel_backoffice.air.utiles.SeatClassType;
import renewal.awesome_travel_backoffice.code.CityCodeRepository;

@Controller
@RequestMapping("/air")
@RequiredArgsConstructor
public class AirController {

    private final AirService airService;
    private final AirRepository airRepository;
    private final CityCodeRepository cityRepo;

    // 항공 목록
    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')")
    public String listAndFilter(
            @ModelAttribute("filter") AirFilterDTO filter, // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        // 1) 정렬 객체 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // 1) 회사 목록 (체크박스용)
        List<String> allAirlines = airService.getAllCompanies();
        model.addAttribute("allAirlines", allAirlines);

        // 2) 페이징(10개 고정) + 필터링 로직
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<SeatClass> airPage = airService.searchAirs(filter, pageable);

        // 도시코드
        model.addAttribute("cityCode", cityRepo.findAll());

        // 3) View에서 쓸 속성들
        model.addAttribute("airPage", airPage);
        // model.addAttribute("airList", airPage.getContent());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Air List");
        model.addAttribute("content", "components/air"); // layout 안에서 이 fragment를 렌더

        return "layout";
    }

    @GetMapping("/new")
    public String newAir(Model model) {

        // 빈 Air 객체
        Air air = new Air();

        // 빈 Airline 객체
        air.setAirline(new Airline());
        // 빈 SeatClasses 배열
        for (SeatClassType seatType : SeatClassType.values()) {
            // SeatClass 종류만큼 SeatClass 객체 추가
            air.getSeatClasses().add(new SeatClass(air, seatType, 0L, 0L, 0L));
        }

        // 회사 목록 (드롭박스용)
        List<String> allAirlines = airService.getAllCompanies();
        model.addAttribute("allAirlines", allAirlines);

        // 도시코드
        model.addAttribute("cityCode", cityRepo.findAll());

        model.addAttribute("air", air);
        model.addAttribute("title", "New Air");
        model.addAttribute("content", "components/airDetail"); // layout 안에서 이 fragment를 렌더

        return "layout";
    }

    @PostMapping("/new")
    public String createAir(@ModelAttribute Air air, Model model) {
        // 경유 횟수로 비행 타입 지정
        if (air.getStopovers() == 0) {
            air.setFlightType(FlightType.DIRECT);
        } else {
            air.setFlightType(FlightType.STOP_OVER);
        }

        airService.createAir(air);

        return "redirect:/air";
    }

    @GetMapping("/{id}")
    public String selectAir(@PathVariable("id") Long id, Model model) {
        Air air = airRepository.getReferenceById(id);

        // 회사 목록 (드롭박스용)
        List<String> allAirlines = airService.getAllCompanies();
        model.addAttribute("allAirlines", allAirlines);

        // 도시코드
        model.addAttribute("cityCode", cityRepo.findAll());

        model.addAttribute("air", air);
        model.addAttribute("title", "Air Detail");
        model.addAttribute("content", "components/airDetail"); // layout 안에서 이 fragment를 렌더

        return "layout";
    }

    @PostMapping("/{id}")
    public String modifyAir(@ModelAttribute Air air, Authentication authentication, Model model) {
        // 경유 횟수로 비행 타입 지정
        if (air.getStopovers() == 0) {
            air.setFlightType(FlightType.DIRECT);
        } else {
            air.setFlightType(FlightType.STOP_OVER);
        }

        // seatClass 지정
        for (SeatClass seat : air.getSeatClasses()) {
            seat.setAir(air);
        }
        airRepository.save(air);

        return "redirect:/air";
    }

    // @GetMapping("/search")
    // public ResponseEntity<Page<AirResponseDto>> searchAirList(@ModelAttribute AirSearchRequestDto req) {
    //     Page<AirResponseDto> result = airService.getAirList(req);
    //     return ResponseEntity.ok(result);
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<AirResponseDto> updateAir(@PathVariable Long id, @RequestBody AirRequestDto dto) {
    //     return ResponseEntity.ok(airService.updateAir(id, dto));
    // }

    // @PatchMapping("/{id}/update-details")
    // public ResponseEntity<Void> updateDetails(@PathVariable Long id, @RequestBody AirRequestDto dto) {
    //     airService.updateDetails(id, dto);
    //     return ResponseEntity.ok().build();
    // }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam AirStatus status) {
        airService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAir(@PathVariable Long id) {
        airService.deleteAir(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public String searchAir(
            @RequestParam(defaultValue = "2000-01-01")  LocalDate from,
            @RequestParam(defaultValue = "2025-01-01")  LocalDate to,
            @RequestParam(defaultValue = "1")  Long count,
            @RequestParam(defaultValue = "departDate") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, 10, sort);

        AirFilterDTO filter = new AirFilterDTO();
        // filter.setDepart(from);
        // filter.setArrive(to);
        filter.setStartCount(count);

        Page<SeatClass> airPage = airService.searchAirs(filter, pageable);

        // View에서 쓸 속성들
        model.addAttribute("airPage", airPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Air Select");

        return "airSelect";
    }
}
