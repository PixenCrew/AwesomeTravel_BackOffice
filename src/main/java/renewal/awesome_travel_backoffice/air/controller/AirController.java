package renewal.awesome_travel_backoffice.air.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Sort;

import renewal.awesome_travel_backoffice.air.dto.AirFilterDTO;
import renewal.common.entity.Air;
import renewal.common.entity.Air.AirStatus;
import renewal.common.entity.Air.FlightType;
import renewal.common.entity.AirReservation;
import renewal.common.entity.Airline;
import renewal.common.entity.SeatClass;
import renewal.common.entity.SeatClass.SeatClassType;
import renewal.common.repository.CityCodeRepository;
import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.awesome_travel_backoffice.air.repository.AirReservationRepository;
import renewal.awesome_travel_backoffice.air.repository.AirlineRepository;
import renewal.awesome_travel_backoffice.air.service.AirService;
import renewal.awesome_travel_backoffice.airport.repository.AirportCodeRepository;

@Controller
@RequestMapping("/air")
@RequiredArgsConstructor
public class AirController {

    private final AirService airService;
    private final AirRepository airRepo;
    private final AirlineRepository airlineRepo;
    private final AirReservationRepository airReservationRepo;
    private final AirportCodeRepository airportRepo;

    // 항공 목록
    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')")
    public String listAndFilter(
            @ModelAttribute AirFilterDTO filter, // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        // 1) 정렬 객체 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // 1) 회사 목록 (체크박스용)
        // List<String> allAirlines = airService.getAllCompanies();
        List<Airline> allAirlines = airlineRepo.findAll();
        model.addAttribute("allAirlines", allAirlines);

        // 2) 페이징(50개 고정) + 필터링 로직
        Pageable pageable = PageRequest.of(page, 50, sort);
        Page<SeatClass> airPage = airService.searchAirs(filter, pageable);

        // 공항 코드
        model.addAttribute("airportCode", airportRepo.findAll());

        // 3) View에서 쓸 속성들
        model.addAttribute("airPage", airPage);
        model.addAttribute("filter", filter); // 필터 객체 추가
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Air List");
        // air.html에서 참조하는 플래그 기본값 설정 (null → SpEL 오류 방지)
        model.addAttribute("isSelectionPage", false);
        model.addAttribute("content", "components/air/air"); // layout 안에서 이 fragment를 렌더

        return "layout";
    }

    @GetMapping("/new")
    public String newAir(Model model) {

        // 빈 Air 객체
        Air air = new Air();

        // 빈 Airline 객체
        air.setAirline(new Airline());
        // 빈 SeatClasses 배열
        for (SeatClass.SeatClassType seatType : SeatClassType.values()) {
            // SeatClass 종류만큼 SeatClass 객체 추가
            SeatClass seat = new SeatClass();
            seat.setClassType(seatType);
            air.getSeatClasses().add(seat);
        }

        // 항공사 목록 (드롭박스용)
        List<Airline> allAirlines = airlineRepo.findAll();
        model.addAttribute("allAirlines", allAirlines);

        // 공항코드 목록 (드롭박스용)
        model.addAttribute("airportCode", airportRepo.findAll());

        model.addAttribute("air", air);
        model.addAttribute("title", "New Air");
        model.addAttribute("content", "components/air/airDetail"); // layout 안에서 이 fragment를 렌더

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
    public String selectAir(@PathVariable Long id, Model model) {
        Air air = airRepo.getReferenceById(id);

        // 항공사 목록 (드롭박스용)
        List<Airline> allAirlines = airlineRepo.findAll();
        model.addAttribute("allAirlines", allAirlines);

        // 공항코드 목록 (드롭박스용)
        model.addAttribute("airportCode", airportRepo.findAll());

        model.addAttribute("air", air);
        model.addAttribute("title", "Air Detail");
        model.addAttribute("content", "components/air/airDetail"); // layout 안에서 이 fragment를 렌더

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

        airService.createAir(air);

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
            @ModelAttribute AirFilterDTO filter, // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "air.departDateTime") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, 50, sort);

        Page<SeatClass> airPage = airService.searchAirs(filter, pageable);

        // View에서 쓸 속성들
        model.addAttribute("airPage", airPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Air Select");
        model.addAttribute("airportCode", airportRepo.findAll());
        
        // 항공선택 플래그
        model.addAttribute("isSelectionPage", true);

        // return "airSelect";
        return "components/air/airSearchPopup";
    }

    @GetMapping("/seat/{id}")
    public String showReservation(@PathVariable Long id, Model model) {

        List<AirReservation> reservations = airReservationRepo.findAllBySeatClassId(id);
        model.addAttribute("reservations", reservations);
        
        return "components/air/airReservation";
    }
}
