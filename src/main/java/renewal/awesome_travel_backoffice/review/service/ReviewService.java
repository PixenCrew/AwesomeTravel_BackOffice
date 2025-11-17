package renewal.awesome_travel_backoffice.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.awesome_travel_backoffice.review.dto.response.ProductReviewGroupDto;
import renewal.awesome_travel_backoffice.review.dto.response.ReviewResponseDto;
import renewal.awesome_travel_backoffice.review.repository.ReviewRepository;
import renewal.common.entity.Review;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BannedWordFilterService bannedWordFilterService;

    /**
     * 어드민 - 전체 댓글 목록 검색 (키워드 포함)
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> searchAllReviews(String keyword, Integer rating, Long productId, 
            java.time.LocalDateTime startDate, java.time.LocalDateTime endDate, Pageable pageable) {
        return reviewRepository.searchAll(keyword, rating, productId, startDate, endDate, pageable)
                .map(this::toResponseDtoWithProduct);
    }

    /**
     * 어드민 - 상품별로 그룹화된 댓글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ProductReviewGroupDto> getReviewsGroupedByProduct(String keyword, Integer rating, Long productId,
            java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        // 모든 댓글 조회 (페이징 없이)
        List<Review> allReviews = reviewRepository.searchAll(
            keyword, rating, productId, startDate, endDate, 
            org.springframework.data.domain.Pageable.unpaged()
        ).getContent();

        // DTO 변환
        List<ReviewResponseDto> reviewDtos = allReviews.stream()
                .map(this::toResponseDtoWithProduct)
                .collect(Collectors.toList());

        // 상품별로 그룹화
        Map<Long, List<ReviewResponseDto>> groupedByProduct = reviewDtos.stream()
                .filter(r -> r.getProductId() != null)
                .collect(Collectors.groupingBy(ReviewResponseDto::getProductId));

        // ProductReviewGroupDto로 변환
        return groupedByProduct.entrySet().stream()
                .map(entry -> {
                    List<ReviewResponseDto> productReviews = entry.getValue();
                    ReviewResponseDto firstReview = productReviews.get(0);
                    
                    // 평균 평점 계산
                    double avgRating = productReviews.stream()
                            .mapToInt(ReviewResponseDto::getRating)
                            .average()
                            .orElse(0.0);
                    
                    return ProductReviewGroupDto.builder()
                            .productId(firstReview.getProductId())
                            .productTitle(firstReview.getProductTitle())
                            .productPrice(firstReview.getProductPrice())
                            .reviewCount(productReviews.size())
                            .averageRating(Math.round(avgRating * 10.0) / 10.0) // 소수점 1자리
                            .reviews(productReviews)
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getReviewCount(), a.getReviewCount())) // 댓글 수 많은 순
                .collect(Collectors.toList());
    }

    /**
     * 어드민 - 댓글 삭제 (신고 연관 포함)
     */
    @Transactional
    public void deleteReviewByAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 신고 연관 자동 삭제됨 (Cascade 설정 덕분에)
        reviewRepository.delete(review);
    }

    /**
     * 특정 사용자의 최근 댓글 조회
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getRecentCommentsByUser(Long userId) {
        return reviewRepository.findTop5ByWriterIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponseDtoWithProduct)
                .toList();
    }

    /**
     * 댓글 생성 (금지어 필터링 적용)
     */
    @Transactional
    public Review createReview(renewal.common.entity.User writer, renewal.common.entity.Product product, 
            String content, int rating) {
        // 금지어 필터링
        String filteredContent = bannedWordFilterService.filter(content);
        
        Review review = Review.create(writer, product, filteredContent, rating);
        return reviewRepository.save(review);
    }

    /**
     * 댓글 수정 (금지어 필터링 적용)
     */
    @Transactional
    public Review updateReview(Long reviewId, String content, int rating) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        
        // 금지어 필터링
        String filteredContent = bannedWordFilterService.filter(content);
        
        review.update(filteredContent, rating);
        return reviewRepository.save(review);
    }

    private ReviewResponseDto toResponseDtoWithProduct(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .writerId(review.getWriter().getId())
                .writerName(review.getWriter().getName())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                .productTitle(review.getProduct() != null ? review.getProduct().getTitle() : null)
                .productPrice(review.getProduct() != null ? review.getProduct().getPrice() : null)
                .build();
    }
}
