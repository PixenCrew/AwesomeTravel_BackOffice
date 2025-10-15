package renewal.awesome_travel_backoffice.airport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.airport.repository.AirportCodeRepository;
import renewal.common.entity.AirportCode;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirportService {

    private final AirportCodeRepository airportRepository;

    // 모든 공항 조회 (페이징)
    public Page<AirportCode> getAllAirports(Pageable pageable) {
        return airportRepository.findAll(pageable);
    }

    // 모든 공항 조회 (리스트)
    public List<AirportCode> getAllAirportsList() {
        return airportRepository.findAll();
    }

    // 공항 코드로 조회
    public Optional<AirportCode> getAirportByCode(String code) {
        return airportRepository.findById(code);
    }

    // 국가별 공항 조회
    public Page<AirportCode> getAirportsByCountry(String countryCode, Pageable pageable) {
        return airportRepository.findByCountryCode(countryCode, pageable);
    }

    // 도시별 공항 조회
    public Page<AirportCode> getAirportsByCity(String cityCode, Pageable pageable) {
        return airportRepository.findByCityCode(cityCode, pageable);
    }

    // 국가 + 도시별 공항 조회
    public Page<AirportCode> getAirportsByCountryAndCity(String countryCode, String cityCode, Pageable pageable) {
        return airportRepository.findByCountryCodeAndCityCode(countryCode, cityCode, pageable);
    }

    // 공항명(한글)으로 검색
    public Page<AirportCode> searchByNameKor(String nameKor, Pageable pageable) {
        return airportRepository.findByNameKorContaining(nameKor, pageable);
    }

    // 공항명(영문)으로 검색
    public Page<AirportCode> searchByNameEng(String nameEng, Pageable pageable) {
        return airportRepository.findByNameEngContaining(nameEng, pageable);
    }

    // 공항 코드로 검색
    public Page<AirportCode> searchByCode(String code, Pageable pageable) {
        return airportRepository.findByCodeContaining(code, pageable);
    }

    // 공항 생성
    @Transactional
    public AirportCode createAirport(AirportCode airport) {
        // 코드 중복 체크
        if (airportRepository.existsById(airport.getAirportCode())) {
            throw new RuntimeException("이미 존재하는 공항 코드입니다: " + airport.getAirportCode());
        }
        return airportRepository.save(airport);
    }

    // 공항 수정
    @Transactional
    public AirportCode updateAirport(String code, AirportCode updatedAirport) {
        AirportCode airport = airportRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("공항 코드를 찾을 수 없습니다: " + code));

        airport.setCityCode(updatedAirport.getCityCode());
        airport.setAirportKor(updatedAirport.getAirportKor());
        airport.setAirportEng(updatedAirport.getAirportEng());
        airport.setCityCode(updatedAirport.getCityCode());

        return airportRepository.save(airport);
    }

    // 공항 삭제
    @Transactional
    public void deleteAirport(String code) {
        if (!airportRepository.existsById(code)) {
            throw new RuntimeException("공항 코드를 찾을 수 없습니다: " + code);
        }
        airportRepository.deleteById(code);
    }

    // 공항 코드 존재 여부 확인
    public boolean existsByCode(String code) {
        return airportRepository.existsById(code);
    }
}
