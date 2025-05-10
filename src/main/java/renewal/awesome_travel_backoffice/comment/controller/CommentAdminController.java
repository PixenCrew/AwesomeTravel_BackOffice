package renewal.awesome_travel_backoffice.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.comment.dto.response.CommentResponseDto;
import renewal.awesome_travel_backoffice.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    // 전체 댓글 검색/조회
    @GetMapping
    public ResponseEntity<Page<CommentResponseDto>> searchComments(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.searchAllComments(keyword, pageable));
    }

    // 댓글 삭제 (신고 연관도 함께 삭제됨)
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteCommentByAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
        return ResponseEntity.ok().build();
    }
}
