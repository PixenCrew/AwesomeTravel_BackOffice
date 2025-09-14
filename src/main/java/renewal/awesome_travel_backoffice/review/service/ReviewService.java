package renewal.awesome_travel_backoffice.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.awesome_travel_backoffice.review.dto.response.ReviewResponseDto;
import renewal.awesome_travel_backoffice.review.repository.ReviewRepository;
import renewal.common.entity.Review;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository commentRepository;

    /**
     * 어드민 - 전체 댓글 목록 검색 (키워드 포함)
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> searchAllReviews(String keyword, Pageable pageable) {
        return commentRepository.searchAll(keyword, pageable)
                .map(this::toResponseDto);
    }

    /**
     * 어드민 - 댓글 삭제 (신고 연관 포함)
     */
    @Transactional
    public void deleteReviewByAdmin(Long commentId) {
        Review comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 신고 연관 자동 삭제됨 (Cascade 설정 덕분에)
        commentRepository.delete(comment);
    }

    private ReviewResponseDto toResponseDto(Review comment) {
        return ReviewResponseDto.builder()
                .id(comment.getId())
                .writerName(comment.getWriter().getName())
                .content(comment.getContent())
                .rating(comment.getRating())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
