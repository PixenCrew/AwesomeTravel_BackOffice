package renewal.awesome_travel_backoffice.comment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import renewal.awesome_travel_backoffice.comment.dto.response.CommentReportResponseDto;
import renewal.awesome_travel_backoffice.comment.service.CommentReportService;
import renewal.common.entity.CommentReport.ReportReason;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/comment-report")
@Controller
public class CommentReportViewController {

    private final CommentReportService commentReportService;

    @GetMapping
    public String listCommentReports(
            @RequestParam(required = false) ReportReason reason,
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
        
        // 신고 목록 검색
        Page<CommentReportResponseDto> reportPage = commentReportService.getAllReports(reason, pageable);
        
        // 모델에 데이터 추가
        model.addAttribute("reportPage", reportPage);
        model.addAttribute("reason", reason);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reportReasons", ReportReason.values());
        model.addAttribute("title", "댓글 신고 관리");
        model.addAttribute("content", "components/comment/commentReport");
        
        return "layout";
    }
}



