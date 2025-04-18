package renewal.awesome_travel_backoffice.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.entity.Tour;
import renewal.awesome_travel_backoffice.repo.TourRepo;

@RequiredArgsConstructor
@RequestMapping("/tour")
@Controller
public class TourController {

    private final TourRepo tourRepo; 

    // 투어 목록
    @GetMapping
    public String productIndex(Model model) {

        List<Tour> list = tourRepo.findAll();
        model.addAttribute("tourList", list);
        model.addAttribute("title", "Tour List");
        model.addAttribute("content", "components/tour");

        return "layout";
    }

    // 새 투어
    @GetMapping("/new")
    public String newTravel(Model model) {

        Tour blank = new Tour();
        model.addAttribute("tour", blank);
        model.addAttribute("title", "New Tour");
        model.addAttribute("content", "components/tourDetail");

        return "layout";
    }
    // 새 투어 등록
    @PostMapping("/new")
    public String submitTravel(@ModelAttribute Tour tour) {

        tourRepo.save(tour);

        return "redirect:/tour";
    }
    
    // 특정 투어
    @GetMapping("/{id}")
    public String selectTravel(@PathVariable Long id, Model model) {

        Tour tour = tourRepo.getReferenceById(id);
        model.addAttribute("tour", tour);
        model.addAttribute("title", "Tour "+tour.getName());
        model.addAttribute("content", "components/tourDetail");

        return "layout";
    }
    // 특정 투어 수정
    @PostMapping("/{id}")
    public String submitSelectedTravel(@ModelAttribute Tour tour) {

        tourRepo.save(tour);

        return "redirect:/tour";
    }   
    // 특정 투어 삭제
    @DeleteMapping("/{id}")
    public String deleteSelectedTravel(@ModelAttribute Tour tour) {

        tourRepo.delete(tour);

        return "redirect:/tour";
    }
}
