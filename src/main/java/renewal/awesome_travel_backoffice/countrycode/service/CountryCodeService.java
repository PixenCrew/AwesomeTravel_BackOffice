package renewal.awesome_travel_backoffice.countrycode.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.common.entity.CountryCode;
import renewal.common.entity.CityCode;
import renewal.common.repository.CountryCodeRepository;
import renewal.common.repository.CityCodeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryCodeService {

    private final CountryCodeRepository countryCodeRepository;
    private final CityCodeRepository cityCodeRepository;

    // 모든 국가 코드 조회 (페이징)
    public Page<CountryCode> getAllCountryCodes(Pageable pageable) {
        return countryCodeRepository.findAllCountryCodes(pageable);
    }

    // 모든 국가 코드 조회 (리스트)
    public List<CountryCode> getAllCountryCodesList() {
        return countryCodeRepository.findAllCountryCodesList();
    }

    // 국가 코드로 조회
    public Optional<CountryCode> getCountryCodeByCode(String code) {
        return countryCodeRepository.findById(code);
    }

    // 국가명(한글)으로 검색
    public Page<CountryCode> searchByNameKor(String nameKor, Pageable pageable) {
        return countryCodeRepository.findByNameKorContaining(nameKor, pageable);
    }

    // 국가명(영문)으로 검색
    public Page<CountryCode> searchByNameEng(String nameEng, Pageable pageable) {
        return countryCodeRepository.findByNameEngContaining(nameEng, pageable);
    }

    // 국가 코드로 검색
    public Page<CountryCode> searchByCode(String code, Pageable pageable) {
        return countryCodeRepository.findByCodeContaining(code, pageable);
    }

    // 국가 코드 생성
    @Transactional
    public CountryCode createCountryCode(CountryCode countryCode) {
        // 코드 중복 체크
        if (countryCodeRepository.existsById(countryCode.getCode())) {
            throw new RuntimeException("이미 존재하는 국가 코드입니다: " + countryCode.getCode());
        }
        return countryCodeRepository.save(countryCode);
    }

    // 국가 코드 수정
    @Transactional
    public CountryCode updateCountryCode(String code, CountryCode updatedCountryCode) {
        CountryCode countryCode = countryCodeRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("국가 코드를 찾을 수 없습니다: " + code));

        countryCode.setNameKor(updatedCountryCode.getNameKor());
        countryCode.setNameEng(updatedCountryCode.getNameEng());

        return countryCodeRepository.save(countryCode);
    }

    // 국가 코드 삭제
    @Transactional
    public void deleteCountryCode(String code) {
        if (!countryCodeRepository.existsById(code)) {
            throw new RuntimeException("국가 코드를 찾을 수 없습니다: " + code);
        }
        
        // 해당 국가 코드를 참조하는 도시 코드가 있는지 확인
        List<CityCode> cityCodes = cityCodeRepository.findByCountryList(code);
        if (!cityCodes.isEmpty()) {
            int cityCount = cityCodes.size();
            throw new RuntimeException(
                "이 국가 코드를 참조하는 도시 코드가 " + cityCount + "개 있어 삭제할 수 없습니다.\n" +
                "먼저 해당 도시 코드를 삭제한 후 국가 코드를 삭제해주세요.\n" +
                "도시 코드 관리 페이지에서 국가 코드 '" + code + "'로 검색하여 관련 도시 코드를 확인하세요."
            );
        }
        
        countryCodeRepository.deleteById(code);
    }

    // 국가 코드 존재 여부 확인
    public boolean existsByCode(String code) {
        return countryCodeRepository.existsById(code);
    }
}


