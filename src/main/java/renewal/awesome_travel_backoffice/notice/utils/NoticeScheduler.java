package renewal.awesome_travel_backoffice.notice.utils;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import renewal.awesome_travel_backoffice.notice.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeScheduler {

    private final NoticeRepository noticeRepository;

    @Transactional
    @Scheduled(cron = "0 0 * * * *") // 매시 정각
    public void syncNoticeVisibility() {
        LocalDateTime now = LocalDateTime.now();

        int hiddenCount = noticeRepository.hideOutOfPeriodNotices(now);
        int exposedCount = noticeRepository.exposeValidNotices(now);

        log.info("[공지 스케줄러] 숨김 처리된 공지: {}건, 노출 재개된 공지: {}건", hiddenCount, exposedCount);
    }

}
