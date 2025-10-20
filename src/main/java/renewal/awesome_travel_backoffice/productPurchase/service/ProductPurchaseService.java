package renewal.awesome_travel_backoffice.productPurchase.service;

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

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.productPurchase.controller.ProductPurchaseAdminController.CountryCodeDto;
import renewal.awesome_travel_backoffice.productPurchase.dto.request.ProductPassengerUpdateRequestDto;
import renewal.awesome_travel_backoffice.productPurchase.dto.request.ProductPurchaseSearchCondition;
import renewal.awesome_travel_backoffice.productPurchase.dto.response.ProductPassengerResponseDto;
import renewal.awesome_travel_backoffice.productPurchase.dto.response.ProductPurchaseResponseDto;
import renewal.awesome_travel_backoffice.productPurchase.dto.response.ProductResponseDto;
import renewal.awesome_travel_backoffice.productPurchase.repository.ProductPurchaseRepository;
import renewal.common.entity.CountryCode;
import renewal.common.entity.Passenger;
import renewal.common.entity.Product;
import renewal.common.entity.PurchaseBase.PurchaseStatus;
import renewal.common.entity.PurchaseProduct;
import renewal.common.repository.CountryCodeRepository;

@Service
@RequiredArgsConstructor
public class ProductPurchaseService {

    private final ProductPurchaseRepository productPurchaseRepository;
    private final CountryCodeRepository countryCodeRepository;

    /**
     * 어드민 - 전체 패키지 상품 구매 목록 조회 (페이징 + 정렬)
     */
    public Page<PurchaseProduct> getAllPurchases(ProductPurchaseSearchCondition condition, Pageable pageable) {
        return productPurchaseRepository.searchByCondition(condition, pageable);
    }

    /**
     * 어드민 - 전체 패키지 상품 구매 목록 조회 (DTO 변환)
     */
    public Page<ProductPurchaseResponseDto> getAllPurchasesDto(ProductPurchaseSearchCondition condition, Pageable pageable) {
        Page<PurchaseProduct> purchases = productPurchaseRepository.searchByCondition(condition, pageable);
        return purchases.map(this::toDto);
    }

    /**
     * 어드민 - 단건 구매 상세 조회
     */
    public PurchaseProduct getPurchase(Long id) {
        PurchaseProduct purchase = productPurchaseRepository.findByPurchaseProductId(id)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));
        return purchase;
    }

    /**
     * 어드민 - 단건 구매 상세 조회 (DTO 변환)
     */
    public ProductPurchaseResponseDto getPurchaseDto(Long id) {
        PurchaseProduct purchase = productPurchaseRepository.findByIdWithPassengers(id)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));
        
        
        // 디버깅 로그 추가
        System.out.println("=== PurchaseProduct 디버깅 정보 ===");
        System.out.println("PurchaseProduct ID: " + purchase.getPurchaseProductId());
        System.out.println("Expected Passengers: " + purchase.getExpectedPassengerCount());
        System.out.println("Actual Passengers: " + (purchase.getPassengers() != null ? purchase.getPassengers().size() : "null"));
        System.out.println("Passenger Info Complete: " + purchase.isPassengerInfoComplete());
        System.out.println("Transaction Complete: " + purchase.isTransactionComplete());
        if (purchase.getPassengers() != null && !purchase.getPassengers().isEmpty()) {
            purchase.getPassengers().forEach(passenger -> {
                System.out.println("  - 승객: " + (passenger.getName() != null ? passenger.getName() : "null") + " (ID: " + passenger.getId() + ")");
                System.out.println("    특별요청: " + passenger.getSpecialRequests());
            });
        } else {
            System.out.println("  ⚠️ Passengers가 비어있습니다!");
        }
        System.out.println("===============================");
        
        return toDto(purchase);
    }

    // 관리자용 - 상태 변경
    @Transactional
    public void changePurchaseStatus(Long id, PurchaseStatus newStatus) {
        PurchaseProduct purchase = productPurchaseRepository.findByPurchaseProductId(id)
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

        // 로그 (예: 실제로는 DB에 남기거나 파일에 기록 가능)
        System.out.printf("[관리자] 패키지 상품 구매 상태 변경: ID=%d | %s → %s | 시간=%s\n",
                purchase.getPurchaseProductId(), currentStatus, newStatus, LocalDateTime.now());
    }

    /**
     * 1단계: 패키지 상품 구매 생성 (승객 정보 부분 입력 가능)
     * 패키지 상품의 경우 결제 후에도 승객 정보를 추가할 수 있습니다.
     */
    @Transactional
    public PurchaseProduct createPurchase(Product product, Long productPurchaseId, Long price, 
                                        Long member_id, String name, String number, String email, 
                                        int expectedPassengerCount, List<ProductPassengerUpdateRequestDto> passengerDtos) {
        
        // 승객 정보 검증 - 0명부터 입력 가능
        if (passengerDtos == null) {
            passengerDtos = new ArrayList<>();
        }
        
        if (passengerDtos.size() > expectedPassengerCount) {
            throw new IllegalArgumentException("입력된 승객 수가 예상 승객 수를 초과합니다.");
        }
        
        PurchaseProduct purchase = new PurchaseProduct(product, productPurchaseId, price, 
                                                     member_id, name, number, email, 
                                                     LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                                                     expectedPassengerCount);
        
        // 승객 정보 입력 마감일 설정 (구매일로부터 30일 후 - 패키지는 더 여유있게)
        purchase.setPassengerInfoDeadline(LocalDateTime.now().plusDays(30));
        
        // 승객 정보 추가 (빈 필드가 있어도 객체 생성)
        for (ProductPassengerUpdateRequestDto dto : passengerDtos) {
            Passenger passenger = new Passenger();

            // 입력된 정보만 설정, 빈 필드는 null로 유지
            passenger.setName(dto.getName());
            passenger.setNumber(dto.getNumber());
            passenger.setEmail(dto.getEmail());
            passenger.setBirth(dto.getBirth());
            passenger.setSex(Passenger.Sex.valueOf(dto.getSex().toLowerCase()));
            passenger.setPassport_num(dto.getPassportNum());
            passenger.setLastName(dto.getLastName());
            passenger.setFirstName(dto.getFirstName());
            passenger.setExpire(dto.getExpire());
            
            // 국적은 유효한 경우에만 설정
            if (dto.getNationality() != null && !dto.getNationality().trim().isEmpty()) {
                CountryCode nationality = countryCodeRepository.findByCode(dto.getNationality())
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 국적 코드입니다."));
                passenger.setNationality(nationality);
            }
            
            purchase.getPassengers().add(passenger);
        }
        
        // 예상 승객 수만큼 빈 승객 객체 생성 (부족한 경우)
        while (purchase.getPassengers().size() < expectedPassengerCount) {
            Passenger emptyPassenger = new Passenger();
            // 모든 필드를 null로 유지 (빈 객체)
            purchase.getPassengers().add(emptyPassenger);
        }
        
        System.out.println("=== 패키지 빈 객체 생성 완료 ===");
        System.out.println("예상 승객 수: " + expectedPassengerCount);
        System.out.println("생성된 승객 수: " + purchase.getPassengers().size());
        System.out.println("입력된 승객 수: " + passengerDtos.size());
        System.out.println("빈 객체 수: " + (purchase.getPassengers().size() - passengerDtos.size()));
        
        // 승객 정보 완료 상태 확인 및 설정
        checkPassengerInfoComplete(purchase);
        
        // 초기에는 항상 HOLDING 상태 (패키지도 승객 정보 입력 대기)
        purchase.setPurchaseStatus(PurchaseStatus.HOLDING);
        
        return productPurchaseRepository.save(purchase);
    }

    /**
     * 2단계: 승객 정보 추가
     */
    @Transactional
    public void addPassengerInfo(Long purchaseId, ProductPassengerUpdateRequestDto passengerDto) {
        PurchaseProduct purchase = productPurchaseRepository.findByPurchaseProductId(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));

        // 마감일 확인
        if (purchase.getPassengerInfoDeadline() != null && 
            LocalDateTime.now().isAfter(purchase.getPassengerInfoDeadline())) {
            throw new IllegalStateException("승객 정보 입력 마감일이 지났습니다.");
        }

        // 승객 정보 검증
        if (passengerDto.getName() == null || passengerDto.getName().trim().isEmpty() ||
            passengerDto.getNumber() == null || passengerDto.getNumber().trim().isEmpty() ||
            passengerDto.getEmail() == null || passengerDto.getEmail().trim().isEmpty() ||
            passengerDto.getBirth() == null ||
            passengerDto.getSex() == null ||
            passengerDto.getNationality() == null || passengerDto.getNationality().trim().isEmpty()) {
            throw new IllegalArgumentException("모든 필수 정보를 입력해주세요.");
        }

        // 빈 승객 객체 찾기 (이름이 null이거나 비어있는 경우)
        Passenger emptyPassenger = purchase.getPassengers().stream()
                .filter(p -> p.getName() == null || p.getName().trim().isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("더 이상 승객 정보를 추가할 수 없습니다."));

        // 승객 정보 설정
        emptyPassenger.setName(passengerDto.getName());
        emptyPassenger.setNumber(passengerDto.getNumber());
        emptyPassenger.setEmail(passengerDto.getEmail());
        emptyPassenger.setBirth(passengerDto.getBirth());
        emptyPassenger.setSex(Passenger.Sex.valueOf(passengerDto.getSex().toLowerCase()));
        emptyPassenger.setPassport_num(passengerDto.getPassportNum());
        emptyPassenger.setLastName(passengerDto.getLastName());
        emptyPassenger.setFirstName(passengerDto.getFirstName());
        emptyPassenger.setExpire(passengerDto.getExpire());

        // 국적 설정
        CountryCode nationality = countryCodeRepository.findByCode(passengerDto.getNationality())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 국적 코드입니다."));
        emptyPassenger.setNationality(nationality);

        // 승객 정보 완료 상태 확인
        checkPassengerInfoComplete(purchase);

        productPurchaseRepository.save(purchase);

        System.out.printf("[2단계] 패키지 상품 승객 정보 추가: 구매ID=%d, 승객명=%s | 시간=%s\n",
                purchaseId, emptyPassenger.getName(), LocalDateTime.now());
    }

    /**
     * 결제 완료 처리 (패키지 상품)
     * 패키지 상품의 경우 인원수에 맞게 결제를 먼저 진행합니다.
     * 승객 정보가 완료되지 않으면 나중에 취소될 수 있습니다.
     */
    @Transactional
    public void completePayment(Long purchaseId) {
        PurchaseProduct purchase = productPurchaseRepository.findByPurchaseProductId(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));
        
        // 결제 완료 상태로 변경 (승객 정보 완료 여부와 관계없이)
        purchase.setPurchaseStatus(PurchaseStatus.PAID);
        
        // 승객 정보가 완료되지 않은 경우 경고 로그
        if (!purchase.isPassengerInfoComplete()) {
            System.out.printf("[경고] 패키지 상품 결제 완료 - 승객 정보 미완료: 구매ID=%d, 예상승객수=%d, 현재승객수=%d\n",
                    purchaseId, purchase.getExpectedPassengerCount(), purchase.getPassengers().size());
        }
        
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
                .filter(purchase -> !purchase.isPassengerInfoComplete())
                .filter(purchase -> purchase.getPurchaseStatus() == PurchaseStatus.PAID)
                .collect(Collectors.toList());
        
        for (PurchaseProduct purchase : expiredPurchases) {
            // 결제 완료 상태에서 취소로 변경
            purchase.setPurchaseStatus(PurchaseStatus.CANCELLED);
            productPurchaseRepository.save(purchase);
            
            System.out.printf("[자동취소] 패키지 상품 승객 정보 마감일 초과: 구매ID=%d, 마감일=%s, 현재시간=%s\n",
                    purchase.getPurchaseProductId(), purchase.getPassengerInfoDeadline(), now);
        }
    }

    /**
     * 승객 정보 완료 상태 확인
     */
    private void checkPassengerInfoComplete(PurchaseProduct purchase) {
        if (purchase.getPassengers().size() == purchase.getExpectedPassengerCount()) {
            // 모든 승객의 필수 정보가 입력되었는지 확인
            boolean allComplete = purchase.getPassengers().stream()
                .allMatch(passenger -> 
                    passenger.getName() != null && !passenger.getName().trim().isEmpty() &&
                    passenger.getNumber() != null && !passenger.getNumber().trim().isEmpty() &&
                    passenger.getEmail() != null && !passenger.getEmail().trim().isEmpty() &&
                    passenger.getBirth() != null &&
                    passenger.getSex() != null &&
                    passenger.getNationality() != null
                );
            purchase.setIsPassengerInfoComplete(allComplete);
        } else {
            purchase.setIsPassengerInfoComplete(false);
        }
    }

    /**
     * 승객 정보 수정 (관리자용)
     */
    @Transactional
    public void updatePassenger(Long purchaseId, Long passengerId, ProductPassengerUpdateRequestDto updateRequest) {
        // 구매 정보 조회
        PurchaseProduct purchase = productPurchaseRepository.findByPurchaseProductId(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));

        // 승객 정보 조회
        Passenger passenger = purchase.getPassengers().stream()
                .filter(p -> p.getId().equals(passengerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("승객 정보를 찾을 수 없습니다."));

        // 디버깅 로그
        System.out.println("=== 승객 정보 수정 디버깅 ===");
        System.out.println("입력된 데이터: " + updateRequest);
        System.out.println("수정 전 승객 정보: " + passenger.getName() + ", " + passenger.getNumber() + ", " + passenger.getEmail());
        
        // 국적 처리
        CountryCode newCountry = null;
        if (updateRequest.getNationality() != null && !updateRequest.getNationality().trim().isEmpty()) {
            newCountry = countryCodeRepository.findByCode(updateRequest.getNationality())
                    .orElse(null);
            System.out.println("국적 코드: " + updateRequest.getNationality() + " -> " + (newCountry != null ? newCountry.getCode() : "null"));
        }
        
        // 승객 정보 업데이트
        // passenger.updateInfo(updateRequest, newCountry, null);
        
        System.out.println("수정 후 승객 정보: " + passenger.getName() + ", " + passenger.getNumber() + ", " + passenger.getEmail());
        System.out.println("===============================");

        // 승객 정보 완료 상태 재확인
        checkPassengerInfoComplete(purchase);

        // 로그
        System.out.printf("[관리자] 패키지 상품 승객 정보 수정: 구매ID=%d, 승객ID=%d, 승객명=%s | 시간=%s\n",
                purchaseId, passengerId, passenger.getName(), LocalDateTime.now());
    }

    /**
     * 내부 변환 메서드 (응답용 DTO로 변환)
     */
    private ProductPurchaseResponseDto toDto(PurchaseProduct purchase) {
        // 상품 정보 DTO 생성
        ProductResponseDto productDto = new ProductResponseDto();
        productDto.setProductId(purchase.getProduct().getId());
        productDto.setTitle(purchase.getProduct().getTitle());
        productDto.setPrice(purchase.getProduct().getPrice());
        productDto.setCountry(purchase.getProduct().getTour().getCountry() != null ? 
                            purchase.getProduct().getTour().getCountry().getCountryKor() : "");
        productDto.setCity(purchase.getProduct().getTour().getName()); // city 대신 name 사용
        productDto.setStartDate(purchase.getProduct().getTour().getStartDate().toString());
        productDto.setEndDate(purchase.getProduct().getTour().getEndDate().toString());
        productDto.setDuration(calculateDuration(purchase.getProduct().getTour().getStartDate(), purchase.getProduct().getTour().getEndDate()));
        productDto.setAverageRating(purchase.getProduct().getAverageRating());
        productDto.setTotalReviews(purchase.getProduct().getTotalReviews());
        
        // 정원 정보 설정
        Long totalCapacity = purchase.getProduct().getTour().getCount();
        productDto.setTotalCapacity(totalCapacity);
        
        // 남은 정원 계산 (총 정원 - 해당 패키지의 모든 구매에서 사용된 총 승객 수)
        Long totalUsedCapacity = calculateTotalUsedCapacity(purchase.getProduct().getId());
        productDto.setRemainingCapacity(totalCapacity - totalUsedCapacity);

        // 승객 정보 DTO 생성
        List<ProductPassengerResponseDto> passengerDtos = new ArrayList<>();
        if (purchase.getPassengers() != null && !purchase.getPassengers().isEmpty()) {
            passengerDtos = purchase.getPassengers().stream()
                    .map(passenger -> {                        
                        return new ProductPassengerResponseDto(
                                passenger.getId(),
                                passenger.getName(),
                                passenger.getNumber(),
                                passenger.getEmail(),
                                passenger.getBirth(),
                                passenger.getSex() != null ? passenger.getSex().name() : "UNKNOWN",
                                passenger.getNationality() != null ? passenger.getNationality().getCode() : "UNKNOWN",
                                passenger.getPassport_num(),
                                passenger.getLastName(),
                                passenger.getFirstName(),
                                passenger.getExpire(),
                                passenger.getSpecialRequests()
                        );
                    }).toList();
        }

        return new ProductPurchaseResponseDto(
                purchase.getPurchaseProductId(),
                productDto,
                purchase.getPurchaseStatus().name(),
                purchase.getPrice(),
                purchase.getMember_id(),
                purchase.getName(),
                purchase.getNumber(),
                purchase.getEmail(),
                purchase.getPurchaseDate(),
                purchase.getPaymentDueDate(),
                passengerDtos,
                purchase.getExpectedPassengerCount(),
                purchase.isPassengerInfoComplete()
        );
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
     * 해당 패키지의 모든 구매에서 사용된 총 승객 수 계산 (취소된 주문 제외)
     */
    private Long calculateTotalUsedCapacity(Long productId) {
        // 해당 상품의 모든 구매 조회
        List<PurchaseProduct> allPurchases = productPurchaseRepository.findByProductId(productId);
        
        // 취소되지 않은 구매의 승객 수만 합계 (HOLDING, PAID 상태만)
        return allPurchases.stream()
                .filter(purchase -> purchase.getPurchaseStatus() == PurchaseStatus.HOLDING ||
                                  purchase.getPurchaseStatus() == PurchaseStatus.PAID)
                .mapToLong(purchase -> 
                    purchase.getPassengers() != null ? 
                    (long) purchase.getPassengers().size() : 0L
                )
                .sum();
    }

    /**
     * 국적 코드 목록 조회 (검색 기능 포함)
     */
    public List<CountryCodeDto> getCountries(String search) {
        System.out.println("=== PurchaseProduct getCountries 호출됨 ===");
        System.out.println("검색어: " + search);
        
        try {
            List<CountryCode> countries = countryCodeRepository.findAll();
            System.out.println("전체 국가 수: " + countries.size());
            
            if (search != null && !search.trim().isEmpty()) {
                // 검색어가 있는 경우: 코드나 한글명, 영문명으로 검색
                String searchTerm = search.trim().toLowerCase();
                countries = countries.stream()
                        .filter(country -> 
                            country.getCode().toLowerCase().contains(searchTerm) ||
                            (country.getNameKor() != null && country.getNameKor().toLowerCase().contains(searchTerm)) ||
                            (country.getNameEng() != null && country.getNameEng().toLowerCase().contains(searchTerm))
                        )
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
                        country.getNameEng()
                    ))
                    .collect(java.util.stream.Collectors.toList());
            
            System.out.println("반환할 국가 수: " + result.size());
            return result;
            
        } catch (Exception e) {
            System.err.println("국가 조회 오류: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
