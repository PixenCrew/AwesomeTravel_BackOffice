package renewal.awesome_travel_backoffice.countrycode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import renewal.common.entity.CountryCode;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryCodeRepository extends JpaRepository<CountryCode, String> {

    // 국가명(한글)으로 검색
    @Query("SELECT c FROM CountryCode c WHERE c.nameKor LIKE %:nameKor% ORDER BY c.code ASC")
    Page<CountryCode> findByNameKorContaining(@Param("nameKor") String nameKor, Pageable pageable);

    // 국가명(영문)으로 검색
    @Query("SELECT c FROM CountryCode c WHERE c.nameEng LIKE %:nameEng% ORDER BY c.code ASC")
    Page<CountryCode> findByNameEngContaining(@Param("nameEng") String nameEng, Pageable pageable);

    // 국가 코드로 검색
    @Query("SELECT c FROM CountryCode c WHERE c.code LIKE %:code% ORDER BY c.code ASC")
    Page<CountryCode> findByCodeContaining(@Param("code") String code, Pageable pageable);

    // 모든 국가 코드 조회 (정렬)
    @Query("SELECT c FROM CountryCode c ORDER BY c.code ASC")
    Page<CountryCode> findAllCountryCodes(Pageable pageable);

    // 모든 국가 코드 조회 (리스트, 셀렉트박스용)
    @Query("SELECT c FROM CountryCode c ORDER BY c.code ASC")
    List<CountryCode> findAllCountryCodesList();

    // 국가 코드로 조회 (단일, 기존 코드 호환용)
    @Query("SELECT c FROM CountryCode c WHERE c.code = :code")
    Optional<CountryCode> findByCode(@Param("code") String code);
}
