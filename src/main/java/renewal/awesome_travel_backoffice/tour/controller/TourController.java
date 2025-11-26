package renewal.awesome_travel_backoffice.tour.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.common.service.CommonCodeService;
import renewal.awesome_travel_backoffice.hotel.repository.HotelRepository;
import renewal.awesome_travel_backoffice.product.repository.ProductAdminRepository;
import renewal.awesome_travel_backoffice.tour.TourService;
import renewal.awesome_travel_backoffice.tour.dto.TourFilterDTO;
import renewal.awesome_travel_backoffice.tour.repository.TourRepository;
import renewal.common.entity.Hotel;
import renewal.common.entity.Location;
import renewal.common.entity.Location.LocationType;
import renewal.common.entity.Product;
import renewal.common.entity.Schedule;
import renewal.common.entity.Tour;

@RequiredArgsConstructor
@RequestMapping("/tour")
@Controller
public class TourController {

    private final TourRepository tourRepo;
    private final TourService tourService;
    private final ProductAdminRepository productAdminRepo;
    private final HotelRepository hotelRepo;

    private final CommonCodeService commonCodeService;

    // 투어 목록
    // 필터 폼과 결과 리스트(또는 전체 리스트)를 동일하게 렌더링
    @GetMapping
    public String listAndFilter(
            @ModelAttribute TourFilterDTO filter, // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            @RequestParam(defaultValue = "startDate") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        // 1) 정렬 객체 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // 1) 회사 목록 (체크박스용)
        List<String> allCompanies = tourService.getAllCompanies();
        model.addAttribute("allCompanies", allCompanies);

        // 2) 페이징(50개 고정) + 필터링 로직
        Pageable pageable = PageRequest.of(page, 50, sort);
        Page<Tour> tourPage = tourService.searchTours(filter, pageable);

        // 3) View에서 쓸 속성들
        model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
        model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
        model.addAttribute("tourPage", tourPage);
        model.addAttribute("filter", filter); // 필터 객체 추가
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("isSelectionPage", false); // 일반 목록 페이지임을 명시
        model.addAttribute("title", "Tour List");
        model.addAttribute("content", "components/tour/tour"); // layout 안에서 이 fragment를 렌더

        return "layout";
    }

    // 새 투어
    @GetMapping("/new")
    public String newTravel(Model model) {

        // 역순으로 구조 생성
        // 3. Location
        Location blankLocation = new Location();
        blankLocation.setLocationType(LocationType.POINT);
        blankLocation.setDescription("");

        // 2. Schedule
        Schedule blankSchedule = new Schedule();
        blankSchedule.getLocations().add(blankLocation);

        // 1. Tour
        Tour blankTour = new Tour();
        blankTour.setName("");
        blankTour.setCompany("");
        blankTour.setCountry(null);
        blankTour.getSchedules().add(blankSchedule);

        model.addAttribute("types", LocationType.values());
        model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
        model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
        model.addAttribute("airportCode", commonCodeService.getAllAirports());
        model.addAttribute("tour", blankTour);
        model.addAttribute("connectedProduct", new Product());
        model.addAttribute("isSelectionPage", false);
        model.addAttribute("title", "New Tour");
        model.addAttribute("content", "components/tour/tourDetail");

        return "layout";
    }

    // 새 투어 등록
    @PostMapping("/new")
    public String submitTravel(@ModelAttribute Tour tour) throws Exception {
        // 모든 Schedule 객체에 tour 참조를 세팅
        for (Schedule schedule : tour.getSchedules()) {
            schedule.setTour(tour);
            // 모든 location 객체에 schedule 참조를 세팅
            for (Location location : schedule.getLocations()) {
                location.setSchedule(schedule);
            }
        }

        tourRepo.save(processTour(tour));

        return "redirect:/tour";
    }

    // 특정 투어
    @GetMapping("/{id}")
    public String selectTravel(@PathVariable Long id, Model model) {

        Tour tour = tourRepo.findById(id).get();
        Product connectedProduct = productAdminRepo.findByTourId(id);
        model.addAttribute("types", LocationType.class);
        model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
        model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
        model.addAttribute("airportCode", commonCodeService.getAllAirports());
        model.addAttribute("tour", tour);
        model.addAttribute("connectedProduct", connectedProduct == null ? 0 : connectedProduct.getId());
        model.addAttribute("isSelectionPage", false);
        model.addAttribute("title", "Tour " + tour.getName());
        model.addAttribute("content", "components/tour/tourDetail");

        return "layout";
    }

    // 특정 투어 수정
    @PostMapping("/{id}")
    public String submitSelectedTravel(@ModelAttribute Tour tour) throws Exception {

        // 모든 Schedule 객체에 tour 참조를 세팅
        for (Schedule schedule : tour.getSchedules()) {
            schedule.setTour(tour);
            // 모든 location 객체에 schedule 참조를 세팅
            for (Location location : schedule.getLocations()) {
                location.setSchedule(schedule);
            }
        }

        tourRepo.save(processTour(tour));

        return "redirect:/tour";
    }

    // 투어 삭제 처리
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTour(@PathVariable Long id) {

        // 연결된 Product 있는지 확인
        if (productAdminRepo.findByTourId(id) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("연결된 패키지를 먼저 삭제해주세요.");
        }

        try {
            // 투어 삭제처리
            tourRepo.deleteById(id);
            return ResponseEntity.ok("삭제 완료");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("항공/호텔 취소중 오류가 발생했습니다. \n" + e);
        }

    }

    // 투어 검색용
    @GetMapping("/search")
    public String searchTour(
            @ModelAttribute TourFilterDTO filter, // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            @RequestParam(defaultValue = "startDate") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, 50, sort);

        // Product 선택용이므로 연결된 Tour 제외
        Page<Tour> tourPage = tourService.searchTours(filter, pageable, true);

        // View에서 쓸 속성들
        model.addAttribute("tourPage", tourPage);
        model.addAttribute("filter", filter); // 필터 객체 추가
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Tour Select");

        // 투어선택 플래그
        model.addAttribute("isSelectionPage", true);
        model.addAttribute("content", "components/tour/tour"); // layout 안에서 이 fragment를 렌더

        return "popup";
    }

    // 투어 공용함수 분리
    protected Tour processTour(Tour tour) throws Exception {
        Long hotelPriceSum = 0L;

        // 새 키워드 목록 생성
        Set<String> keywords = new HashSet<String>();

        // Schedules 순회
        for (Schedule schedule : tour.getSchedules()) {
            // Locations 순회
            for (Location location : schedule.getLocations()) {
                // 키워드 목록에 이름 추가
                // keywords.add(location.getName());

                if (location.getLocationType() == LocationType.AIR) {
                    // AIR 처리
                } else if (location.getLocationType() == LocationType.HOTEL) {
                    // 호텔 가격 합산
                    Hotel hotel = hotelRepo.findById(location.getHotel().getId()).get();
                    keywords.add(location.getName());
                    if (hotel != null) {
                        hotelPriceSum += hotel.getPrice();
                    }
                } else {
                    // POINT 처리
                    keywords.add(location.getName());
                }
            }
        }

        // 처리 결과 저장
        tour.setHotelPriceSum(hotelPriceSum);
        tour.setKeywords(keywords);

        return tour;
    }
}
