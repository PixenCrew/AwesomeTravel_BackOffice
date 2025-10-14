package renewal.awesome_travel_backoffice.airline.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import renewal.common.entity.Airline;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirlineCodeRepository extends JpaRepository<Airline, String> {

    // 항공사명(한글)으로 검색
    @Query("SELECT a FROM Airline a WHERE a.nameKor LIKE %:nameKor% ORDER BY a.code ASC")
    Page<Airline> findByNameKorContaining(@Param("nameKor") String nameKor, Pageable pageable);

    // 항공사명(영문)으로 검색
    @Query("SELECT a FROM Airline a WHERE a.nameEng LIKE %:nameEng% ORDER BY a.code ASC")
    Page<Airline> findByNameEngContaining(@Param("nameEng") String nameEng, Pageable pageable);

    // 항공사 코드로 검색
    @Query("SELECT a FROM Airline a WHERE a.code LIKE %:code% ORDER BY a.code ASC")
    Page<Airline> findByCodeContaining(@Param("code") String code, Pageable pageable);

    // 모든 항공사 조회 (정렬)
    @Query("SELECT a FROM Airline a ORDER BY a.code ASC")
    Page<Airline> findAllAirlines(Pageable pageable);

    // 모든 항공사 조회 (리스트, 셀렉트박스용)
    @Query("SELECT a FROM Airline a ORDER BY a.code ASC")
    List<Airline> findAllAirlinesList();

    // 항공사 코드로 조회 (단일, 기존 코드 호환용)
    @Query("SELECT a FROM Airline a WHERE a.code = :code")
    Optional<Airline> findByCode(@Param("code") String code);
}
