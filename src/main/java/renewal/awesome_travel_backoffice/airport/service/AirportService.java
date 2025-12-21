package renewal.awesome_travel_backoffice.airport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.airport.repository.AirportCodeRepository;
import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.common.entity.AirportCode;
import renewal.common.entity.Air;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirportService {

    private final AirportCodeRepository airportRepository;
    private final AirRepository airRepository;

    // 모든 공항 조회 (페이징)
    public Page<AirportCode> getAllAirports(@NonNull Pageable pageable) {
        return airportRepository.findAll(pageable);
    }

    // 모든 공항 조회 (리스트)
    public List<AirportCode> getAllAirportsList() {
        return airportRepository.findAllAirportCodesList();
    }

    // 공항 코드로 조회
    public Optional<AirportCode> getAirportByCode(@NonNull String code) {
        return airportRepository.findById(code);
    }

    // 국가별 공항 조회
    public Page<AirportCode> getAirportsByCountry(@NonNull String countryCode, @NonNull Pageable pageable) {
        return airportRepository.findByCountryCode(countryCode, pageable);
    }

    // 도시별 공항 조회
    public Page<AirportCode> getAirportsByCity(@NonNull String cityCode, @NonNull Pageable pageable) {
        return airportRepository.findByCityCode(cityCode, pageable);
    }

    // 국가 + 도시별 공항 조회
    public Page<AirportCode> getAirportsByCountryAndCity(@NonNull String countryCode, @NonNull String cityCode,
            @NonNull Pageable pageable) {
        return airportRepository.findByCountryCodeAndCityCode(countryCode, cityCode, pageable);
    }

    // 공항명(한글)으로 검색
    public Page<AirportCode> searchByNameKor(@NonNull String nameKor, @NonNull Pageable pageable) {
        return airportRepository.findByNameKorContaining(nameKor, pageable);
    }

    // 공항명(영문)으로 검색
    public Page<AirportCode> searchByNameEng(@NonNull String nameEng, @NonNull Pageable pageable) {
        return airportRepository.findByNameEngContaining(nameEng, pageable);
    }

    // 공항 코드로 검색
    public Page<AirportCode> searchByCode(@NonNull String code, @NonNull Pageable pageable) {
        return airportRepository.findByCodeContaining(code, pageable);
    }

    // 공항 생성
    @Transactional
    public AirportCode createAirport(AirportCode airport) {
        String newCode = Objects.requireNonNull(airport.getAirportCode(), "공항 코드가 필요합니다.");
        // 코드 중복 체크
        if (airportRepository.existsById(newCode)) {
            throw new RuntimeException("이미 존재하는 공항 코드입니다: " + newCode);
        }
        return airportRepository.save(airport);
    }

    // 공항 수정
    @Transactional
    public AirportCode updateAirport(@NonNull String code, @NonNull AirportCode updatedAirport) {
        AirportCode airport = airportRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("공항 코드를 찾을 수 없습니다: " + code));

        airport.setAirportKor(updatedAirport.getAirportKor());
        airport.setAirportEng(updatedAirport.getAirportEng());
        airport.setCityCode(updatedAirport.getCityCode());

        return airportRepository.save(airport);
    }

    // 공항 삭제
    @Transactional
    public void deleteAirport(@NonNull String code) {
        if (!airportRepository.existsById(code)) {
            throw new RuntimeException("공항 코드를 찾을 수 없습니다: " + code);
        }
        
        // 해당 공항 코드를 참조하는 항공(Air) 데이터가 있는지 확인
        List<Air> departAirs = airRepository.findByDepartAirportCode(code);
        List<Air> arriveAirs = airRepository.findByArriveAirportCode(code);
        
        int totalCount = departAirs.size() + arriveAirs.size();
        if (totalCount > 0) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("이 공항 코드를 참조하는 항공 데이터가 ").append(totalCount).append("개 있어 삭제할 수 없습니다.\n");
            
            if (!departAirs.isEmpty()) {
                errorMessage.append("- 출발 공항으로 사용: ").append(departAirs.size()).append("개\n");
            }
            if (!arriveAirs.isEmpty()) {
                errorMessage.append("- 도착 공항으로 사용: ").append(arriveAirs.size()).append("개\n");
            }
            
            errorMessage.append("먼저 해당 항공 데이터를 삭제하거나 수정한 후 공항 코드를 삭제해주세요.\n");
            errorMessage.append("항공 관리 페이지에서 공항 코드 '").append(code).append("'로 검색하여 관련 항공 데이터를 확인하세요.");
            
            throw new RuntimeException(errorMessage.toString());
        }
        
        airportRepository.deleteById(code);
    }

    // 공항 코드 존재 여부 확인
    public boolean existsByCode(@NonNull String code) {
        return airportRepository.existsById(code);
    }
}
