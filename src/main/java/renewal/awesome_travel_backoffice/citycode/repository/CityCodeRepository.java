package renewal.awesome_travel_backoffice.citycode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import renewal.common.entity.CityCode;

import java.util.List;

@Repository
public interface CityCodeRepository extends JpaRepository<CityCode, String> {

    // 도시명(한글)으로 검색
    @Query("SELECT c FROM CityCode c WHERE c.kor LIKE %:kor% ORDER BY c.code ASC")
    Page<CityCode> findByKorContaining(@Param("kor") String kor, Pageable pageable);

    // 도시명(영문)으로 검색
    @Query("SELECT c FROM CityCode c WHERE c.eng LIKE %:eng% ORDER BY c.code ASC")
    Page<CityCode> findByEngContaining(@Param("eng") String eng, Pageable pageable);

    // 도시 코드로 검색
    @Query("SELECT c FROM CityCode c WHERE c.code LIKE %:code% ORDER BY c.code ASC")
    Page<CityCode> findByCodeContaining(@Param("code") String code, Pageable pageable);

    // 국가별 도시 조회
    @Query("SELECT c FROM CityCode c WHERE c.country = :country ORDER BY c.code ASC")
    Page<CityCode> findByCountry(@Param("country") String country, Pageable pageable);

    // 국가별 도시 조회 (리스트)
    @Query("SELECT c FROM CityCode c WHERE c.country = :country ORDER BY c.code ASC")
    List<CityCode> findByCountryList(@Param("country") String country);

    // 국가 + 도시명(한글) 검색
    @Query("SELECT c FROM CityCode c WHERE c.country = :country AND c.kor LIKE %:kor% ORDER BY c.code ASC")
    Page<CityCode> findByCountryAndKorContaining(@Param("country") String country, @Param("kor") String kor, Pageable pageable);

    // 모든 도시 코드 조회 (정렬)
    @Query("SELECT c FROM CityCode c ORDER BY c.country ASC, c.code ASC")
    Page<CityCode> findAllCityCodes(Pageable pageable);

    // 모든 도시 코드 조회 (리스트, 셀렉트박스용)
    @Query("SELECT c FROM CityCode c ORDER BY c.country ASC, c.code ASC")
    List<CityCode> findAllCityCodesList();
}
