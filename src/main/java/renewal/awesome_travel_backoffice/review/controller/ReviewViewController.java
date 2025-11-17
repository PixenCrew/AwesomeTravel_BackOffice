package renewal.awesome_travel_backoffice.review.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.review.dto.response.ProductReviewGroupDto;
import renewal.awesome_travel_backoffice.review.dto.response.ReviewResponseDto;
import renewal.awesome_travel_backoffice.review.service.ReviewService;

@RequiredArgsConstructor
@RequestMapping("/review")
@Controller
public class ReviewViewController {

    private final ReviewService reviewService;

    @GetMapping
    public String listReviews(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "false") boolean grouped,
            Model model) {
        
        // 날짜 파싱
        java.time.LocalDateTime startDateTime = null;
        java.time.LocalDateTime endDateTime = null;
        if (startDate != null && !startDate.isEmpty()) {
            startDateTime = java.time.LocalDate.parse(startDate).atStartOfDay();
        }
        if (endDate != null && !endDate.isEmpty()) {
            endDateTime = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
        }
        
        // 그룹화 모드인 경우
        if (grouped) {
            List<ProductReviewGroupDto> groupedReviews = 
                reviewService.getReviewsGroupedByProduct(keyword, rating, productId, startDateTime, endDateTime);
            
            model.addAttribute("groupedReviews", groupedReviews);
        } else {
            // 정렬 설정
            Sort sort = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(sortField).ascending()
                    : Sort.by(sortField).descending();
            
            // 페이징 설정
            Pageable pageable = PageRequest.of(page, 20, sort);
            
            // 댓글 검색
            Page<ReviewResponseDto> reviewPage = reviewService.searchAllReviews(
                keyword, rating, productId, startDateTime, endDateTime, pageable);
            
            model.addAttribute("reviewPage", reviewPage);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
        }
        
        // 모델에 데이터 추가
        model.addAttribute("grouped", grouped);
        model.addAttribute("keyword", keyword);
        model.addAttribute("rating", rating);
        model.addAttribute("productId", productId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("title", "댓글 관리");
        model.addAttribute("content", "components/review/review");
        
        return "layout";
    }
}

