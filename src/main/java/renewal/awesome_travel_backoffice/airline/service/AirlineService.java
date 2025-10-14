package renewal.awesome_travel_backoffice.airline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.airline.repository.AirlineCodeRepository;
import renewal.common.entity.Airline;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirlineService {

    private final AirlineCodeRepository airlineCodeRepository;

    // 모든 항공사 조회 (페이징)
    public Page<Airline> getAllAirlines(Pageable pageable) {
        return airlineCodeRepository.findAllAirlines(pageable);
    }

    // 모든 항공사 조회 (리스트)
    public List<Airline> getAllAirlinesList() {
        return airlineCodeRepository.findAllAirlinesList();
    }

    // 항공사 코드로 조회
    public Optional<Airline> getAirlineByCode(String code) {
        return airlineCodeRepository.findById(code);
    }

    // 항공사명(한글)으로 검색
    public Page<Airline> searchByNameKor(String nameKor, Pageable pageable) {
        return airlineCodeRepository.findByNameKorContaining(nameKor, pageable);
    }

    // 항공사명(영문)으로 검색
    public Page<Airline> searchByNameEng(String nameEng, Pageable pageable) {
        return airlineCodeRepository.findByNameEngContaining(nameEng, pageable);
    }

    // 항공사 코드로 검색
    public Page<Airline> searchByCode(String code, Pageable pageable) {
        return airlineCodeRepository.findByCodeContaining(code, pageable);
    }

    // 항공사 생성
    @Transactional
    public Airline createAirline(Airline airline) {
        // 코드 중복 체크
        if (airlineCodeRepository.existsById(airline.getCode())) {
            throw new RuntimeException("이미 존재하는 항공사 코드입니다: " + airline.getCode());
        }
        return airlineCodeRepository.save(airline);
    }

    // 항공사 수정
    @Transactional
    public Airline updateAirline(String code, Airline updatedAirline) {
        Airline airline = airlineCodeRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("항공사 코드를 찾을 수 없습니다: " + code));

        airline.setNameKor(updatedAirline.getNameKor());
        airline.setNameEng(updatedAirline.getNameEng());
        airline.setInfantSeatsRequired(updatedAirline.isInfantSeatsRequired());

        return airlineCodeRepository.save(airline);
    }

    // 항공사 삭제
    @Transactional
    public void deleteAirline(String code) {
        if (!airlineCodeRepository.existsById(code)) {
            throw new RuntimeException("항공사 코드를 찾을 수 없습니다: " + code);
        }
        airlineCodeRepository.deleteById(code);
    }

    // 항공사 코드 존재 여부 확인
    public boolean existsByCode(String code) {
        return airlineCodeRepository.existsById(code);
    }
}
