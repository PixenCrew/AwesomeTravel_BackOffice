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

import renewal.awesome_travel_backoffice.comment.dto.response.CommentResponseDto;
import renewal.awesome_travel_backoffice.comment.service.CommentService;
import renewal.common.entity.CommentReport.ReportReason;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/comment")
@Controller
public class CommentViewController {

    private final CommentService commentService;

    @GetMapping
    public String listComments(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        
        // 정렬 설정
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        
        // 페이징 설정
        Pageable pageable = PageRequest.of(page, 20, sort);
        
        // 댓글 검색
        Page<CommentResponseDto> commentPage = commentService.searchAllComments(keyword, pageable);
        
        // 모델에 데이터 추가
        model.addAttribute("commentPage", commentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "댓글 관리");
        model.addAttribute("content", "components/comment/comment");
        
        return "layout";
    }
}



