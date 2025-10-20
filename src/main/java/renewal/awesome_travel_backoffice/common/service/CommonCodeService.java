package renewal.awesome_travel_backoffice.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import renewal.awesome_travel_backoffice.airport.repository.AirportCodeRepository;
import renewal.awesome_travel_backoffice.airline.repository.AirlineCodeRepository;
import renewal.common.repository.CityCodeRepository;
import renewal.common.repository.CountryCodeRepository;
import renewal.common.entity.Airline;
import renewal.common.entity.AirportCode;
import renewal.common.entity.CityCode;
import renewal.common.entity.CountryCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonCodeService {

    private final AirportCodeRepository airportRepo;
    private final AirlineCodeRepository airlineRepo;
    private final CityCodeRepository cityCodeRepo;
    private final CountryCodeRepository countryCodeRepo;
    
    /** 공항코드 캐싱 */
    @Cacheable("airportCodes")
    public List<AirportCode> getAllAirports() {
        System.out.println(">>> DB 조회: airportCodes"); // 캐시 확인용 로그
        return airportRepo.findAll();
    }

    /** 항공사 캐싱 */
    @Cacheable("airlines")
    public List<Airline> getAllAirlines() {
        System.out.println(">>> DB 조회: airlines");
        return airlineRepo.findAll();
    }

    /** 항공사 캐싱 */
    @Cacheable("cityCodes")
    public List<CityCode> getAllCityCodes() {
        System.out.println(">>> DB 조회: cityCodes");
        return cityCodeRepo.findAll();
    }

    /** 국가코드 캐싱 */
    @Cacheable("countryCodes")
    public List<CountryCode> getAllCountryCodes() {
        System.out.println(">>> DB 조회: countryCodes");
        return countryCodeRepo.findAll();
    }
}
