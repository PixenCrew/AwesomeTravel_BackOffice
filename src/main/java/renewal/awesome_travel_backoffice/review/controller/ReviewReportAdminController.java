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
            Pageable pageable
    ) {
        return ResponseEntity.ok(reviewReportService.getAllReports(reason, pageable));
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        reviewReportService.deleteReport(reportId);
        return ResponseEntity.ok().build();
    }
}

