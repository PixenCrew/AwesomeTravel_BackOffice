package renewal.awesome_travel_backoffice.banner.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import renewal.common.entity.Banner;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long>, JpaSpecificationExecutor<Banner> {

    // 활성화된 배너만 조회 (현재 날짜 기준)
    @Query("SELECT b FROM Banner b WHERE b.active = true AND b.startDate <= :currentDate AND b.endDate >= :currentDate ORDER BY b.displayOrder ASC")
    List<Banner> findActiveBanners(@Param("currentDate") LocalDate currentDate);

    // 활성화된 배너만 조회 (페이징)
    @Query("SELECT b FROM Banner b WHERE b.active = true AND b.startDate <= :currentDate AND b.endDate >= :currentDate ORDER BY b.displayOrder ASC")
    Page<Banner> findActiveBanners(@Param("currentDate") LocalDate currentDate, Pageable pageable);

    // 모든 배너 조회 (관리자용)
    @Query("SELECT b FROM Banner b ORDER BY b.displayOrder ASC, b.createdAt DESC")
    Page<Banner> findAllBanners(Pageable pageable);

    // 제목으로 검색
    @Query("SELECT b FROM Banner b WHERE b.title LIKE %:title% ORDER BY b.displayOrder ASC, b.createdAt DESC")
    Page<Banner> findByTitleContaining(@Param("title") String title, Pageable pageable);

    // 활성 상태로 검색
    @Query("SELECT b FROM Banner b WHERE b.active = :active ORDER BY b.displayOrder ASC, b.createdAt DESC")
    Page<Banner> findByActive(@Param("active") Boolean active, Pageable pageable);

    // 날짜 범위로 검색
    @Query("SELECT b FROM Banner b WHERE b.startDate >= :startDate AND b.endDate <= :endDate ORDER BY b.displayOrder ASC, b.createdAt DESC")
    Page<Banner> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
}

