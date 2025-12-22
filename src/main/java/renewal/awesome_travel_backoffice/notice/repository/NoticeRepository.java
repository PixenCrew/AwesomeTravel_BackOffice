package renewal.awesome_travel_backoffice.notice.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice> {
    @Modifying
    @Query("UPDATE Notice n SET n.isVisible = false " +
            "WHERE (n.startAt > :now OR n.endAt < :now) AND n.isVisible = true")
    int hideOutOfPeriodNotices(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Notice n SET n.isVisible = true " +
            "WHERE n.startAt <= :now AND n.endAt >= :now AND n.isVisible = false")
    int exposeValidNotices(@Param("now") LocalDateTime now);

    // 고정순서 중복 체크 (생성 시)
    boolean existsByFixTrueAndPriority(Integer priority);
    
    // 고정순서 중복 체크 (수정 시, 자기 자신 제외)
    boolean existsByFixTrueAndPriorityAndIdNot(Integer priority, Long id);

}

