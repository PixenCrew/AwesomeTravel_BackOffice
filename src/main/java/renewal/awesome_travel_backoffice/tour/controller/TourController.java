package renewal.awesome_travel_backoffice.tour.controller;

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

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.tour.TourService;
import renewal.awesome_travel_backoffice.tour.dto.TourFilterDTO;
import renewal.awesome_travel_backoffice.tour.entity.Point;
import renewal.awesome_travel_backoffice.tour.entity.Tour;
import renewal.awesome_travel_backoffice.tour.repository.TourRepository;

@RequiredArgsConstructor
@RequestMapping("/tour")
@Controller
public class TourController {

    private final TourRepository tourRepo;
    private final TourService tourService;

    // 투어 목록
    // 필터 폼과 결과 리스트(또는 전체 리스트)를 동일하게 렌더링
    @GetMapping
    public String listAndFilter(
            @ModelAttribute("filter") TourFilterDTO filter,     // 필터 DTO를 바인딩
            @RequestParam(defaultValue = "0") int page,          // 페이지 번호
            @RequestParam(defaultValue = "startdate") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model
    ) {
        // 1) 정렬 객체 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
        ? Sort.by(sortField).ascending()
        : Sort.by(sortField).descending();

        // 1) 회사 목록 (체크박스용)
        List<String> allCompanies = tourService.getAllCompanies();
        model.addAttribute("allCompanies", allCompanies);

        // 2) 페이징(10개 고정) + 필터링 로직
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<Tour> tourPage = tourService.searchTours(filter, pageable);

        // 3) View에서 쓸 속성들
        model.addAttribute("tourPage", tourPage);
        model.addAttribute("tourList", tourPage.getContent());  
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "Tour List");
        model.addAttribute("content", "components/tour");  // layout 안에서 이 fragment를 렌더

        return "layout";
    }

    // @GetMapping("/search")
    // public Page<Tour> searchTours(
    //     TourFilterDTO filter,
    //     @RequestParam(defaultValue = "0") int page,
    //     @RequestParam(defaultValue = "10") int size
    // ) {
    //     Pageable pageable = PageRequest.of(page, size);
    //     return tourService.searchTours(filter, pageable);
    // }

    // 새 투어
    @GetMapping("/new")
    public String newTravel(Model model) {

        Tour blank = new Tour();
        Point defaultPoint = new Point();
        defaultPoint.setLocation("");
        defaultPoint.setEnddate(null);
        blank.getCourse().add(defaultPoint);

        model.addAttribute("tour", blank);
        model.addAttribute("title", "New Tour");
        model.addAttribute("content", "components/tourDetail");

        return "layout";
    }

    // 새 투어 등록
    @PostMapping("/new")
    public String submitTravel(@ModelAttribute Tour tour) {
        // 모든 Point 객체에 tour 참조를 세팅
        tour.getCourse().forEach(point -> point.setTour(tour));

        tourRepo.save(tour);

        return "redirect:/tour";
    }

    // 특정 투어
    @GetMapping("/{id}")
    public String selectTravel(@PathVariable("id") Long id, Model model) {

        Tour tour = tourRepo.getReferenceById(id);
        model.addAttribute("tour", tour);
        model.addAttribute("title", "Tour " + tour.getName());
        model.addAttribute("content", "components/tourDetail");

        return "layout";
    }

    // 특정 투어 수정
    @PostMapping("/{id}")
    public String submitSelectedTravel(@ModelAttribute Tour tour) {
        // 모든 Point 객체에 tour 참조를 세팅
        tour.getCourse().forEach(point -> point.setTour(tour));
        tourRepo.save(tour);

        return "redirect:/tour";
    }

    // 특정 투어 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSelectedTravel(@PathVariable Long id) {

        tourRepo.deleteById(id);

        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }
}
