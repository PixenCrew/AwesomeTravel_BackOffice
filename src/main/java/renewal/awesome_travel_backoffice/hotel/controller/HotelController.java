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
import renewal.awesome_travel_backoffice.common.service.CommonCodeService;
import renewal.awesome_travel_backoffice.hotel.dto.HotelFilterDTO;
import renewal.awesome_travel_backoffice.hotel.repository.AmenityRepository;
import renewal.awesome_travel_backoffice.hotel.repository.HotelRepository;
// import renewal.awesome_travel_backoffice.hotel.repository.HotelReservationRepository;
import renewal.awesome_travel_backoffice.hotel.service.HotelService;
import renewal.common.entity.Hotel;
// import renewal.common.entity.HotelReservation;
import renewal.common.entity.Hotel.HotelType;

@RequiredArgsConstructor
@RequestMapping("/hotel")
@Controller
public class HotelController {

    private final HotelService hotelService;
    private final HotelRepository hotelRepo;
    private final AmenityRepository amenityRepo;
    
    private final CommonCodeService commonCodeService;

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
        model.addAttribute("cityCode", commonCodeService.getAllCityCodes());

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
        model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
        
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
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다. ID: " + id));

        // 도시코드
        model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
        
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
        try {
            // 호텔 존재 여부 확인
            if (!hotelRepo.existsById(id)) {
                return ResponseEntity.status(404).body("호텔을 찾을 수 없습니다. ID: " + id);
            }

            // 호텔 삭제
            hotelRepo.deleteById(id);
            
            return ResponseEntity.ok("삭제 완료");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 외래키 제약 조건 위반 시
            return ResponseEntity.status(500).body("삭제할 수 없습니다. 이 호텔은 투어 스케줄(Location)에서 사용 중입니다. 먼저 해당 투어 스케줄에서 호텔 연결을 해제해주세요.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("삭제 실패: " + e.getMessage());
        }
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
        model.addAttribute("cityCode", commonCodeService.getAllCityCodes());
        
        // 호텔선택 플래그
        model.addAttribute("isSelectionPage", true);
        model.addAttribute("content", "components/hotel/hotel"); // layout 안에서 이 fragment를 렌더

        return "popup";
    }
}
