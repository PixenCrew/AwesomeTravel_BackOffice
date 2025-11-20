package renewal.awesome_travel_backoffice.purchaseAir.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import renewal.awesome_travel_backoffice.air.repository.AirRepository;
import renewal.awesome_travel_backoffice.air.repository.SeatClassAdminRepository;
import renewal.awesome_travel_backoffice.purchaseAir.dto.request.PurchaseAirSearchCondition;
import renewal.awesome_travel_backoffice.purchaseAir.dto.response.AirResponseOneDto;
import renewal.awesome_travel_backoffice.purchaseAir.dto.response.PurchaseAirResponseDto;
import renewal.awesome_travel_backoffice.purchaseAir.repository.PurchaseAirAdminRepository;
import renewal.awesome_travel_backoffice.purchaseAir.repository.PurchaseAirSpecification;

import org.springframework.data.jpa.domain.Specification;
import renewal.common.service.AirServiceCommon;
import renewal.common.service.PassengerServiceCommon;
import renewal.common.dto.PassengerResponseDto;
import renewal.common.dto.PassengerUpdateRequestDto;
import renewal.common.entity.Air;
import renewal.common.entity.CountryCode;
import renewal.common.entity.Passenger;
import renewal.common.entity.PassengerAir;
import renewal.common.entity.PurchaseAir;
import renewal.common.entity.PurchaseBase.ConfirmedSeatClass;
import renewal.common.entity.PurchaseBase.PurchaseStatus;
import renewal.common.entity.SeatClass;
import renewal.common.repository.CountryCodeRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseAirService {

    private final PurchaseAirAdminRepository airPurchaseAdminRepository;
    private final AirRepository airRepository;
    private final SeatClassAdminRepository seatClassAdminRepository;
    private final AirServiceCommon airServiceCommon;
    private final PassengerServiceCommon passengerServiceCommon;
    private final CountryCodeRepository countryCodeRepository;
    /**
     * 주문 취소 (공통 로직 호출)
     */
    @Transactional
    public void cancelPurchase(Long id) {
        airServiceCommon.cancelPurchase(id);
    }


    /**
     * 어드민 - 전체 항공 예약 목록 조회 (페이징 + 정렬)
     */
    public Page<PurchaseAir> getAllPurchases(PurchaseAirSearchCondition condition, Pageable pageable) {
        Specification<PurchaseAir> spec = buildSpecification(condition);
        return airPurchaseAdminRepository.findAll(spec, pageable);
    }

    /**
     * 어드민 - 전체 항공 예약 목록 조회 (DTO 변환)
     */
    @Transactional(readOnly = true)
    public Page<PurchaseAirResponseDto> getAllPurchasesDto(PurchaseAirSearchCondition condition, Pageable pageable) {
        Specification<PurchaseAir> spec = buildSpecification(condition);
        Page<PurchaseAir> purchases = airPurchaseAdminRepository.findAll(spec, pageable);
        return purchases.map(this::toDto);
    }

    /**
     * 검색 조건을 Specification으로 변환
     */
    private Specification<PurchaseAir> buildSpecification(PurchaseAirSearchCondition condition) {
        Specification<PurchaseAir> spec = Specification.where(null);

        if (condition.getStatus() != null) {
            spec = spec.and(PurchaseAirSpecification.statusEquals(condition.getStatus()));
        }
        if (condition.getName() != null) {
            spec = spec.and(PurchaseAirSpecification.nameContains(condition.getName()));
        }
        if (condition.getEmail() != null) {
            spec = spec.and(PurchaseAirSpecification.emailContains(condition.getEmail()));
        }
        if (condition.getStartDate() != null || condition.getEndDate() != null) {
            spec = spec.and(PurchaseAirSpecification.purchaseDateBetween(
                    condition.getStartDate(), condition.getEndDate()));
        }

        return spec;
    }

    @Transactional(readOnly = true)
    public PurchaseAirDetailView getPurchaseDetail(Long id) {
        PurchaseAir purchase = airPurchaseAdminRepository.findByIdWithPassengers(id)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));

        List<ConfirmedSeatClass> seatSnapshots = airPurchaseAdminRepository.findByIdWithSeatClasses(id)
                .map(PurchaseAir::getFinalSeatClasses)
                .map(ArrayList::new)
                .orElseGet(ArrayList::new);
        purchase.setFinalSeatClasses(seatSnapshots);

        // 완료된 승객 수 계산
        long completedCount = purchase.getPassengers().stream()
                .filter(p -> p.isCompleted())
                .count();

        // ConfirmedSeatClass의 airId로 Air 조회하여 항공사 정보 가져오기
        PurchaseAirResponseDto purchaseDto = toDto(purchase);
        purchaseDto.setCompletedPassengerCount(completedCount);
        if (!seatSnapshots.isEmpty()) {
            ConfirmedSeatClass confirmedSeatClass = seatSnapshots.get(0);
            Air air = airRepository.findById(confirmedSeatClass.getAirId()).orElse(null);
            if (air != null && air.getAirline() != null) {
                purchaseDto.setAirlineCode(air.getAirline().getCode());
                purchaseDto.setAirlineNameKor(air.getAirline().getNameKor());
                purchaseDto.setAirlineNameEng(air.getAirline().getNameEng());
            }
        }

        return PurchaseAirDetailView.builder()
                .purchase(purchaseDto)
                .seatClasses(seatSnapshots)
                .build();
    }

    @Transactional(readOnly = true)
    public List<PassengerResponseDto> getPassengers(Long purchaseId) {
        PurchaseAir purchase = airPurchaseAdminRepository.findByIdWithPassengers(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));
        return purchase.getPassengers().stream()
                .map(passengerServiceCommon::toResponseDto)
                .toList();
    }

    /**
     * 구매 상태 변경 (관리자용)
     * PurchaseAir는 PAID와 CANCELLED 상태만 허용합니다.
     */
    @Transactional
    public void changePurchaseStatus(Long id, PurchaseStatus newStatus) {
        PurchaseAir purchase = airPurchaseAdminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));

        PurchaseStatus currentStatus = purchase.getPurchaseStatus();

        // 동일 상태로의 변경 차단
        if (currentStatus == newStatus) {
            throw new IllegalStateException("이미 해당 상태입니다.");
        }

        // PurchaseAir는 PAID와 CANCELLED만 허용
        if (newStatus != PurchaseStatus.PAID && newStatus != PurchaseStatus.CANCELLED) {
            throw new IllegalArgumentException("항공구매는 PAID 또는 CANCELLED 상태만 허용됩니다.");
        }

        // PAID → CANCELLED 변경 시 공통 취소 로직 사용 (좌석 복구 포함)
        if (currentStatus == PurchaseStatus.PAID && newStatus == PurchaseStatus.CANCELLED) {
            airServiceCommon.cancelPurchase(id);
            return; // cancelPurchase에서 이미 상태 변경과 좌석 복구를 처리하므로 여기서 종료
        }

        // 상태 변경 적용
        purchase.setPurchaseStatus(newStatus);
    }


    /**
     * 내부 변환 메서드 (응답용 DTO로 변환)
     */
    public PurchaseAirResponseDto toDto(PurchaseAir purchase) {
        return PurchaseAirResponseDto.builder()
                .id(purchase.getId())
                .status(purchase.getPurchaseStatus())
                .price(purchase.getPrice())
                .member_id(purchase.getUser() != null ? purchase.getUser().getId() : null)
                .name(purchase.getName())
                .number(purchase.getNumber())
                .email(purchase.getEmail())
                .purchaseDate(purchase.getPurchaseDate())
                .paymentDueDate(purchase.getPaymentDueDate())
                .title(purchase.getTitle())
                .finalPriceAdult(purchase.getFinalPriceAdult())
                .finalPriceYouth(purchase.getFinalPriceYouth())
                .finalPriceInfant(purchase.getFinalPriceInfant())
                .adultCount(purchase.getAdultCount())
                .youthCount(purchase.getYouthCount())
                .infantCount(purchase.getInfantCount())
                .isPassengerInfoComplete(purchase.getIsPassengerInfoComplete())
                .passengerInfoDeadline(purchase.getPassengerInfoDeadline())
                .transactionComplete(purchase.getIsTransactionComplete() != null ? purchase.getIsTransactionComplete() : false)
                .build();
    }

    /**
     * 탑승객 정보 추가 (관리자용)
     */
    @Transactional
    public void addPassengerInfo(Long purchaseId, PassengerUpdateRequestDto passengerDto) {
        PurchaseAir purchase = airPurchaseAdminRepository.findByIdWithPassengers(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));

        if (purchase.getPassengerInfoDeadline() != null &&
                LocalDateTime.now().isAfter(purchase.getPassengerInfoDeadline())) {
            throw new IllegalStateException("승객 정보 입력 마감일이 지났습니다.");
        }

        passengerServiceCommon.validateRequiredFields(passengerDto);

        Passenger emptyPassenger = purchase.getPassengers().stream()
                .filter(passengerServiceCommon::isPassengerSlotEmpty)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("추가 가능한 승객 슬롯이 없습니다."));

        passengerServiceCommon.applyPassengerInfo(emptyPassenger, passengerDto);
        purchase.setIsPassengerInfoComplete(
                passengerServiceCommon.isPassengerInfoComplete(purchase.getPassengers()));
    }

    /**
     * 탑승객 정보 수정 (관리자용)
     */
    @Transactional
    public void updatePassenger(Long purchaseId, Long passengerId, PassengerUpdateRequestDto updateRequest) {
        PurchaseAir purchase = airPurchaseAdminRepository.findByIdWithPassengers(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));

        // 승객 정보 조회
        Passenger passenger = purchase.getPassengers().stream()
                .filter(p -> p.getId().equals(passengerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("승객 정보를 찾을 수 없습니다."));

        // 승객 정보 검증
        passengerServiceCommon.validateRequiredFields(updateRequest);

        // 승객 정보 업데이트
        passengerServiceCommon.applyPassengerInfo(passenger, updateRequest);

        // 승객 정보 완료 상태 재확인
        purchase.setIsPassengerInfoComplete(
                passengerServiceCommon.isPassengerInfoComplete(purchase.getPassengers()));
    }

    private long getSafeCount(Long value) {
        return value != null ? value : 0L;
    }

    

    /**
     * 승객 수 수정 (관리자용)
     */
    @Transactional
    public void updatePassengerCount(Long purchaseId, Long adultCount, Long youthCount, Long infantCount) {
        PurchaseAir purchase = airPurchaseAdminRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));

        if (adultCount == null || adultCount < 0) {
            throw new IllegalArgumentException("성인 수는 0 이상이어야 합니다.");
        }
        if (youthCount == null || youthCount < 0) {
            throw new IllegalArgumentException("청소년 수는 0 이상이어야 합니다.");
        }
        if (infantCount == null || infantCount < 0) {
            throw new IllegalArgumentException("영유아 수는 0 이상이어야 합니다.");
        }

        Long totalCount = adultCount + youthCount + infantCount;
        Long currentPassengerCount = purchase.getPassengers() != null ? (long) purchase.getPassengers().size() : 0L;

        // 승객 수가 줄어드는 경우, 기존 승객 정보를 삭제할 수 없으므로 경고
        if (totalCount < currentPassengerCount) {
            throw new IllegalStateException(
                    String.format("현재 등록된 승객 수(%d명)보다 적은 수(%d명)로 변경할 수 없습니다. 먼저 승객 정보를 삭제해주세요.", 
                            currentPassengerCount, totalCount));
        }

        // 승객 수 업데이트
        purchase.setAdultCount(adultCount);
        purchase.setYouthCount(youthCount);
        purchase.setInfantCount(infantCount);

        // 승객 슬롯이 부족한 경우 빈 승객 객체 추가
        if (totalCount > currentPassengerCount) {
            int additionalSlots = (int) (totalCount - currentPassengerCount);
            for (int i = 0; i < additionalSlots; i++) {
                PassengerAir newPassenger = new PassengerAir();
                purchase.getPassengers().add(newPassenger);
            }
        }

        // 승객 정보 완료 상태 재확인
        purchase.setIsPassengerInfoComplete(
                passengerServiceCommon.isPassengerInfoComplete(purchase.getPassengers()));
    }

    /**
     * 국적 코드 목록 조회 (검색 기능 포함)
     */
    public List<CountryCodeDto> getCountries(String search) {
        System.out.println("=== PurchaseAir getCountries 호출됨 ===");
        System.out.println("검색어: " + search);

        try {
            List<CountryCode> countries = countryCodeRepository.findAll();
            System.out.println("전체 국가 수: " + countries.size());

            if (search != null && !search.trim().isEmpty()) {
                // 검색어가 있는 경우: 코드나 한글명, 영문명으로 검색
                String searchTerm = search.trim().toLowerCase();
                countries = countries.stream()
                        .filter(country -> country.getCode().toLowerCase().contains(searchTerm) ||
                                (country.getNameKor() != null
                                        && country.getNameKor().toLowerCase().contains(searchTerm))
                                ||
                                (country.getNameEng() != null
                                        && country.getNameEng().toLowerCase().contains(searchTerm)))
                        .limit(20) // 최대 20개까지만 반환
                        .collect(java.util.stream.Collectors.toList());
            } else {
                // 검색어가 없는 경우: 주요 국가들만
                countries = countries.stream()
                        .limit(10) // 최대 10개까지만 반환
                        .collect(java.util.stream.Collectors.toList());
            }

            List<CountryCodeDto> result = countries.stream()
                    .map(country -> new CountryCodeDto(
                            country.getCode(),
                            country.getNameKor(),
                            country.getNameEng()))
                    .collect(java.util.stream.Collectors.toList());

            System.out.println("반환할 국가 수: " + result.size());
            return result;

        } catch (Exception e) {
            System.err.println("국가 조회 오류: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    // 국적 코드 DTO
    public static class CountryCodeDto {
        private String code;
        private String nameKor;
        private String nameEng;

        public CountryCodeDto(String code, String nameKor, String nameEng) {
            this.code = code;
            this.nameKor = nameKor;
            this.nameEng = nameEng;
        }

        // Getters
        public String getCode() {
            return code;
        }

        public String getNameKor() {
            return nameKor;
        }

        public String getNameEng() {
            return nameEng;
        }
    }

    @Getter
    @Builder
    public static class PurchaseAirDetailView {
        private final PurchaseAirResponseDto purchase;
        private final List<ConfirmedSeatClass> seatClasses;
    }

}
