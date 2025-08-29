package renewal.awesome_travel_backoffice.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.awesome_travel_backoffice.comment.utiles.ReportReason;
import renewal.common.entity.CommentReport;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    @Query("SELECT r FROM CommentReport r " +
            "JOIN FETCH r.comment c " +
            "JOIN FETCH c.writer w " +
            "JOIN FETCH r.reporter u " +
            "WHERE (:reason IS NULL OR r.reason = :reason)")
    Page<CommentReport> searchReports(@Param("reason") ReportReason reason, Pageable pageable);

}
