package renewal.awesome_travel_backoffice.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import renewal.awesome_travel_backoffice.review.dto.response.ReviewReportResponseDto;
import renewal.awesome_travel_backoffice.review.service.ReviewReportService;
import renewal.common.entity.ReviewReport.ReportReason;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/review-reports")
public class ReviewReportAdminController {

    private final ReviewReportService reviewReportService;

    @GetMapping
    public ResponseEntity<Page<ReviewReportResponseDto>> getAllReports(
            @RequestParam(required = false) ReportReason reason,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable
    ) {
        java.time.LocalDateTime startDateTime = null;
        java.time.LocalDateTime endDateTime = null;
        if (startDate != null && !startDate.isEmpty()) {
            startDateTime = java.time.LocalDate.parse(startDate).atStartOfDay();
        }
        if (endDate != null && !endDate.isEmpty()) {
            endDateTime = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
        }
        return ResponseEntity.ok(reviewReportService.getAllReports(
            reason, keyword, startDateTime, endDateTime, pageable));
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        reviewReportService.deleteReport(reportId);
        return ResponseEntity.ok().build();
    }
}

