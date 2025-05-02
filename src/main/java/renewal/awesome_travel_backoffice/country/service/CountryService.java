package renewal.awesome_travel_backoffice.country.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.country.dto.CountryDto;
import renewal.awesome_travel_backoffice.country.entity.Country;
import renewal.awesome_travel_backoffice.country.repository.CountryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public List<CountryDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(c -> new CountryDto(
                        c.getCountryCode(),
                        c.getCountryName(),
                        c.getCountryNameLocal()
                ))
                .toList();
    }

    @Transactional
    public void createCountry(CountryDto dto) {
        if (countryRepository.existsById(dto.getCountryCode())) {
            throw new IllegalArgumentException("이미 존재하는 국가 코드입니다.");
        }
        Country country = new Country(dto.getCountryCode(), dto.getCountryName(), dto.getCountryNameLocal());
        countryRepository.save(country);
    }

    @Transactional
    public void updateCountry(String countryCode, CountryDto dto) {
        Country country = countryRepository.findById(countryCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 국가입니다."));
        country.updateCountry(dto.getCountryName(), dto.getCountryNameLocal());
    }

    @Transactional
    public void deleteCountry(String countryCode) {
        countryRepository.deleteById(countryCode);
    }
}
