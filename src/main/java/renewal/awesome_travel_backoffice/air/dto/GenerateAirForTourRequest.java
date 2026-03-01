package renewal.awesome_travel_backoffice.air.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 투어 조건에 맞는 항공 일괄 생성 시 옵션.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateAirForTourRequest {

    /** 사용할 항공사 코드 목록 (복수 선택). 비어있으면 첫 번째 항공사만 사용. 생성 시 순서대로 번갈아 적용 */
    private List<String> airlineCodes;

    /** 출발 시 (0~23) */
    @Builder.Default
    private int departHour = 9;

    /** 출발 분 (0~59) */
    @Builder.Default
    private int departMinute = 0;

    /** 비행 소요 시간(분). 기본 12시간 = 720 */
    @Builder.Default
    private long flightDurationMinutes = 720L;

    /** 기본 성인 가격(원). 등급별 미지정 시 사용 */
    @Builder.Default
    private long defaultPriceAdult = 500_000L;

    /** 기본 최대 좌석 수. 등급별 미지정 시 사용 */
    @Builder.Default
    private long defaultMaxSeats = 30L;

    /** 이코노미 성인 가격(원). null/0이면 defaultPriceAdult 사용 */
    private Long priceEconomy;
    /** 프리미엄 이코노미 성인 가격(원). null/0이면 defaultPriceAdult 사용 */
    private Long pricePremiumEconomy;
    /** 비즈니스 성인 가격(원). null/0이면 defaultPriceAdult 사용 */
    private Long priceBusiness;
    /** 퍼스트 성인 가격(원). null/0이면 defaultPriceAdult 사용 */
    private Long priceFirst;

    /** 이코노미 최대 좌석 수. null/0이면 defaultMaxSeats 사용 */
    private Long maxSeatsEconomy;
    /** 프리미엄 이코노미 최대 좌석 수 */
    private Long maxSeatsPremiumEconomy;
    /** 비즈니스 최대 좌석 수 */
    private Long maxSeatsBusiness;
    /** 퍼스트 최대 좌석 수 */
    private Long maxSeatsFirst;
}
