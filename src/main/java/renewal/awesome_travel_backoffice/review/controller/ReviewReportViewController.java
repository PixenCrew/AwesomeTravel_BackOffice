package renewal.awesome_travel_backoffice.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import renewal.awesome_travel_backoffice.review.dto.response.ReviewReportResponseDto;
import renewal.awesome_travel_backoffice.review.service.ReviewReportService;
import renewal.common.entity.ReviewReport.ReportReason;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/review-report")
@Controller
public class ReviewReportViewController {

    private final ReviewReportService reviewReportService;

    @GetMapping
    public String listReviewReports(
            @RequestParam(required = false) ReportReason reason,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "reportedAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        
        // 정렬 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        
        // 페이징 설정
        Pageable pageable = PageRequest.of(page, 20, sort);
        
        // 날짜 파싱
        java.time.LocalDateTime startDateTime = null;
        java.time.LocalDateTime endDateTime = null;
        if (startDate != null && !startDate.isEmpty()) {
            startDateTime = java.time.LocalDate.parse(startDate).atStartOfDay();
        }
        if (endDate != null && !endDate.isEmpty()) {
            endDateTime = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
        }
        
        // 신고 목록 검색
        Page<ReviewReportResponseDto> reportPage = reviewReportService.getAllReports(
            reason, keyword, startDateTime, endDateTime, pageable);
        
        // 모델에 데이터 추가
        model.addAttribute("reportPage", reportPage);
        model.addAttribute("reason", reason);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reportReasons", ReportReason.values());
        model.addAttribute("title", "댓글 신고 관리");
        model.addAttribute("content", "components/review/reviewReport");
        
        return "layout";
    }
}

