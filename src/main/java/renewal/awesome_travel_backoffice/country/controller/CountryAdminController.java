package renewal.awesome_travel_backoffice.country.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.country.dto.CountryDto;
import renewal.awesome_travel_backoffice.country.service.CountryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/countries")
public class CountryAdminController {

    private final CountryService countryService;

    // 관리자용 전체 조회
    @GetMapping
    public ResponseEntity<List<CountryDto>> getAllForAdmin() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }

    // 등록
    @PostMapping
    public ResponseEntity<String> create(@RequestBody CountryDto dto) {
        countryService.createCountry(dto);
        return ResponseEntity.ok(dto.getCountryCode());
    }

    // 수정
    @PutMapping("/{countryCode}")
    public ResponseEntity<Void> update(@PathVariable String countryCode, @RequestBody CountryDto dto) {
        countryService.updateCountry(countryCode, dto);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{countryCode}")
    public ResponseEntity<Void> delete(@PathVariable String countryCode) {
        countryService.deleteCountry(countryCode);
        return ResponseEntity.noContent().build();
    }
}
