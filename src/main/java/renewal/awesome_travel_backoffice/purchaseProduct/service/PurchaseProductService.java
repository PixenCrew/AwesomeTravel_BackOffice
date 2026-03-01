package renewal.awesome_travel_backoffice.purchaseProduct.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.purchaseProduct.controller.PurchaseProductAdminController.CountryCodeDto;
import renewal.awesome_travel_backoffice.purchaseProduct.dto.request.PurchaseProductSearchCondition;
import renewal.awesome_travel_backoffice.purchaseProduct.dto.response.ProductResponseDto;
import renewal.awesome_travel_backoffice.purchaseProduct.dto.response.PurchaseProductResponseDto;
import renewal.common.repository.PurchaseProductRepository;
import renewal.awesome_travel_backoffice.purchaseProduct.repository.PurchaseProductAdminRepository;
import renewal.awesome_travel_backoffice.purchaseProduct.repository.PurchaseProductSpecification;
import org.springframework.data.jpa.domain.Specification;
import renewal.common.dto.PassengerResponseDto;
import renewal.common.dto.PassengerUpdateRequestDto;
import renewal.common.entity.CountryCode;
import renewal.common.entity.Passenger;
import renewal.common.entity.PassengerProduct;
import renewal.common.entity.PurchaseBase.PurchaseStatus;
import renewal.common.entity.PurchaseProduct;
import renewal.common.repository.CountryCodeRepository;
import renewal.common.service.PassengerServiceCommon;
import renewal.common.service.ProductServiceCommon;

@Service
@RequiredArgsConstructor
public class PurchaseProductService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseProductService.class);

    private final PurchaseProductRepository productPurchaseRepository;
    private final PurchaseProductAdminRepository purchaseProductAdminRepository;
    private final CountryCodeRepository countryCodeRepository;
    private final ProductServiceCommon productServiceCommon;
    private final PassengerServiceCommon passengerServiceCommon;

    /**
     * 어드민 - 전체 패키지 상품 구매 목록 조회 (페이징 + 정렬)
     */
    public Page<PurchaseProduct> getAllPurchases(PurchaseProductSearchCondition condition, Pageable pageable) {
        Specification<PurchaseProduct> spec = buildSpecification(condition);
        return purchaseProductAdminRepository.findAll(spec, pageable);
    }

    /**
     * 어드민 - 전체 패키지 상품 구매 목록 조회 (DTO 변환)
     */
    public Page<PurchaseProductResponseDto> getAllPurchasesDto(PurchaseProductSearchCondition condition,
            Pageable pageable) {
        Specification<PurchaseProduct> spec = buildSpecification(condition);
        Page<PurchaseProduct> purchases = purchaseProductAdminRepository.findAll(spec, pageable);
        return purchases.map(this::toDto);
    }

    /**
     * 검색 조건을 Specification으로 변환
     */
    private Specification<PurchaseProduct> buildSpecification(PurchaseProductSearchCondition condition) {
        Specification<PurchaseProduct> spec = Specification.where(null);

        if (condition.getProductPurchaseId() != null) {
            spec = spec.and(PurchaseProductSpecification.productPurchaseIdEquals(condition.getProductPurchaseId()));
        }
        if (condition.getPurchaseStatus() != null) {
            spec = spec.and(PurchaseProductSpecification.purchaseStatusEquals(condition.getPurchaseStatus()));
        }
        if (condition.getMemberId() != null) {
            spec = spec.and(PurchaseProductSpecification.memberIdEquals(condition.getMemberId()));
        }
        if (condition.getProductId() != null) {
            spec = spec.and(PurchaseProductSpecification.productIdEquals(condition.getProductId()));
        }
        if (condition.getPurchaseDateFrom() != null || condition.getPurchaseDateTo() != null) {
            spec = spec.and(PurchaseProductSpecification.purchaseDateBetween(
                    condition.getPurchaseDateFrom(), condition.getPurchaseDateTo()));
        }
        if (condition.getMinPrice() != null || condition.getMaxPrice() != null) {
            spec = spec.and(PurchaseProductSpecification.priceBetween(
                    condition.getMinPrice(), condition.getMaxPrice()));
        }
        if (condition.getCustomerName() != null) {
            spec = spec.and(PurchaseProductSpecification.customerNameContains(condition.getCustomerName()));
        }
        if (condition.getCustomerEmail() != null) {
            spec = spec.and(PurchaseProductSpecification.customerEmailContains(condition.getCustomerEmail()));
        }
        if (condition.getProductTitle() != null) {
            spec = spec.and(PurchaseProductSpecification.productTitleContains(condition.getProductTitle()));
        }

        return spec;
    }

    /**
     * 어드민 - 단건 구매 상세 조회
     */
    @Transactional(readOnly = true)
    public PurchaseProduct getPurchase(Long id) {
        PurchaseProduct purchase = productPurchaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));
        if (purchase.getPassengers() != null) {
            purchase.getPassengers().size();
        }
        if (purchase.getFinalSeatClasses() != null) {
            purchase.getFinalSeatClasses().size();
        }
        return purchase;
    }

    /**
     * 어드민 - 단건 구매 상세 조회 (DTO 변환)
     */
    public PurchaseProductResponseDto getPurchaseDto(Long id) {
        PurchaseProduct purchase = purchaseProductAdminRepository.findByIdWithPassengers(id)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));

        List<renewal.common.entity.PurchaseBase.ConfirmedSeatClass> seatSnapshots = purchaseProductAdminRepository.findByIdWithSeatClasses(id)
                .map(PurchaseProduct::getFinalSeatClasses)
                .map(ArrayList::new)
                .orElseGet(ArrayList::new);
        purchase.setFinalSeatClasses(seatSnapshots);

        if (log.isDebugEnabled()) {
            log.debug("PurchaseProduct 상세: id={}, passengers={}, passengerInfoComplete={}, transactionComplete={}",
                    purchase.getId(),
                    purchase.getPassengers() != null ? purchase.getPassengers().size() : 0,
                    purchase.getIsPassengerInfoComplete(),
                    purchase.getIsTransactionComplete());
        }

        return toDto(purchase);
    }

    /**
     * 승객 목록 조회 (승객 페이지용)
     */
    @Transactional(readOnly = true)
    public List<renewal.common.dto.PassengerResponseDto> getPassengers(Long purchaseId) {
        PurchaseProduct purchase = purchaseProductAdminRepository.findByIdWithPassengers(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));
        return purchase.getPassengers().stream()
                .map(passengerServiceCommon::toResponseDto)
                .toList();
    }

    /**
     * 주문 취소 (공통 로직 호출)
     */
    @Transactional
    public void cancelPurchase(Long id) {
        productServiceCommon.cancelPurchase(id);
    }

    // 관리자용 - 상태 변경
    @Transactional
    public void changePurchaseStatus(Long id, PurchaseStatus newStatus) {
        PurchaseProduct purchase = productPurchaseRepository.findById(id)
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

        // 상태 변경 적용
        purchase.setPurchaseStatus(newStatus);

        log.info("[관리자] 패키지 상품 구매 상태 변경: id={}, {} → {}", purchase.getId(), currentStatus, newStatus);
    }

    /**
     * 2단계: 승객 정보 추가
     */
    @Transactional
    public void addPassengerInfo(Long purchaseId, PassengerUpdateRequestDto passengerDto) {
        PurchaseProduct purchase = productPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));

        // 마감일 확인
        if (purchase.getPassengerInfoDeadline() != null &&
                LocalDateTime.now().isAfter(purchase.getPassengerInfoDeadline())) {
            throw new IllegalStateException("승객 정보 입력 마감일이 지났습니다.");
        }

        // 승객 정보 검증
        passengerServiceCommon.validateRequiredFields(passengerDto);

        // 빈 승객 객체 찾기 (이름이 null이거나 비어있는 경우)
        Passenger emptyPassenger = purchase.getPassengers().stream()
                .filter(passengerServiceCommon::isPassengerSlotEmpty)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("더 이상 승객 정보를 추가할 수 없습니다."));

        // 승객 정보 설정
        passengerServiceCommon.applyPassengerInfo(emptyPassenger, passengerDto);

        // 승객 정보 완료 상태 확인
        refreshPassengerInfoComplete(purchase);

        productPurchaseRepository.save(purchase);

        log.info("[2단계] 패키지 상품 승객 정보 추가: purchaseId={}, 승객명={}", purchaseId, passengerServiceCommon.buildKoreanName(emptyPassenger));
    }

    /**
     * 결제 완료 처리 (패키지 상품)
     * 패키지 상품의 경우 인원수에 맞게 결제를 먼저 진행합니다.
     * 승객 정보가 완료되지 않으면 나중에 취소될 수 있습니다.
     */
    @Transactional
    public void completePayment(Long purchaseId) {
        PurchaseProduct purchase = productPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));

        // 결제 완료 상태로 변경 (승객 정보 완료 여부와 관계없이)
        purchase.setPurchaseStatus(PurchaseStatus.PAID);

        // // 승객 정보가 완료되지 않은 경우 경고 로그
        // if (!purchase.getIsPassengerInfoComplete()) {
        // System.out.printf("[경고] 패키지 상품 결제 완료 - 승객 정보 미완료: 구매ID=%d, 예상승객수=%d,
        // 현재승객수=%d\n",
        // purchaseId, purchase.getExpectedPassengerCount(),
        // purchase.getPassengers().size());
        // }

        productPurchaseRepository.save(purchase);
    }

    /**
     * 승객 정보 마감일 체크 및 취소 처리 (패키지 상품)
     * 마감일이 지났는데 승객 정보가 완료되지 않은 경우 취소 처리
     */
    @Transactional
    public void checkPassengerInfoDeadline() {
        LocalDateTime now = LocalDateTime.now();

        // 마감일이 지났는데 승객 정보가 완료되지 않은 패키지 상품 조회
        List<PurchaseProduct> expiredPurchases = productPurchaseRepository.findAll().stream()
                .filter(purchase -> purchase.getPassengerInfoDeadline() != null)
                .filter(purchase -> now.isAfter(purchase.getPassengerInfoDeadline()))
                .filter(purchase -> !purchase.getIsPassengerInfoComplete())
                .filter(purchase -> purchase.getPurchaseStatus() == PurchaseStatus.PAID)
                .collect(Collectors.toList());

        for (PurchaseProduct purchase : expiredPurchases) {
            // 결제 완료 상태에서 취소로 변경
            purchase.setPurchaseStatus(PurchaseStatus.CANCELLED);
            productPurchaseRepository.save(purchase);

            log.info("[자동취소] 패키지 상품 승객 정보 마감일 초과: purchaseId={}, 마감일={}", purchase.getId(), purchase.getPassengerInfoDeadline());
        }
    }

    /**
     * 승객 정보 완료 상태 확인
     */
    private void refreshPassengerInfoComplete(PurchaseProduct purchase) {
        purchase.setIsPassengerInfoComplete(
                passengerServiceCommon.isPassengerInfoComplete(purchase.getPassengers()));
    }

    /**
     * 승객 수 수정 (관리자용)
     */
    @Transactional
    public void updatePassengerCount(Long purchaseId, Long adultCount, Long youthCount, Long infantCount) {
        PurchaseProduct purchase = productPurchaseRepository.findById(purchaseId)
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
                PassengerProduct newPassenger = new PassengerProduct();
                purchase.getPassengers().add(newPassenger);
            }
        }
        refreshPassengerInfoComplete(purchase);
        productPurchaseRepository.save(purchase);
    }

    /**
     * 승객 정보 수정 (관리자용)
     */
    @Transactional
    public void updatePassenger(Long purchaseId, Long passengerId, PassengerUpdateRequestDto updateRequest) {
        // 구매 정보 조회
        PurchaseProduct purchase = productPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));

        // 승객 정보 조회
        Passenger passenger = purchase.getPassengers().stream()
                .filter(p -> p.getId().equals(passengerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("승객 정보를 찾을 수 없습니다."));

        // 디버깅 로그
        passengerServiceCommon.validateRequiredFields(updateRequest);
        passengerServiceCommon.applyPassengerInfo(passenger, updateRequest);

        // 승객 정보 완료 상태 재확인
        refreshPassengerInfoComplete(purchase);

        log.info("[관리자] 패키지 상품 승객 정보 수정: purchaseId={}, passengerId={}, 승객명={}", purchaseId, passengerId, passengerServiceCommon.buildKoreanName(passenger));
    }

    /**
     * 내부 변환 메서드 (응답용 DTO로 변환)
     */
    public PurchaseProductResponseDto toDto(PurchaseProduct purchase) {
        // 상품 정보 DTO 생성 (Product가 없을 수 있음 - 임시 데이터 생성 중일 때)
        ProductResponseDto productDto = null;
        if (purchase.getProduct() != null) {
            renewal.common.entity.Product product = purchase.getProduct();
            renewal.common.entity.Tour tour = product.getTour();
            
            productDto = ProductResponseDto.builder()
                    .productId(product.getId())
                    .title(product.getTitle())
                    .price(product.getPrice())
                    .country(tour != null && tour.getCountry() != null
                            ? tour.getCountry().getCountryKor()
                            : "")
                    .city(tour != null ? tour.getName() : "") // city 대신 name 사용
                    .startDate(tour != null && tour.getStartDate() != null 
                            ? tour.getStartDate().toString() 
                            : null)
                    .endDate(tour != null && tour.getEndDate() != null 
                            ? tour.getEndDate().toString() 
                            : null)
                    .duration(calculateDuration(
                            tour != null ? tour.getStartDate() : null,
                            tour != null ? tour.getEndDate() : null))
                    .averageRating(product.getAverageRating())
                    .totalReviews(product.getTotalReviews())
                    .totalCapacity(tour != null ? tour.getCount() : null)
                    .remainingCapacity(calculateRemainingCapacity(product))
                    .build();
        } else {
            // Product가 없는 경우 기본값으로 DTO 생성
            productDto = ProductResponseDto.builder()
                    .productId(null)
                    .title(purchase.getTitle() != null ? purchase.getTitle() : "상품 정보 없음")
                    .price(purchase.getPrice())
                    .country("")
                    .city("")
                    .startDate(null)
                    .endDate(null)
                    .duration(null)
                    .averageRating(0.0)
                    .totalReviews(0L)
                    .totalCapacity(null)
                    .remainingCapacity(0L)
                    .build();
        }

        // 승객 정보 DTO 생성
        List<PassengerResponseDto> passengerDtos = purchase.getPassengers() != null
                ? purchase.getPassengers().stream()
                        .map(passengerServiceCommon::toResponseDto)
                        .toList()
                : java.util.Collections.emptyList();

        long actualPassengerCount = passengerDtos.size();
        long expectedPassengerCount = getSafeCount(purchase.getAdultCount())
                + getSafeCount(purchase.getYouthCount())
                + getSafeCount(purchase.getInfantCount());
        long passengerEmptyCount = expectedPassengerCount > actualPassengerCount
                ? expectedPassengerCount - actualPassengerCount
                : 0;
        long passengerDisplayCount = expectedPassengerCount > 0 ? expectedPassengerCount : actualPassengerCount;
        
        // 완료된 승객 수 계산
        long completedCount = purchase.getPassengers() != null
                ? purchase.getPassengers().stream()
                        .filter(p -> p.isCompleted())
                        .count()
                : 0L;

        return PurchaseProductResponseDto.builder()
                .productPurchaseId(purchase.getId())
                .product(productDto)
                .purchaseStatus(purchase.getPurchaseStatus().name())
                .price(purchase.getPrice())
                .memberId(purchase.getUser() != null ? purchase.getUser().getId() : null)
                .name(purchase.getName())
                .number(purchase.getNumber())
                .email(purchase.getEmail())
                .purchaseDate(purchase.getPurchaseDate())
                .paymentDueDate(purchase.getPaymentDueDate())
                .passengers(passengerDtos)
                // PurchaseBase 필드들
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
                .completedPassengerCount(completedCount)
                .passengerActualCount(actualPassengerCount)
                .passengerExpectedCount(expectedPassengerCount)
                .passengerEmptyCount(passengerEmptyCount)
                .passengerDisplayCount(passengerDisplayCount)
                // PurchaseProduct 전용 필드들
                .airlineCode(purchase.getAirline() != null ? purchase.getAirline().getCode() : null)
                .airlineNameKor(purchase.getAirline() != null ? purchase.getAirline().getNameKor() : null)
                .airlineNameEng(purchase.getAirline() != null ? purchase.getAirline().getNameEng() : null)
                .departDateTime(purchase.getDepartDateTime())
                .returnDateTime(purchase.getReturnDateTime())
                .bak(purchase.getBak())
                .il(purchase.getIl())
                .handlerId(purchase.getHandler() != null ? purchase.getHandler().getId() : null)
                .handlerName(purchase.getHandler() != null ? purchase.getHandler().getName() : null)
                .handlerPosition(purchase.getHandler() != null ? purchase.getHandler().getPosition() : null)
                .handlerEmail(purchase.getHandler() != null ? purchase.getHandler().getEmail() : null)
                .handlerNumber(purchase.getHandler() != null ? purchase.getHandler().getNumber() : null)
                .handlerFax(purchase.getHandler() != null ? purchase.getHandler().getFax() : null)
                .waiting(purchase.isWaiting())
                .build();
    }

    /**
     * 여행 기간 계산 (일수)
     */
    private Integer calculateDuration(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1; // 시작일과 종료일 포함
    }

    /**
     * 남은 정원 계산
     */
    private Long calculateRemainingCapacity(renewal.common.entity.Product product) {
        if (product.getTour() == null || product.getTour().getCount() == null) {
            return 0L;
        }
        Long totalCapacity = product.getTour().getCount();
        Long totalUsedCapacity = calculateTotalUsedCapacity(product.getId());
        return totalCapacity - totalUsedCapacity;
    }

    /**
     * 해당 패키지의 모든 구매에서 사용된 총 승객 수 계산 (취소된 주문 제외)
     */
    private Long calculateTotalUsedCapacity(Long productId) {
        // 해당 상품의 모든 구매 조회
        List<PurchaseProduct> allPurchases = purchaseProductAdminRepository.findByProductId(productId);

        // 취소되지 않은 구매의 승객 수만 합계 (HOLDING, PAID 상태만)
        return allPurchases.stream()
                .filter(purchase -> purchase.getPurchaseStatus() == PurchaseStatus.HOLDING ||
                        purchase.getPurchaseStatus() == PurchaseStatus.PAID)
                .mapToLong(purchase -> purchase.getPassengers() != null ? (long) purchase.getPassengers().size() : 0L)
                .sum();
    }

    private long getSafeCount(Long value) {
        return value != null ? value : 0L;
    }

    /**
     * 국적 코드 목록 조회 (검색 기능 포함)
     */
    public List<CountryCodeDto> getCountries(String search) {
        if (log.isDebugEnabled()) {
            log.debug("getCountries: search={}", search);
        }

        try {
            List<CountryCode> countries = countryCodeRepository.findAll();

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

            return result;

        } catch (Exception e) {
            log.warn("국가 조회 오류: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

}
