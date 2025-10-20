package renewal.awesome_travel_backoffice.hotel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import renewal.awesome_travel_backoffice.hotel.dto.HotelFilterDTO;
import renewal.awesome_travel_backoffice.hotel.repository.AmenityRepository;
import renewal.awesome_travel_backoffice.hotel.repository.HotelRepository;
// import renewal.awesome_travel_backoffice.hotel.repository.HotelReservationRepository;
import renewal.awesome_travel_backoffice.hotel.service.HotelService;
import renewal.common.entity.Hotel;
// import renewal.common.entity.HotelReservation;
import renewal.common.entity.Hotel.HotelType;
import renewal.common.repository.CityCodeRepository;

@RequiredArgsConstructor
@RequestMapping("/hotel")
@Controller
public class HotelController {

    private final HotelService hotelService;
    private final HotelRepository hotelRepo;
    // private final HotelReservationRepository hotelReservationRepo;
    private final AmenityRepository amenityRepo;
    private final CityCodeRepository cityRepo;

    // 호텔 목록 + 필터 + 페이징
    @GetMapping
    public String listAndFilter(
            @ModelAttribute HotelFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, 50, sort);
        Page<Hotel> hotelPage = hotelService.searchHotels(filter, pageable);

        // 도시코드
        model.addAttribute("cityCode", cityRepo.findAll());

        model.addAttribute("hotelPage", hotelPage);
        model.addAttribute("filter", filter); // 필터 객체 추가
        // model.addAttribute("hotelList", hotelPage.getContent());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("isSelectionPage", false); // 기본값 설정
        model.addAttribute("title", "Hotel List");
        model.addAttribute("content", "components/hotel/hotel");

        return "layout";
    }

    // 새 호텔 등록 화면
    @GetMapping("/new")
    public String newHotel(Model model) {
        Hotel hotel = new Hotel();
        
        // 도시코드
        model.addAttribute("cityCode", cityRepo.findAll());
        
        model.addAttribute("hotel", hotel);
        model.addAttribute("hotelTypes", HotelType.values());
        model.addAttribute("allAmenities", amenityRepo.findAll());
        model.addAttribute("title", "New Hotel");
        model.addAttribute("content", "components/hotel/hotelDetail");
        return "layout";
    }

    // 새 호텔 등록 처리
    @PostMapping("/new")
    public String submitNewHotel(@ModelAttribute Hotel hotel) {
        hotelRepo.save(hotel);
        return "redirect:/hotel";
    }

    // 호텔 상세 조회
    @GetMapping("/{id}")
    public String selectHotel(@PathVariable Long id, Model model) {
        Hotel hotel = hotelRepo.getReferenceById(id);

        // 도시코드
        model.addAttribute("cityCode", cityRepo.findAll());
        
        model.addAttribute("hotel", hotel);
        model.addAttribute("hotelTypes", HotelType.values());
        model.addAttribute("allAmenities", amenityRepo.findAll());
        model.addAttribute("title", "Hotel Detail");
        model.addAttribute("content", "components/hotel/hotelDetail");
        return "layout";
    }

    // 호텔 수정 처리
    @PostMapping("/{id}")
    public String submitEditedHotel(@ModelAttribute Hotel hotel) {
        hotelRepo.save(hotel);
        return "redirect:/hotel";
    }

    // 호텔 삭제 처리
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHotel(@PathVariable Long id) {

        // // 1. 호텔 ID에 연결된 모든 예약 가져오기
        // List<HotelReservation> hotelReservations = hotelReservationRepo.findByHotelId(id);

        // // 2. 예약 삭제
        // for (HotelReservation hotelReservation : hotelReservations) {
        //     hotelReservationRepo.deleteById(hotelReservation.getId());
        // }
        
        // 3. 호텔 삭제
        hotelRepo.deleteById(id);
        
        return ResponseEntity.ok("삭제 완료");
    }

    // // 특정 호텔 예약 조회
    // @GetMapping("/{id}/hotelReservation")
    // public String selectHotelHotelReservation(@PathVariable("id") Long id, Model model) {
    //     List<HotelReservation> hotelReservations = hotelReservationRepo.findByHotelId(id);
    //     model.addAttribute("hotelReservation", hotelReservations);
    //     model.addAttribute("title", "Hotel ID "+id+" HotelReservation");
    //     model.addAttribute("content", "components/hotelReservation");
    //     return "layout";
    // }
    
    @GetMapping("/search")
    public String searchHotel(
            @ModelAttribute HotelFilterDTO filter, // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, 50, sort);

        Page<Hotel> hotelPage = hotelService.searchHotels(filter, pageable);

        // View에서 쓸 속성들
        model.addAttribute("hotelPage", hotelPage);
        model.addAttribute("filter", filter); // 필터 객체 추가
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Hotel Select");
        model.addAttribute("cityCode", cityRepo.findAll());
        
        // 호텔선택 플래그
        model.addAttribute("isSelectionPage", true);
        model.addAttribute("content", "components/hotel/hotel"); // layout 안에서 이 fragment를 렌더

        return "popup";
    }
}
