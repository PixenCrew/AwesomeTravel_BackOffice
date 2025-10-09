package renewal.awesome_travel_backoffice.comment.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
    SELECT c FROM Comment c
    LEFT JOIN FETCH c.writer
    LEFT JOIN FETCH c.product
    WHERE (:keyword IS NULL OR
           LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           LOWER(c.writer.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           LOWER(c.product.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Comment> searchAll(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 특정 사용자의 최근 댓글 5개 조회
     */
    List<Comment> findTop5ByWriterIdOrderByCreatedAtDesc(Long writerId);

}
