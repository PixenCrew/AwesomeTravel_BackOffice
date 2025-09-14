package renewal.awesome_travel_backoffice.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.review.dto.response.ReviewResponseDto;
import renewal.awesome_travel_backoffice.review.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/comments")
public class ReviewAdminController {

    private final ReviewService reviewService;

    // 전체 댓글 검색/조회
    @GetMapping
    public ResponseEntity<Page<ReviewResponseDto>> searchReviews(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.searchAllReviews(keyword, pageable));
    }

    // 댓글 삭제 (신고 연관도 함께 삭제됨)
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReviewByAdmin(@PathVariable Long reviewId) {
        reviewService.deleteReviewByAdmin(reviewId);
        return ResponseEntity.ok().build();
    }
}
