package renewal.awesome_travel_backoffice.air.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.lang.NonNull;

import renewal.common.entity.Air;

import java.util.List;

public interface AirRepository extends JpaRepository<Air, Long>, JpaSpecificationExecutor<Air>  {
    @Override
    @EntityGraph(attributePaths = "seatClasses", type = EntityGraph.EntityGraphType.LOAD)
    @NonNull 
    Page<Air> findAll(@Nullable Specification<Air> spec, @NonNull Pageable pageable);

    boolean existsByflightNumber(String code);
    
    // 출발 공항 코드로 조회
    @Query("SELECT a FROM Air a WHERE a.departAirport.airportCode = :airportCode")
    List<Air> findByDepartAirportCode(@Param("airportCode") String airportCode);
    
    // 도착 공항 코드로 조회
    @Query("SELECT a FROM Air a WHERE a.arriveAirport.airportCode = :airportCode")
    List<Air> findByArriveAirportCode(@Param("airportCode") String airportCode);

}
