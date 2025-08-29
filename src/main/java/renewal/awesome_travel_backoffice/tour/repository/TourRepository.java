package renewal.awesome_travel_backoffice.tour.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import renewal.common.entity.Tour;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
     
    // 투어 등록한 회사들 목록 조회
    @Query("SELECT DISTINCT t.company FROM Tour t WHERE t.company IS NOT NULL")
    List<String> findDistinctCompanies();
}