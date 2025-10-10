package renewal.awesome_travel_backoffice.comment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import renewal.awesome_travel_backoffice.comment.dto.response.CommentReportResponseDto;
import renewal.awesome_travel_backoffice.comment.service.CommentReportService;
import renewal.common.entity.CommentReport.ReportReason;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/comment-reports")
public class CommentReportAdminController {

    private final CommentReportService commentReportService;

    @GetMapping
    public ResponseEntity<Page<CommentReportResponseDto>> getAllReports(
            @RequestParam(required = false) ReportReason reason,
            Pageable pageable
    ) {
        return ResponseEntity.ok(commentReportService.getAllReports(reason, pageable));
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        commentReportService.deleteReport(reportId);
        return ResponseEntity.ok().build();
    }
}

