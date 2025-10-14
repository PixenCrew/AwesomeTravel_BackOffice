package renewal.awesome_travel_backoffice.comment.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.awesome_travel_backoffice.comment.dto.response.CommentResponseDto;
import renewal.awesome_travel_backoffice.review.repository.ReviewRepository;
import renewal.common.entity.Review;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final ReviewRepository reviewRepository;

    /**
     * 어드민 - 모든 리뷰 검색
     */
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> searchAllComments(String keyword, Pageable pageable) {
        Page<Review> reviews;
        if (keyword != null && !keyword.trim().isEmpty()) {
            reviews = reviewRepository.findByContentContaining(keyword, pageable);
        } else {
            reviews = reviewRepository.findAll(pageable);
        }
        return reviews.map(this::toResponseDto);
    }

    /**
     * 사용자별 최근 댓글 조회
     */
    public List<CommentResponseDto> getRecentCommentsByUser(Long userId) {
        List<Review> reviews = reviewRepository.findTop5ByWriterIdOrderByCreatedAtDesc(userId);
        return reviews.stream().map(this::toResponseDto).collect(java.util.stream.Collectors.toList());
    }

    /**
     * DTO 변환 메서드
     */
    private CommentResponseDto toResponseDto(Review review) {
        return CommentResponseDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productTitle(review.getProduct().getTitle())
                .writerId(review.getWriter().getId())
                .writerName(review.getWriter().getName())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}

