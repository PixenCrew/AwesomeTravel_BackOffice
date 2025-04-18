package renewal.awesome_travel_backoffice.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import renewal.awesome_travel_backoffice.notice.entity.Notice;

import java.time.LocalDateTime;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Modifying
    @Query("UPDATE Notice n SET n.isVisible = false " +
            "WHERE (n.startAt > :now OR n.endAt < :now) AND n.isVisible = true")
    int hideOutOfPeriodNotices(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Notice n SET n.isVisible = true " +
            "WHERE n.startAt <= :now AND n.endAt >= :now AND n.isVisible = false")
    int exposeValidNotices(@Param("now") LocalDateTime now);



}

