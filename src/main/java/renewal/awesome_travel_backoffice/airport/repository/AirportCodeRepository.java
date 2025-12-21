package renewal.awesome_travel_backoffice.airport.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import renewal.common.entity.AirportCode;

@Repository
public interface AirportCodeRepository extends JpaRepository<AirportCode, String> {

    // 공항명(한글)으로 검색
    @Query("SELECT a FROM AirportCode a WHERE a.airportKor LIKE %:airportKor% ORDER BY a.airportCode ASC")
    Page<AirportCode> findByNameKorContaining(@Param("airportKor") String airportKor, Pageable pageable);

    // 공항명(영문)으로 검색
    @Query("SELECT a FROM AirportCode a WHERE a.airportEng LIKE %:airportEng% ORDER BY a.airportCode ASC")
    Page<AirportCode> findByNameEngContaining(@Param("airportEng") String airportEng, Pageable pageable);

    // 공항 코드로 검색
    @Query("SELECT a FROM AirportCode a WHERE a.airportCode LIKE %:airportCode% ORDER BY a.airportCode ASC")
    Page<AirportCode> findByCodeContaining(@Param("airportCode") String airportCode, Pageable pageable);

    // 국가별 공항 조회
    @Query("SELECT a FROM AirportCode a WHERE a.cityCode.countryCode.countryCode = :countryCode ORDER BY a.airportCode ASC")
    Page<AirportCode> findByCountryCode(@Param("countryCode") String countryCode, Pageable pageable);

    // 도시별 공항 조회
    @Query("SELECT a FROM AirportCode a WHERE a.cityCode.cityCode = :cityCode ORDER BY a.airportCode ASC")
    Page<AirportCode> findByCityCode(@Param("cityCode") String cityCode, Pageable pageable);
    
    // 도시별 공항 조회 (리스트)
    @Query("SELECT a FROM AirportCode a WHERE a.cityCode.cityCode = :cityCode ORDER BY a.airportCode ASC")
    List<AirportCode> findByCityCodeList(@Param("cityCode") String cityCode);

    // 국가 + 도시별 공항 조회
    @Query("SELECT a FROM AirportCode a WHERE a.cityCode.countryCode.countryCode = :countryCode AND a.cityCode.cityCode = :cityCode ORDER BY a.airportCode ASC")
    Page<AirportCode> findByCountryCodeAndCityCode(@Param("countryCode") String countryCode,
            @Param("cityCode") String cityCode, Pageable pageable);

    // 모든 공항 조회 (정렬)
    @Query("SELECT a FROM AirportCode a ORDER BY a.cityCode.countryCode.countryCode ASC, a.cityCode.cityCode ASC, a.airportCode ASC")
    Page<AirportCode> findAllAirportCodes(Pageable pageable);

    // 모든 공항 조회 (리스트)
    @Query("SELECT a FROM AirportCode a ORDER BY a.cityCode.countryCode.countryCode ASC, a.cityCode.cityCode ASC, a.airportCode ASC")
    List<AirportCode> findAllAirportCodesList();

    // 공항 코드로 조회 (단일)
    @Query("SELECT a FROM AirportCode a WHERE a.airportCode = :airportCode")
    Optional<AirportCode> findByCode(@Param("airportCode") String airportCode);

    @EntityGraph(attributePaths = { "cityCode", "cityCode.countryCode" })
    List<AirportCode> findByCityCodeCountryCodeCountryCode(String string);

    @Query("""
            SELECT a
            FROM AirportCode a
            JOIN FETCH a.cityCode
            WHERE a.cityCode.cityCode IN (
                SELECT d.value
                FROM MenuCode m
                JOIN m.details d
                WHERE d.targetColumn = renewal.common.entity.MenuCode.MenuCodeDetail.TargetColumn.CITY
            )
            """)
    List<AirportCode> findAllByMenuCodeCities();
}
