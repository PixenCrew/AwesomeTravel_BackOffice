package renewal.awesome_travel_backoffice.faq.repositiry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.Faq.FaqCategory;
import renewal.common.entity.Faq;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    Page<Faq> findByCategory(FaqCategory category, Pageable pageable);

    @Query("SELECT f FROM Faq f WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "((:searchType IS NULL OR :searchType = '' OR :searchType = 'TITLE_CONTENT') AND (f.question LIKE CONCAT('%', :keyword, '%') OR f.answer LIKE CONCAT('%', :keyword, '%'))) OR " +
            "(:searchType = 'TITLE' AND f.question LIKE CONCAT('%', :keyword, '%')) OR " +
            "(:searchType = 'CONTENT' AND f.answer LIKE CONCAT('%', :keyword, '%'))) AND " +
            "(:category IS NULL OR f.category = :category) AND " +
            "(:isVisible IS NULL OR f.visible = :isVisible) AND " +
            "(:startDate IS NULL OR f.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR f.createdAt <= :endDate) " +
            "ORDER BY f.createdAt DESC")
    Page<Faq> searchFaqs(@Param("keyword") String keyword,
                         @Param("searchType") String searchType,
                         @Param("category") FaqCategory category,
                         @Param("isVisible") Boolean isVisible,
                         @Param("startDate") java.time.LocalDateTime startDate,
                         @Param("endDate") java.time.LocalDateTime endDate,
                         Pageable pageable);
}
