package renewal.awesome_travel_backoffice.inquiry.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    @Query("SELECT i FROM Inquiry i WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "((:searchType IS NULL OR :searchType = '' OR :searchType = 'TITLE_CONTENT') AND (i.title LIKE CONCAT('%', :keyword, '%') OR i.content LIKE CONCAT('%', :keyword, '%'))) OR " +
            "(:searchType = 'TITLE' AND i.title LIKE CONCAT('%', :keyword, '%')) OR " +
            "(:searchType = 'CONTENT' AND i.content LIKE CONCAT('%', :keyword, '%'))) AND " +
            "(:isAnswered IS NULL OR i.isAnswered = :isAnswered) AND " +
            "(:category IS NULL OR i.category = :category) AND " +
            "(:status IS NULL OR i.status = :status) AND " +
            "(:startDate IS NULL OR i.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR i.createdAt <= :endDate) " +
            "ORDER BY i.createdAt DESC")
    Page<Inquiry> searchAdmin(@Param("keyword") String keyword,
                              @Param("searchType") String searchType,
                              @Param("isAnswered") Boolean isAnswered,
                              @Param("category") renewal.common.entity.Inquiry.InquiryCategory category,
                              @Param("status") renewal.common.entity.Inquiry.InquiryStatus status,
                              @Param("startDate") java.time.LocalDateTime startDate,
                              @Param("endDate") java.time.LocalDateTime endDate,
                              Pageable pageable);
}
