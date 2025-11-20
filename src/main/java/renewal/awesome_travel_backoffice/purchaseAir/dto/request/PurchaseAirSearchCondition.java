package renewal.awesome_travel_backoffice.purchaseAir.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import renewal.common.entity.PurchaseBase.PurchaseStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseAirSearchCondition {

    private PurchaseStatus status;        // 상태 필터 (예: HOLDING, PAID)

    private String name;                  // 예약자 이름 (부분 검색)

    private String email;                 // 예약자 이메일 (정확 or 부분)

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;         // 예약일 시작

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;           // 예약일 종료
}
