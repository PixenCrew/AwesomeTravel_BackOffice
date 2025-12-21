package renewal.awesome_travel_backoffice.popup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.Popup;

import java.time.LocalDate;
import java.util.List;

public interface PopupRepository extends JpaRepository<Popup, Long>, JpaSpecificationExecutor<Popup> {
    
    // 현재 날짜에 노출되어야 하는 활성화된 팝업들 조회
    // (프론트엔드에서 현재 노출되어야 하는 팝업을 조회하는 용도로 사용)
    @Query("SELECT p FROM Popup p WHERE p.active = true AND p.startDate <= :currentDate AND p.endDate >= :currentDate ORDER BY p.displayOrder ASC")
    List<Popup> findCurrentActivePopups(@Param("currentDate") LocalDate currentDate);
}
