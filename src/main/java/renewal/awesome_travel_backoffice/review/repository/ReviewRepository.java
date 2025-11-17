package renewal.awesome_travel_backoffice.review.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
    SELECT c FROM Review c
    WHERE (:keyword IS NULL OR :keyword = '' OR
           LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           LOWER(c.writer.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           LOWER(c.product.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:rating IS NULL OR c.rating = :rating)
    AND (:productId IS NULL OR c.product.id = :productId)
    AND (:startDate IS NULL OR c.createdAt >= :startDate)
    AND (:endDate IS NULL OR c.createdAt <= :endDate)
    """)
    Page<Review> searchAll(
        @Param("keyword") String keyword,
        @Param("rating") Integer rating,
        @Param("productId") Long productId,
        @Param("startDate") java.time.LocalDateTime startDate,
        @Param("endDate") java.time.LocalDateTime endDate,
        Pageable pageable);

    /**
     * 특정 사용자의 최근 댓글 5개 조회
     */
    List<Review> findTop5ByWriterIdOrderByCreatedAtDesc(Long writerId);
    
    /**
     * 내용으로 검색
     */
    Page<Review> findByContentContaining(String keyword, Pageable pageable);

}
