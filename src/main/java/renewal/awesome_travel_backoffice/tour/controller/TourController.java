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

import org.hibernate.Hibernate;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.air.dto.GenerateAirForTourRequest;
import renewal.awesome_travel_backoffice.air.service.AirService;
import renewal.awesome_travel_backoffice.common.service.CommonCodeService;
import renewal.awesome_travel_backoffice.hotel.repository.HotelRepository;
import renewal.awesome_travel_backoffice.product.repository.ProductAdminRepository;
import renewal.awesome_travel_backoffice.tour.TourService;
import renewal.awesome_travel_backoffice.tour.dto.TourFilterDTO;
import renewal.awesome_travel_backoffice.tour.repository.TourRepository;
import renewal.awesome_travel_backoffice.airport.repository.AirportCodeRepository;
import renewal.common.entity.AirportCode;
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
    private final AirService airService;
    private final AirportCodeRepository airportCodeRepo;

    private final CommonCodeService commonCodeService;

    // 투어 목록
    // 필터 폼과 결과 리스트(또는 전체 리스트)를 동일하게 렌더링
    @GetMapping
    public String listAndFilter(
            @ModelAttribute TourFilterDTO filter, // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            @RequestParam(defaultValue = "id") String sortField,
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
        model.addAttribute("title", "투어 관리");
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
        model.addAttribute("title", "새 투어 등록");
        model.addAttribute("content", "components/tour/tourDetail");

        return "layout";
    }

    // 새 투어 등록
    @PostMapping("/new")
    public String submitTravel(@ModelAttribute Tour tour, Model model) throws Exception {
        final long MAX_PRICE = 100000000L; // 1억원
        
        // 최대인원이 1000을 넘지 않도록 검증
        if (tour.getMaxCapacity() != null && tour.getMaxCapacity() > 1000) {
            model.addAttribute("types", LocationType.values());
            model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
            model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
            model.addAttribute("airportCode", commonCodeService.getAllAirports());
            model.addAttribute("tour", tour);
            model.addAttribute("connectedProduct", new Product());
            model.addAttribute("isSelectionPage", false);
            model.addAttribute("title", "새 투어 등록");
            model.addAttribute("content", "components/tour/tourDetail");
            model.addAttribute("error", String.format("최대인원(%d)은 1000을 넘을 수 없습니다.", tour.getMaxCapacity()));
            return "layout";
        }
        
        // 최소인원이 최대인원을 넘지 않도록 검증
        if (tour.getMinCapacity() != null && tour.getMaxCapacity() != null) {
            if (tour.getMinCapacity() > tour.getMaxCapacity()) {
                model.addAttribute("types", LocationType.values());
                model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
                model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
                model.addAttribute("airportCode", commonCodeService.getAllAirports());
                model.addAttribute("tour", tour);
                model.addAttribute("connectedProduct", new Product());
                model.addAttribute("isSelectionPage", false);
                model.addAttribute("title", "새 투어 등록");
                model.addAttribute("content", "components/tour/tourDetail");
                model.addAttribute("error", String.format("최소인원(%d)은 최대인원(%d)을 넘을 수 없습니다.", 
                    tour.getMinCapacity(), tour.getMaxCapacity()));
                return "layout";
            }
        }
        
        // 가격 1억원 제한 검증
        if (tour.getPriceAdult() != null && tour.getPriceAdult() > MAX_PRICE) {
            model.addAttribute("types", LocationType.values());
            model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
            model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
            model.addAttribute("airportCode", commonCodeService.getAllAirports());
            model.addAttribute("tour", tour);
            model.addAttribute("connectedProduct", new Product());
            model.addAttribute("isSelectionPage", false);
            model.addAttribute("title", "새 투어 등록");
            model.addAttribute("content", "components/tour/tourDetail");
            model.addAttribute("error", String.format("가격[성인](%d원)은 1억원을 넘을 수 없습니다.", tour.getPriceAdult()));
            return "layout";
        }
        if (tour.getPriceYouth() != null && tour.getPriceYouth() > MAX_PRICE) {
            model.addAttribute("types", LocationType.values());
            model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
            model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
            model.addAttribute("airportCode", commonCodeService.getAllAirports());
            model.addAttribute("tour", tour);
            model.addAttribute("connectedProduct", new Product());
            model.addAttribute("isSelectionPage", false);
            model.addAttribute("title", "새 투어 등록");
            model.addAttribute("content", "components/tour/tourDetail");
            model.addAttribute("error", String.format("가격[청소년](%d원)은 1억원을 넘을 수 없습니다.", tour.getPriceYouth()));
            return "layout";
        }
        if (tour.getPriceInfant() != null && tour.getPriceInfant() > MAX_PRICE) {
            model.addAttribute("types", LocationType.values());
            model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
            model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
            model.addAttribute("airportCode", commonCodeService.getAllAirports());
            model.addAttribute("tour", tour);
            model.addAttribute("connectedProduct", new Product());
            model.addAttribute("isSelectionPage", false);
            model.addAttribute("title", "새 투어 등록");
            model.addAttribute("content", "components/tour/tourDetail");
            model.addAttribute("error", String.format("가격[영유아](%d원)은 1억원을 넘을 수 없습니다.", tour.getPriceInfant()));
            return "layout";
        }

        // AIR 타입 Location의 출발/도착 공항을 폼에서 넘어온 코드로 DB 엔티티로 조회해 세팅 (FK 오류 방지)
        resolveAirportCodesForTour(tour);
        
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

        Tour tour = tourRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("투어를 찾을 수 없습니다. id=" + id));
        Product connectedProduct = productAdminRepo.findByTourId(id);
        model.addAttribute("types", LocationType.class);
        model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
        model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
        model.addAttribute("airportCode", commonCodeService.getAllAirports());
        model.addAttribute("tour", tour);
        model.addAttribute("connectedProduct", connectedProduct == null ? 0 : connectedProduct.getId());
        model.addAttribute("isSelectionPage", false);
        model.addAttribute("title", "투어 상세");
        model.addAttribute("content", "components/tour/tourDetail");
        model.addAttribute("allAirlines", commonCodeService.getAllAirlines());

        return "layout";
    }

    /**
     * 투어의 AIR 구간·운영 기간에 맞는 항공편을 일괄 생성합니다.
     * 투어 상세 페이지에서 "조건에 맞는 항공 생성"으로 호출합니다.
     */
    @PostMapping("/{id}/generate-air")
    public String generateAirForTour(
            @PathVariable Long id,
            @RequestParam(required = false) List<String> airlineCodes,
            @RequestParam(required = false) Integer departHour,
            @RequestParam(required = false) Integer departMinute,
            @RequestParam(required = false) Long flightDurationMinutes,
            @RequestParam(required = false) Long defaultPriceAdult,
            @RequestParam(required = false) Long defaultMaxSeats,
            @RequestParam(required = false) Long priceEconomy,
            @RequestParam(required = false) Long pricePremiumEconomy,
            @RequestParam(required = false) Long priceBusiness,
            @RequestParam(required = false) Long priceFirst,
            @RequestParam(required = false) Long maxSeatsEconomy,
            @RequestParam(required = false) Long maxSeatsPremiumEconomy,
            @RequestParam(required = false) Long maxSeatsBusiness,
            @RequestParam(required = false) Long maxSeatsFirst,
            Model model,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            Tour tour = tourRepo.findById(id).orElse(null);
            if (tour == null) {
                redirectAttributes.addFlashAttribute("generateAirError", "투어를 찾을 수 없습니다.");
                return "redirect:/tour";
            }
            Hibernate.initialize(tour.getSchedules());
            if (tour.getSchedules() != null) {
                for (Schedule schedule : tour.getSchedules()) {
                    Hibernate.initialize(schedule.getLocations());
                    if (schedule.getLocations() != null) {
                        for (Location loc : schedule.getLocations()) {
                            if (loc != null && loc.getLocationType() == LocationType.AIR) {
                                if (loc.getDepartAirport() != null) Hibernate.initialize(loc.getDepartAirport());
                                if (loc.getArriveAirport() != null) Hibernate.initialize(loc.getArriveAirport());
                            }
                        }
                    }
                }
            }
            GenerateAirForTourRequest request = GenerateAirForTourRequest.builder()
                    .airlineCodes(airlineCodes)
                    .departHour(departHour != null ? departHour : 9)
                    .departMinute(departMinute != null ? departMinute : 0)
                    .flightDurationMinutes(flightDurationMinutes != null ? flightDurationMinutes : 720L)
                    .defaultPriceAdult(defaultPriceAdult != null ? defaultPriceAdult : 500_000L)
                    .defaultMaxSeats(defaultMaxSeats != null ? defaultMaxSeats : 30L)
                    .priceEconomy(priceEconomy)
                    .pricePremiumEconomy(pricePremiumEconomy)
                    .priceBusiness(priceBusiness)
                    .priceFirst(priceFirst)
                    .maxSeatsEconomy(maxSeatsEconomy)
                    .maxSeatsPremiumEconomy(maxSeatsPremiumEconomy)
                    .maxSeatsBusiness(maxSeatsBusiness)
                    .maxSeatsFirst(maxSeatsFirst)
                    .build();
            java.util.List<renewal.common.entity.Air> created = airService.generateAirForTour(tour, request);
            redirectAttributes.addFlashAttribute("generateAirMessage",
                    created.size() + "건의 항공편이 생성되었습니다.");
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(TourController.class)
                    .warn("generate-air failed for tour id={}", id, e);
            String message = e.getMessage() != null ? e.getMessage() : "항공 생성 중 오류가 발생했습니다.";
            redirectAttributes.addFlashAttribute("generateAirError", message);
        }
        return "redirect:/tour/" + id;
    }

    // 특정 투어 수정
    @PostMapping("/{id}")
    public String submitSelectedTravel(@ModelAttribute Tour tour, Model model) throws Exception {
        final long MAX_PRICE = 100000000L; // 1억원
        
        // 최대인원이 1000을 넘지 않도록 검증
        if (tour.getMaxCapacity() != null && tour.getMaxCapacity() > 1000) {
            Product connectedProduct = productAdminRepo.findByTourId(tour.getId());
            model.addAttribute("types", LocationType.class);
            model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
            model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
            model.addAttribute("airportCode", commonCodeService.getAllAirports());
            model.addAttribute("tour", tour);
            model.addAttribute("connectedProduct", connectedProduct == null ? 0 : connectedProduct.getId());
            model.addAttribute("isSelectionPage", false);
            model.addAttribute("title", "투어 상세");
            model.addAttribute("content", "components/tour/tourDetail");
            model.addAttribute("error", String.format("최대인원(%d)은 1000을 넘을 수 없습니다.", tour.getMaxCapacity()));
            return "layout";
        }
        
        // 최소인원이 최대인원을 넘지 않도록 검증
        if (tour.getMinCapacity() != null && tour.getMaxCapacity() != null) {
            if (tour.getMinCapacity() > tour.getMaxCapacity()) {
                Product connectedProduct = productAdminRepo.findByTourId(tour.getId());
                model.addAttribute("types", LocationType.class);
                model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
                model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
                model.addAttribute("airportCode", commonCodeService.getAllAirports());
                model.addAttribute("tour", tour);
                model.addAttribute("connectedProduct", connectedProduct == null ? 0 : connectedProduct.getId());
                model.addAttribute("isSelectionPage", false);
                model.addAttribute("title", "투어 상세");
                model.addAttribute("content", "components/tour/tourDetail");
                model.addAttribute("error", String.format("최소인원(%d)은 최대인원(%d)을 넘을 수 없습니다.", 
                    tour.getMinCapacity(), tour.getMaxCapacity()));
                return "layout";
            }
        }
        
        // 가격 1억원 제한 검증
        if (tour.getPriceAdult() != null && tour.getPriceAdult() > MAX_PRICE) {
            Product connectedProduct = productAdminRepo.findByTourId(tour.getId());
            model.addAttribute("types", LocationType.class);
            model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
            model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
            model.addAttribute("airportCode", commonCodeService.getAllAirports());
            model.addAttribute("tour", tour);
            model.addAttribute("connectedProduct", connectedProduct == null ? 0 : connectedProduct.getId());
            model.addAttribute("isSelectionPage", false);
            model.addAttribute("title", "투어 상세");
            model.addAttribute("content", "components/tour/tourDetail");
            model.addAttribute("error", String.format("가격[성인](%d원)은 1억원을 넘을 수 없습니다.", tour.getPriceAdult()));
            return "layout";
        }
        if (tour.getPriceYouth() != null && tour.getPriceYouth() > MAX_PRICE) {
            Product connectedProduct = productAdminRepo.findByTourId(tour.getId());
            model.addAttribute("types", LocationType.class);
            model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
            model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
            model.addAttribute("airportCode", commonCodeService.getAllAirports());
            model.addAttribute("tour", tour);
            model.addAttribute("connectedProduct", connectedProduct == null ? 0 : connectedProduct.getId());
            model.addAttribute("isSelectionPage", false);
            model.addAttribute("title", "투어 상세");
            model.addAttribute("content", "components/tour/tourDetail");
            model.addAttribute("error", String.format("가격[청소년](%d원)은 1억원을 넘을 수 없습니다.", tour.getPriceYouth()));
            return "layout";
        }
        if (tour.getPriceInfant() != null && tour.getPriceInfant() > MAX_PRICE) {
            Product connectedProduct = productAdminRepo.findByTourId(tour.getId());
            model.addAttribute("types", LocationType.class);
            model.addAttribute("countryCode", commonCodeService.getAllCountryCodes());
            model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
            model.addAttribute("airportCode", commonCodeService.getAllAirports());
            model.addAttribute("tour", tour);
            model.addAttribute("connectedProduct", connectedProduct == null ? 0 : connectedProduct.getId());
            model.addAttribute("isSelectionPage", false);
            model.addAttribute("title", "투어 상세");
            model.addAttribute("content", "components/tour/tourDetail");
            model.addAttribute("error", String.format("가격[영유아](%d원)은 1억원을 넘을 수 없습니다.", tour.getPriceInfant()));
            return "layout";
        }

        // AIR 타입 Location의 출발/도착 공항을 폼에서 넘어온 코드로 DB 엔티티로 조회해 세팅 (FK 오류 방지)
        resolveAirportCodesForTour(tour);

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

    /** AIR 타입 Location의 departAirport, arriveAirport를 폼에서 바인딩된 코드 문자열로 DB의 AirportCode 엔티티로 교체 (FK 오류 방지) */
    private void resolveAirportCodesForTour(Tour tour) {
        if (tour.getSchedules() == null) return;
        for (Schedule schedule : tour.getSchedules()) {
            if (schedule.getLocations() == null) continue;
            for (Location location : schedule.getLocations()) {
                if (location == null || location.getLocationType() != LocationType.AIR) continue;
                String depCode = location.getDepartAirport() != null ? location.getDepartAirport().getAirportCode() : null;
                if (depCode != null && !depCode.isBlank()) {
                    depCode = depCode.trim().split("\\|")[0].trim(); // "ICN | 인천" 형태 대비
                    location.setDepartAirport(airportCodeRepo.findByCode(depCode).orElse(null));
                } else {
                    location.setDepartAirport(null);
                }
                String arrCode = location.getArriveAirport() != null ? location.getArriveAirport().getAirportCode() : null;
                if (arrCode != null && !arrCode.isBlank()) {
                    arrCode = arrCode.trim().split("\\|")[0].trim();
                    location.setArriveAirport(airportCodeRepo.findByCode(arrCode).orElse(null));
                } else {
                    location.setArriveAirport(null);
                }
            }
        }
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
            @RequestParam(defaultValue = "id") String sortField,
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
        model.addAttribute("title", "투어 선택");

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
                    keywords.add(location.getName());
                    if (location.getHotel() != null && location.getHotel().getId() != null) {
                        Hotel hotel = hotelRepo.findById(location.getHotel().getId()).orElse(null);
                        if (hotel != null) {
                            hotelPriceSum += hotel.getPrice();
                        }
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
