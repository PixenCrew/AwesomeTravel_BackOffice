package renewal.awesome_travel_backoffice.airPurchase.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.air.dto.response.AirResponseDto;
import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.air.entity.SeatClass;
import renewal.awesome_travel_backoffice.airPurchase.dto.request.AirPurchaseSearchCondition;
import renewal.awesome_travel_backoffice.airPurchase.dto.response.AirPassengerResponseDto;
import renewal.awesome_travel_backoffice.airPurchase.dto.response.AirPurchaseResponseDto;
import renewal.awesome_travel_backoffice.airPurchase.dto.response.AirResponseOneDto;
import renewal.awesome_travel_backoffice.airPurchase.entity.AirPurchase;
import renewal.awesome_travel_backoffice.airPurchase.repository.AirPurchaseRepository;
import renewal.awesome_travel_backoffice.airPurchase.utiles.PurchaseStatus;
import renewal.awesome_travel_backoffice.specialRequest.entity.SpecialRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AirPurchaseService {

    private final AirPurchaseRepository airPurchaseRepository;

    /**
     *  어드민 - 전체 항공 예약 목록 조회 (페이징 + 정렬)
     */
    public Page<AirPurchaseResponseDto> getAllPurchases(AirPurchaseSearchCondition condition, Pageable pageable) {
        return airPurchaseRepository.searchByCondition(condition, pageable)
                .map(this::toDto);
    }


    /**
     *  어드민 - 단건 예약 상세 조회
     */
    public AirPurchaseResponseDto getPurchase(Long id) {
        AirPurchase purchase = airPurchaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));
        return toDto(purchase);
    }

    // 관리자용 - 상태 변경
    @Transactional
    public void changePurchaseStatus(Long id, PurchaseStatus newStatus) {
        AirPurchase purchase = airPurchaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));

        PurchaseStatus currentStatus = purchase.getPurchaseStatus();

        // 동일 상태로의 변경 차단
        if (currentStatus == newStatus) {
            throw new IllegalStateException("이미 해당 상태입니다.");
        }

        // PAID → CANCELLED 같은 위험한 전이 차단 (선택)
        if (currentStatus == PurchaseStatus.PAID && newStatus == PurchaseStatus.CANCELLED) {
            throw new IllegalStateException("결제 완료된 예약은 취소할 수 없습니다.");
        }

        // CANCELLED → PAID 같은 재확정도 차단 (선택)
        if (currentStatus == PurchaseStatus.CANCELLED && newStatus == PurchaseStatus.PAID) {
            throw new IllegalStateException("취소된 예약은 다시 확정할 수 없습니다.");
        }

        // 좌석 수 복구: HOLDING or PAID → CANCELLED
        if ((currentStatus == PurchaseStatus.HOLDING || currentStatus == PurchaseStatus.PAID)
                && newStatus == PurchaseStatus.CANCELLED) {
            SeatClass seatClass = purchase.getSeatClass();
            seatClass.increaseAvailableSeats(purchase.getAirPassengers().size());
        }

        // 상태 변경 적용
        purchase.setPurchaseStatus(newStatus);

        // 로그 (예: 실제로는 DB에 남기거나 파일에 기록 가능)
        System.out.printf("[관리자] 예약 상태 변경: ID=%d | %s → %s | 시간=%s\n",
                purchase.getId(), currentStatus, newStatus, LocalDateTime.now());
    }

    /**
     *  내부 변환 메서드 (응답용 DTO로 변환)
     */
    private AirPurchaseResponseDto toDto(AirPurchase purchase) {
        SeatClass seatClass = purchase.getSeatClass();
        Air air = seatClass.getAir();

        //유저기능과 달리 AirResponseDto의 구조가 다르기 떄문에 AirResponseOneDto로 변경해서 보여줌
        //어드민에서의 AirResponse는 항공하나의 리턴이 아닌 seatclass전체를 리턴하기 때문에 차이가 있음
        AirResponseOneDto airDto = AirResponseOneDto.builder()
                .airId(air.getId())
                .code(air.getCode())
                .airlineCode(air.getAirline().getCode())
                .airlineNameKor(air.getAirline().getNameKor())
                .airlineNameEng(air.getAirline().getNameEng())
                .depart(air.getDepart())
                .arrive(air.getArrive())
                .departTime(air.getDepart_time())
                .arriveTime(air.getArrive_time())
                .stopovers(air.getStopovers())
                .flightType(air.getFlightType())
                .seatClassId(seatClass.getId())
                .seatClassType(seatClass.getClassType())
                .price(seatClass.getPrice())
                .availableSeats(seatClass.getAvailableSeats())
                .build();

        List<AirPassengerResponseDto> passengerDtos = purchase.getAirPassengers().stream()
                .map(passenger -> {
                    List<String> requestList = passenger.getSpecialRequests().stream()
                            .map(SpecialRequest::getRequestType)
                            .toList();
                    return new AirPassengerResponseDto(
                            passenger.getName(),
                            passenger.getNumber(),
                            passenger.getEmail(),
                            passenger.getBirth(),
                            passenger.getSex().name(),
                            passenger.getNationality().getCountryCode(),
                            passenger.getPassport_num(),
                            passenger.getLastName(),
                            passenger.getFirstName(),
                            passenger.getExpire(),
                            requestList
                    );
                }).toList();

        return new AirPurchaseResponseDto(
                purchase.getId(),
                airDto,
                purchase.getPurchaseStatus(),
                purchase.getPrice(),
                purchase.getMember_id(),
                purchase.getName(),
                purchase.getNumber(),
                purchase.getEmail(),
                purchase.getPurchaseDate(),
                purchase.getPaymentDueDate(),
                passengerDtos
        );
    }
}

