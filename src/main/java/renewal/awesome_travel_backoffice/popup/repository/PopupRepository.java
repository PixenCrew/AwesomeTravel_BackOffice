package renewal.awesome_travel_backoffice.popup.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.Popup;

import java.time.LocalDate;
import java.util.List;

public interface PopupRepository extends JpaRepository<Popup, Long> {
    
    // 활성화된 팝업들을 표시 순서대로 조회
    @Query("SELECT p FROM Popup p WHERE p.active = true ORDER BY p.displayOrder ASC")
    List<Popup> findActivePopupsOrderByDisplayOrder();
    
    // 현재 날짜에 노출되어야 하는 활성화된 팝업들 조회
    @Query("SELECT p FROM Popup p WHERE p.active = true AND p.startDate <= :currentDate AND p.endDate >= :currentDate ORDER BY p.displayOrder ASC")
    List<Popup> findCurrentActivePopups(@Param("currentDate") LocalDate currentDate);
    
    // 제목으로 검색
    List<Popup> findByTitleContainingIgnoreCase(String title);
    
    // 활성화 상태로 검색 (페이징 지원)
    Page<Popup> findByActive(boolean active, Pageable pageable);
    
    // 시작일 범위로 검색
    List<Popup> findByStartDateBetween(LocalDate startDateFrom, LocalDate startDateTo);
    
    // 종료일 범위로 검색
    List<Popup> findByEndDateBetween(LocalDate endDateFrom, LocalDate endDateTo);
}
