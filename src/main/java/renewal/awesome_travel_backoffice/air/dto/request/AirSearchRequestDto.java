package renewal.awesome_travel_backoffice.air.dto.request;

import lombok.Getter;
import lombok.Setter;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;

@Getter
@Setter
public class AirSearchRequestDto {

    private String airline;        // 항공사 코드 또는 이름 (단건)
    private String depart;         // 출발지 코드
    private String arrive;         // 도착지 코드
    private String departDateFrom; // 출발 시간 시작 (문자열 형태로 받음)
    private String departDateTo;   // 출발 시간 끝

    private AirStatus status;      // 항공편 상태 (ACTIVE, INACTIVE 등)

    private String sortField;      // 정렬 기준 (ex: "departTime", "airline", "stopovers")
    private String sortOrder;      // 정렬 방향 ("asc" / "desc")

    private int page = 0;          // 페이지 번호 (0부터 시작)
    private int size = 10;         // 페이지 크기
}
