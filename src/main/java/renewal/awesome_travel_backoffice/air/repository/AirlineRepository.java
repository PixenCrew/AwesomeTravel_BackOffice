package renewal.awesome_travel_backoffice.air.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import renewal.common.entity.Airline;

public interface AirlineRepository extends JpaRepository<Airline,String> {
    // 항공사명(한글) 기준 조회
    Optional<Airline> findByNameKor(String nameKor);

    // 항공사명(영문) 기준 조회
    Optional<Airline> findByNameEng(String nameEng);

    // 항공사 코드 기준 조회 (예: "KE", "OZ" 등)
    Optional<Airline> findByCode(String code);
    
    // 항공사 목록 조회
    @Query("SELECT DISTINCT a.code FROM Airline a WHERE a.code IS NOT NULL")
    List<String> findDistinctAirlines();
}
