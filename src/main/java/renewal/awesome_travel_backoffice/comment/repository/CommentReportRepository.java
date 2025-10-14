package renewal.awesome_travel_backoffice.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.ReviewReport.ReportReason;
import renewal.common.entity.ReviewReport;

public interface CommentReportRepository extends JpaRepository<ReviewReport, Long> {

    @Query("SELECT r FROM ReviewReport r " +
            "JOIN FETCH r.review c " +
            "JOIN FETCH c.writer w " +
            "JOIN FETCH r.reporter u " +
            "WHERE (:reason IS NULL OR r.reason = :reason)")
    Page<ReviewReport> searchReports(@Param("reason") ReportReason reason, Pageable pageable);

}
