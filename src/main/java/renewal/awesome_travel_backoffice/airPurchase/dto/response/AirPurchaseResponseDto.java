package renewal.awesome_travel_backoffice.airPurchase.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import renewal.common.entity.BasePurchase.PurchaseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AirPurchaseResponseDto {

    private Long id;

    private AirResponseOneDto airDto; // 기존 AirDto → AirResponseDto 로 변경

    private PurchaseStatus status;

    private Long price;

    private Long member_id;

    private String name;

    private String number;

    private String email;

    private LocalDateTime purchaseDate;

    private LocalDateTime paymentDueDate;

    private List<AirPassengerResponseDto> airPassengers;
}

