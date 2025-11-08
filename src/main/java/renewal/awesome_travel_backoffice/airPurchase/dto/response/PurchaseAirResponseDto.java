package renewal.awesome_travel_backoffice.airPurchase.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import renewal.common.entity.PurchaseBase.PurchaseStatus;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseAirResponseDto {

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

    private int expectedPassengerCount;

    private boolean isPassengerInfoComplete;

    private LocalDateTime passengerInfoDeadline;

    private boolean transactionComplete;

    public PurchaseAirResponseDto(Long id, AirResponseOneDto airDto, PurchaseStatus status, Long price, Long member_id,
            String name, String number, String email, LocalDateTime purchaseDate,
            LocalDateTime paymentDueDate, List<AirPassengerResponseDto> airPassengers,
            boolean isPassengerInfoComplete,
            LocalDateTime passengerInfoDeadline, boolean transactionComplete) {
        this.id = id;
        this.airDto = airDto;
        this.status = status;
        this.price = price;
        this.member_id = member_id;
        this.name = name;
        this.number = number;
        this.email = email;
        this.purchaseDate = purchaseDate;
        this.paymentDueDate = paymentDueDate;
        this.airPassengers = airPassengers;
        // this.expectedPassengerCount = expectedPassengerCount;
        this.isPassengerInfoComplete = isPassengerInfoComplete;
        this.passengerInfoDeadline = passengerInfoDeadline;
        this.transactionComplete = transactionComplete;
    }
}
