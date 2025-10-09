package renewal.awesome_travel_backoffice.airport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.airport.repository.AirportRepository;
import renewal.common.entity.Airport;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirportService {

    private final AirportRepository airportRepository;

    // 모든 공항 조회 (페이징)
    public Page<Airport> getAllAirports(Pageable pageable) {
        return airportRepository.findAllAirports(pageable);
    }

    // 모든 공항 조회 (리스트)
    public List<Airport> getAllAirportsList() {
        return airportRepository.findAllAirportsList();
    }

    // 공항 코드로 조회
    public Optional<Airport> getAirportByCode(String code) {
        return airportRepository.findById(code);
    }

    // 국가별 공항 조회
    public Page<Airport> getAirportsByCountry(String countryCode, Pageable pageable) {
        return airportRepository.findByCountryCode(countryCode, pageable);
    }

    // 도시별 공항 조회
    public Page<Airport> getAirportsByCity(String cityCode, Pageable pageable) {
        return airportRepository.findByCityCode(cityCode, pageable);
    }

    // 국가 + 도시별 공항 조회
    public Page<Airport> getAirportsByCountryAndCity(String countryCode, String cityCode, Pageable pageable) {
        return airportRepository.findByCountryCodeAndCityCode(countryCode, cityCode, pageable);
    }

    // 공항명(한글)으로 검색
    public Page<Airport> searchByNameKor(String nameKor, Pageable pageable) {
        return airportRepository.findByNameKorContaining(nameKor, pageable);
    }

    // 공항명(영문)으로 검색
    public Page<Airport> searchByNameEng(String nameEng, Pageable pageable) {
        return airportRepository.findByNameEngContaining(nameEng, pageable);
    }

    // 공항 코드로 검색
    public Page<Airport> searchByCode(String code, Pageable pageable) {
        return airportRepository.findByCodeContaining(code, pageable);
    }

    // 공항 생성
    @Transactional
    public Airport createAirport(Airport airport) {
        // 코드 중복 체크
        if (airportRepository.existsById(airport.getCode())) {
            throw new RuntimeException("이미 존재하는 공항 코드입니다: " + airport.getCode());
        }
        return airportRepository.save(airport);
    }

    // 공항 수정
    @Transactional
    public Airport updateAirport(String code, Airport updatedAirport) {
        Airport airport = airportRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("공항 코드를 찾을 수 없습니다: " + code));

        airport.setCityCode(updatedAirport.getCityCode());
        airport.setCountryCode(updatedAirport.getCountryCode());
        airport.setNameKor(updatedAirport.getNameKor());
        airport.setNameEng(updatedAirport.getNameEng());
        airport.setAirportType(updatedAirport.getAirportType());

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
