package renewal.awesome_travel_backoffice.tour.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import renewal.awesome_travel_backoffice.air.entity.AirReservation;
import renewal.awesome_travel_backoffice.air.entity.SeatClass;
import renewal.awesome_travel_backoffice.air.repository.AirReservationRepository;
import renewal.awesome_travel_backoffice.code.CityCodeRepository;
import renewal.awesome_travel_backoffice.code.CountryCodeRepository;
import renewal.awesome_travel_backoffice.hotel.entity.Hotel;
import renewal.awesome_travel_backoffice.hotel.entity.HotelReservation;
import renewal.awesome_travel_backoffice.hotel.repository.HotelRepository;
import renewal.awesome_travel_backoffice.hotel.repository.HotelReservationRepository;
import renewal.awesome_travel_backoffice.product.repository.ProductRepository;
import renewal.awesome_travel_backoffice.tour.TourService;
import renewal.awesome_travel_backoffice.tour.dto.TourFilterDTO;
import renewal.awesome_travel_backoffice.tour.entity.Location;
import renewal.awesome_travel_backoffice.tour.entity.Location.Type;
import renewal.awesome_travel_backoffice.tour.entity.Schedule;
import renewal.awesome_travel_backoffice.tour.entity.Tour;
import renewal.awesome_travel_backoffice.tour.repository.TourRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/tour")
@Controller
public class TourController {

    private final TourRepository tourRepo;
    private final TourService tourService;
    private final HotelRepository hotelRepo;
    private final HotelReservationRepository hotelReservationRepo;
    private final AirReservationRepository airReservationRepo;
    private final ProductRepository productRepo;

    private final CountryCodeRepository countryRepo;
    private final CityCodeRepository cityRepo;

    // 투어 목록
    // 필터 폼과 결과 리스트(또는 전체 리스트)를 동일하게 렌더링
    @GetMapping
    public String listAndFilter(
            @ModelAttribute("filter") TourFilterDTO filter, // 필터 DTO를 바인딩
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
        model.addAttribute("countryCode", countryRepo.findAll());
        model.addAttribute("cityCode", cityRepo.findAll());
        model.addAttribute("tourPage", tourPage);
        // model.addAttribute("tourList", tourPage.getContent());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Tour List");
        model.addAttribute("content", "components/tour"); // layout 안에서 이 fragment를 렌더

        return "layout";
    }

    // @GetMapping("/search")
    // public Page<Tour> searchTours(
    // TourFilterDTO filter,
    // @RequestParam(defaultValue = "0") int page,
    // @RequestParam(defaultValue = "10") int size
    // ) {
    // Pageable pageable = PageRequest.of(page, size);
    // return tourService.searchTours(filter, pageable);
    // }

    // 새 투어
    @GetMapping("/new")
    public String newTravel(Model model) {

        // 역순으로 구조 생성
        // 3. Location
        Location blankLocation = new Location();
        blankLocation.setLocationType(Type.POINT);

        // 2. Schedule
        Schedule blankSchedule = new Schedule();
        blankSchedule.getLocations().add(blankLocation);

        // 1. Tour
        Tour blankTour = new Tour();
        blankTour.getSchedules().add(blankSchedule);

        // defaultLocation.setCity(null);
        // defaultLocation.setDate(null);
        // defaultLocation.setType(Type.POINT);
        // blank.getLocations().add(defaultLocation);

        model.addAttribute("types", Type.values());
        model.addAttribute("countryCode", countryRepo.findAll());
        model.addAttribute("cityCode", cityRepo.findAll());
        model.addAttribute("tour", blankTour);
        model.addAttribute("title", "New Tour");
        model.addAttribute("content", "components/tourDetail");

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
        ;

        // tour.id 생성을 위한 1차 저장
        tourRepo.save(tour);

        tourRepo.save(setTour(tour));

        return "redirect:/tour";
    }

    // 특정 투어
    @GetMapping("/{id}")
    public String selectTravel(@PathVariable("id") Long id, Model model) {

        Tour tour = tourRepo.getReferenceById(id);
        model.addAttribute("types", Type.class);
        model.addAttribute("countryCode", countryRepo.findAll());
        model.addAttribute("cityCode", cityRepo.findAll());
        model.addAttribute("tour", tour);
        model.addAttribute("title", "Tour " + tour.getName());
        model.addAttribute("content", "components/tourDetail");

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
        ;

        tourRepo.save(setTour(tour));

        return "redirect:/tour";
    }

    // 투어 삭제 처리
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTour(@PathVariable Long id) {

        // 연결된 Product 있는지 확인
        if (!productRepo.findByTourId(id).isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("연결된 패키지를 먼저 삭제해주세요.");
        }

        // 연결된 Air, Hotel 예약 CANCELED로 변경
        try {

            // 호텔 취소, 항공 취소 및 잔여좌석 복원
            tourService.cancelHotelAir(id);

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
            @ModelAttribute("filter") TourFilterDTO filter, // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, 50, sort);

        Page<Tour> tourPage = tourService.searchTours(filter, pageable);

        // View에서 쓸 속성들
        model.addAttribute("tourPage", tourPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Tour Select");

        // 투어선택 플래그
        model.addAttribute("isSelectionPage", true);

        return "components/tour";
    }

    protected Tour setTour(Tour tour) throws Exception {
        Long requiredPersons = tour.getCount(); // 인원수
        Long hotelId = null; // 호텔
        LocalDate startDate = null;
        LocalDate endDate = null;

        Long airPriceSum = 0L;
        Long hotelPriceSum = 0L;

        // 기존 항공권, 호텔 예약 CANCEL 처리
        tourService.cancelHotelAir(tour.getId());

        // Schedules 순회
        for (Schedule schedule : tour.getSchedules()) {
            LocalDate currentDate = schedule.getDate();

            // Locations 순회
            for (Location location : schedule.getLocations()) {
                if (location.getLocationType() == Type.AIR) {
                    SeatClass sc = location.getSeatClass();
                    airPriceSum += sc.getPrice();
                    sc.reserveSeats(requiredPersons);
                    location.setLocationType(Type.AIR);
                    airReservationRepo
                            .save(new AirReservation(sc, tour.getId(), requiredPersons, AirReservation.Status.BOOKED));
                } else if (location.getLocationType() == Type.HOTEL) {
                    location.setLocationType(Type.HOTEL);
                    Long currentHotelId = location.getHotel().getId();
                    hotelPriceSum += location.getHotel().getPrice();
                    if (hotelId == null) {
                        hotelId = currentHotelId;
                        startDate = currentDate;
                    }
                    if (!hotelId.equals(currentHotelId)) { // id 다르면
                        // 전 호텔 끝
                        Hotel hotel = hotelRepo.findById(hotelId).get();
                        hotelReservationRepo.save(new HotelReservation(hotel, tour.getId(), requiredPersons, startDate,
                                endDate, HotelReservation.Status.BOOKED));
                        // 현 호텔 시작
                        hotelId = currentHotelId;
                        startDate = currentDate;
                    }
                } else {
                    location.setLocationType(Type.POINT);

                }
            }
            endDate = currentDate;
        }
        // 마지막 hotel 등록
        if (hotelId != null) {
            Hotel hotel = hotelRepo.findById(hotelId).get();
            hotelReservationRepo.save(new HotelReservation(hotel, tour.getId(), requiredPersons, startDate, endDate,
                    HotelReservation.Status.BOOKED));
        }

        tour.setAirPriceSum(airPriceSum * tour.getCount()); // 가격 총합 x 인원수
        tour.setHotelPriceSum(hotelPriceSum * tour.getCount());

        return tour;
    }
}
