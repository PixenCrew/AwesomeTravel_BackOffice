package renewal.awesome_travel_backoffice.citycode.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.common.entity.CityCode;
import renewal.common.repository.CityCodeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityCodeService {

    private final CityCodeRepository cityCodeRepository;

    // 모든 도시 코드 조회 (페이징)
    public Page<CityCode> getAllCityCodes(Pageable pageable) {
        return cityCodeRepository.findAllCityCodes(pageable);
    }

    // 모든 도시 코드 조회 (리스트)
    public List<CityCode> getAllCityCodesList() {
        return cityCodeRepository.findAllCityCodesList();
    }

    // 도시 코드로 조회
    public Optional<CityCode> getCityCodeByCode(String code) {
        return cityCodeRepository.findById(code);
    }

    // 국가별 도시 조회
    public Page<CityCode> getCitiesByCountry(String country, Pageable pageable) {
        return cityCodeRepository.findByCountry(country, pageable);
    }

    // 국가별 도시 조회 (리스트)
    public List<CityCode> getCitiesByCountryList(String country) {
        return cityCodeRepository.findByCountryList(country);
    }

    // 도시명(한글)으로 검색
    public Page<CityCode> searchByKor(String kor, Pageable pageable) {
        return cityCodeRepository.findByKorContaining(kor, pageable);
    }

    // 도시명(영문)으로 검색
    public Page<CityCode> searchByEng(String eng, Pageable pageable) {
        return cityCodeRepository.findByEngContaining(eng, pageable);
    }

    // 도시 코드로 검색
    public Page<CityCode> searchByCode(String code, Pageable pageable) {
        return cityCodeRepository.findByCodeContaining(code, pageable);
    }

    // 국가 + 도시명(한글) 검색
    public Page<CityCode> searchByCountryAndKor(String country, String kor, Pageable pageable) {
        return cityCodeRepository.findByCountryAndKorContaining(country, kor, pageable);
    }

    // 도시 코드 생성
    @Transactional
    public CityCode createCityCode(CityCode cityCode) {
        // 코드 중복 체크
        if (cityCodeRepository.existsById(cityCode.getCityCode())) {
            throw new RuntimeException("이미 존재하는 도시 코드입니다: " + cityCode.getCityCode());
        }
        return cityCodeRepository.save(cityCode);
    }

    // 도시 코드 수정
    @Transactional
    public CityCode updateCityCode(String code, CityCode updatedCityCode) {
        CityCode cityCode = cityCodeRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("도시 코드를 찾을 수 없습니다: " + code));

        cityCode.setCountry(updatedCityCode.getCountry());
        cityCode.setKor(updatedCityCode.getKor());
        cityCode.setEng(updatedCityCode.getEng());

        return cityCodeRepository.save(cityCode);
    }

    // 도시 코드 삭제
    @Transactional
    public void deleteCityCode(String code) {
        if (!cityCodeRepository.existsById(code)) {
            throw new RuntimeException("도시 코드를 찾을 수 없습니다: " + code);
        }
        cityCodeRepository.deleteById(code);
    }

    // 도시 코드 존재 여부 확인
    public boolean existsByCode(String code) {
        return cityCodeRepository.existsById(code);
    }
}


