package renewal.awesome_travel_backoffice.regioncategory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import renewal.common.entity.RegionCategory;
import renewal.common.entity.RegionCategory.CategoryType;

import java.util.List;

@Repository
public interface RegionCategoryRepository extends JpaRepository<RegionCategory, Long> {

    // 타입별 조회
    @Query("SELECT r FROM RegionCategory r WHERE r.type = :type ORDER BY r.displayOrder ASC, r.id ASC")
    Page<RegionCategory> findByType(@Param("type") CategoryType type, Pageable pageable);

    // 타입별 조회 (리스트)
    @Query("SELECT r FROM RegionCategory r WHERE r.type = :type ORDER BY r.displayOrder ASC, r.id ASC")
    List<RegionCategory> findByTypeList(@Param("type") CategoryType type);

    // 부모 카테고리로 조회
    @Query("SELECT r FROM RegionCategory r WHERE r.parentId = :parentId ORDER BY r.displayOrder ASC, r.id ASC")
    List<RegionCategory> findByParentId(@Param("parentId") Long parentId);

    // 최상위 카테고리 조회 (부모 없음)
    @Query("SELECT r FROM RegionCategory r WHERE r.parentId IS NULL ORDER BY r.displayOrder ASC, r.id ASC")
    List<RegionCategory> findTopLevel();

    // 이름으로 검색
    @Query("SELECT r FROM RegionCategory r WHERE r.name LIKE %:name% ORDER BY r.displayOrder ASC")
    Page<RegionCategory> findByNameContaining(@Param("name") String name, Pageable pageable);

    // 코드로 검색
    @Query("SELECT r FROM RegionCategory r WHERE r.code LIKE %:code% ORDER BY r.displayOrder ASC")
    Page<RegionCategory> findByCodeContaining(@Param("code") String code, Pageable pageable);

    // 타입 + 이름 검색
    @Query("SELECT r FROM RegionCategory r WHERE r.type = :type AND r.name LIKE %:name% ORDER BY r.displayOrder ASC")
    Page<RegionCategory> findByTypeAndNameContaining(@Param("type") CategoryType type, @Param("name") String name, Pageable pageable);

    // 모든 카테고리 조회 (계층 순서대로)
    @Query("SELECT r FROM RegionCategory r ORDER BY r.type ASC, r.parentId ASC, r.displayOrder ASC")
    Page<RegionCategory> findAllCategories(Pageable pageable);

    // 모든 카테고리 조회 (리스트)
    @Query("SELECT r FROM RegionCategory r ORDER BY r.displayOrder ASC, r.id ASC")
    List<RegionCategory> findAllCategoriesList();
}
