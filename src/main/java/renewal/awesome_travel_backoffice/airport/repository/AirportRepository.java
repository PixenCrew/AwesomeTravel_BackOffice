package renewal.awesome_travel_backoffice.airport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import renewal.common.entity.Airport;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, String> {

    // 공항명(한글)으로 검색
    @Query("SELECT a FROM Airport a WHERE a.nameKor LIKE %:nameKor% ORDER BY a.code ASC")
    Page<Airport> findByNameKorContaining(@Param("nameKor") String nameKor, Pageable pageable);

    // 공항명(영문)으로 검색
    @Query("SELECT a FROM Airport a WHERE a.nameEng LIKE %:nameEng% ORDER BY a.code ASC")
    Page<Airport> findByNameEngContaining(@Param("nameEng") String nameEng, Pageable pageable);

    // 공항 코드로 검색
    @Query("SELECT a FROM Airport a WHERE a.code LIKE %:code% ORDER BY a.code ASC")
    Page<Airport> findByCodeContaining(@Param("code") String code, Pageable pageable);

    // 국가별 공항 조회
    @Query("SELECT a FROM Airport a WHERE a.countryCode = :countryCode ORDER BY a.code ASC")
    Page<Airport> findByCountryCode(@Param("countryCode") String countryCode, Pageable pageable);

    // 도시별 공항 조회
    @Query("SELECT a FROM Airport a WHERE a.cityCode = :cityCode ORDER BY a.code ASC")
    Page<Airport> findByCityCode(@Param("cityCode") String cityCode, Pageable pageable);

    // 국가 + 도시별 공항 조회
    @Query("SELECT a FROM Airport a WHERE a.countryCode = :countryCode AND a.cityCode = :cityCode ORDER BY a.code ASC")
    Page<Airport> findByCountryCodeAndCityCode(@Param("countryCode") String countryCode, @Param("cityCode") String cityCode, Pageable pageable);

    // 모든 공항 조회 (정렬)
    @Query("SELECT a FROM Airport a ORDER BY a.countryCode ASC, a.cityCode ASC, a.code ASC")
    Page<Airport> findAllAirports(Pageable pageable);

    // 모든 공항 조회 (리스트)
    @Query("SELECT a FROM Airport a ORDER BY a.countryCode ASC, a.cityCode ASC, a.code ASC")
    List<Airport> findAllAirportsList();

    // 공항 코드로 조회 (단일)
    @Query("SELECT a FROM Airport a WHERE a.code = :code")
    Optional<Airport> findByCode(@Param("code") String code);
}
