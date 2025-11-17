package renewal.awesome_travel_backoffice.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.ReviewReport;
import renewal.common.entity.ReviewReport.ReportReason;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

    @Query("SELECT r FROM ReviewReport r " +
            "JOIN FETCH r.review c " +
            "JOIN FETCH c.writer w " +
            "JOIN FETCH r.reporter u " +
            "WHERE (:reason IS NULL OR r.reason = :reason) " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "     LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(w.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:startDate IS NULL OR r.reportedAt >= :startDate) " +
            "AND (:endDate IS NULL OR r.reportedAt <= :endDate)")
    Page<ReviewReport> searchReports(
        @Param("reason") ReportReason reason,
        @Param("keyword") String keyword,
        @Param("startDate") java.time.LocalDateTime startDate,
        @Param("endDate") java.time.LocalDateTime endDate,
        Pageable pageable);

}
