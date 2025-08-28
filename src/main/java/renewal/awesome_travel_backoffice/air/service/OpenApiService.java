package renewal.awesome_travel_backoffice.air.service;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenApiService {

    public void fetchAndSaveData() throws UnsupportedEncodingException {

        String SERVICE_KEY = "LlzmSnhpn+oy0xRuNRMG8B7zoQQFfiaIkqMpw6ZfXw1HGSiYmOUPwwNSq1mayIkaQED3E5GUYYzHGX92CVXYgQ==".trim();
        String encodedKey = URLEncoder.encode(SERVICE_KEY, "UTF-8");
        System.out.println("미리 인코딩된 키: " + encodedKey); // 미리 인코딩된 결과 확인

        URI uri = UriComponentsBuilder
                .fromUriString("https://api.odcloud.kr/api/15043890/v1/uddi:57dcf102-1447-49e9-bd2b-cfb32e869d5c")
                .queryParam("page", "1")
                .queryParam("perPage", "10")
                .queryParam("serviceKey", encodedKey)  // 여기서는 trim() 후, RestTemplate이 자동 인코딩하도록 함
                .build(true) // true: 이미 인코딩된 값이라고 가정하면 false로 설정
                .toUri();

        System.out.println("최종 uri : " + uri);

        // try {
        //     ApiResponse apiResponse = restTemplate.getForObject(uri, ApiResponse.class);
        //     if (apiResponse != null && apiResponse.getData() != null) {
        //         List<FlightItem> items = apiResponse.getData();
        //         for (FlightItem item : items) {
        //             // Airline 매핑
        //             Airline airline = airlineRepository.findByNameEng(item.getAirline())
        //                     .orElse(airline = new Airline(item.getAirline()));
        //             // DB 저장을 위한 엔티티 변환
        //             Air air = new Air(
        //                     item.getFlightNumber(),
        //                     airline,
        //                     item.getDepartureAirport(),
        //                     item.getStartDate(),
        //                     item.getDepartureTime(),
        //                     item.getArrivalAirport(),
        //                     item.getEndDate(),
        //                     item.getArrivalTime()
        //             );
        //             airRepository.save(air);
        //         }
        //     }
        // } catch (Exception e) {
        //     // 예외 처리: 로그 남기기 등
        //     e.printStackTrace();
        // }
    }
}
