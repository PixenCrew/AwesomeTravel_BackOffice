package renewal.awesome_travel_backoffice.airPurchase.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.common.entity.SeatClass;
import renewal.common.entity.Air;
import renewal.common.entity.SpecialRequest;
import renewal.common.entity.CountryCode;
import renewal.awesome_travel_backoffice.airPurchase.dto.request.PurchaseAirSearchCondition;
import renewal.awesome_travel_backoffice.airPurchase.dto.request.AirPassengerUpdateRequestDto;
import renewal.awesome_travel_backoffice.airPurchase.dto.response.PurchaseAirResponseDto;
import renewal.awesome_travel_backoffice.airPurchase.dto.response.AirResponseOneDto;
import renewal.awesome_travel_backoffice.airPurchase.dto.response.AirPassengerResponseDto;
import renewal.awesome_travel_backoffice.airPurchase.repository.PurchaseAirRepository;
import renewal.common.entity.PurchaseAir;
import renewal.common.entity.PassengerAir;
import renewal.common.entity.PurchaseBase.PurchaseStatus;
import renewal.common.repository.CountryCodeRepository;
import renewal.common.entity.PassengerBase.Sex;
import renewal.awesome_travel_backoffice.airPurchase.controller.PurchaseAirAdminController.CountryCodeDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseAirService {

    private final PurchaseAirRepository airPurchaseRepository;
    private final CountryCodeRepository countryCodeRepository;

    /**
     *  어드민 - 전체 항공 예약 목록 조회 (페이징 + 정렬)
     */
    public Page<PurchaseAir> getAllPurchases(PurchaseAirSearchCondition condition, Pageable pageable) {
        return airPurchaseRepository.searchByCondition(condition, pageable);
    }

    /**
     *  어드민 - 전체 항공 예약 목록 조회 (DTO 변환)
     */
    public Page<PurchaseAirResponseDto> getAllPurchasesDto(PurchaseAirSearchCondition condition, Pageable pageable) {
        Page<PurchaseAir> purchases = airPurchaseRepository.searchByCondition(condition, pageable);
        return purchases.map(this::toDto);
    }


    /**
     *  어드민 - 단건 예약 상세 조회
     */
    public PurchaseAir getPurchase(Long id) {
        PurchaseAir purchase = airPurchaseRepository.findByProductPurchaseId(id)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));
        return purchase;
    }

    /**
     *  어드민 - 단건 예약 상세 조회 (DTO 변환)
     */
    public PurchaseAirResponseDto getPurchaseDto(Long id) {
        System.out.println("=== getPurchaseDto 호출 - ID: " + id + " ===");
        
        PurchaseAir purchase = airPurchaseRepository.findByIdWithPassengers(id)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역 없음"));
        
        
        // 디버깅 로그 추가
        System.out.println("=== AirPurchase 디버깅 정보 ===");
        System.out.println("AirPurchase ID: " + purchase.getProductPurchaseId());
        System.out.println("Expected Passengers: " + purchase.getExpectedPassengerCount());
        System.out.println("Actual Passengers: " + (purchase.getPassengerAirs() != null ? purchase.getPassengerAirs().size() : "null"));
        System.out.println("Passenger Info Complete: " + purchase.isPassengerInfoComplete());
        System.out.println("Transaction Complete: " + purchase.isTransactionComplete());
        if (purchase.getPassengerAirs() != null && !purchase.getPassengerAirs().isEmpty()) {
            purchase.getPassengerAirs().forEach(passenger -> {
                System.out.println("  - 승객: " + (passenger.getName() != null ? passenger.getName() : "null") + " (ID: " + passenger.getId() + ")");
                System.out.println("    특별요청: " + passenger.getSpecialRequests().size() + "개");
            });
        } else {
            System.out.println("  ⚠️ PassengerAirs가 비어있습니다!");
        }
        System.out.println("===============================");
        
        return toDto(purchase);
    }

    // 관리자용 - 상태 변경
    @Transactional
    public void changePurchaseStatus(Long id, PurchaseStatus newStatus) {
        PurchaseAir purchase = airPurchaseRepository.findByProductPurchaseId(id)
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
            seatClass.setAvailableSeats(Long.valueOf(purchase.getPassengerAirs().size()));
        }

        // 상태 변경 적용
        purchase.setPurchaseStatus(newStatus);

        // 로그 (예: 실제로는 DB에 남기거나 파일에 기록 가능)
        System.out.printf("[관리자] 예약 상태 변경: ID=%d | %s → %s | 시간=%s\n",
                purchase.getProductPurchaseId(), currentStatus, newStatus, LocalDateTime.now());
    }

    /**
     *  내부 변환 메서드 (응답용 DTO로 변환)
     */
    private PurchaseAirResponseDto toDto(PurchaseAir purchase) {
        SeatClass seatClass = purchase.getSeatClass();
        Air air = seatClass.getAir();

        //유저기능과 달리 AirResponseDto의 구조가 다르기 떄문에 AirResponseOneDto로 변경해서 보여줌
        //어드민에서의 AirResponse는 항공하나의 리턴이 아닌 seatclass전체를 리턴하기 때문에 차이가 있음
        AirResponseOneDto airDto = AirResponseOneDto.builder()
                .airId(air.getId())
                .code(air.getFlightNumber())
                .airlineCode(air.getAirline().getCode())
                .airlineNameKor(air.getAirline().getNameKor())
                .airlineNameEng(air.getAirline().getNameEng())
                .depart(air.getDepartAirport() != null ? air.getDepartAirport() : null)
                .arrive(air.getArriveAirport() != null ? air.getArriveAirport() : null)
                .departTime(air.getDepartDateTime())
                .arriveTime(air.getArriveDateTime())
                .stopovers(air.getStopovers())
                .flightType(air.getFlightType())
                .seatClassId(seatClass.getId())
                .seatClassType(seatClass.getClassType())
                .price(seatClass.getPrice())
                .availableSeats(seatClass.getAvailableSeats())
                .build();

        List<AirPassengerResponseDto> passengerDtos = purchase.getPassengerAirs().stream()
                .map(passenger -> {
                    List<String> requestList = passenger.getSpecialRequests().stream()
                            .map(SpecialRequest::getRequestType)
                            .toList();
                    return new AirPassengerResponseDto(
                            passenger.getId(),
                            passenger.getName(),
                            passenger.getNumber(),
                            passenger.getEmail(),
                            passenger.getBirth(),
                            passenger.getSex() != null ? passenger.getSex().name() : null,
                            passenger.getNationality() != null ? passenger.getNationality().getCode() : null,
                            passenger.getPassport_num(),
                            passenger.getLastName(),
                            passenger.getFirstName(),
                            passenger.getExpire(),
                            requestList
                    );
                }).toList();

        return new PurchaseAirResponseDto(
                purchase.getProductPurchaseId(),
                airDto,
                purchase.getPurchaseStatus(),
                purchase.getPrice(),
                purchase.getMember_id(),
                purchase.getName(),
                purchase.getNumber(),
                purchase.getEmail(),
                purchase.getPurchaseDate(),
                purchase.getPaymentDueDate(),
                passengerDtos,
                purchase.getExpectedPassengerCount(),
                purchase.isPassengerInfoComplete(),
                purchase.getPassengerInfoDeadline(),
                purchase.isTransactionComplete()
        );
    }

    /**
     * 1단계: 항공권 구매 생성 (부분적 승객 정보 입력 가능)
     * 최소 1명의 승객 정보는 입력해야 하며, 나머지는 마감일 전까지 입력 가능합니다.
     */
    @Transactional
    public PurchaseAir createPurchaseWithPassengers(SeatClass seatClass, Long productPurchaseId, Long price, 
                                                   Long member_id, String name, String number, String email, 
                                                   int expectedPassengerCount, List<AirPassengerUpdateRequestDto> passengerDtos) {
        
        // 승객 정보 검증 - 0명부터 입력 가능
        if (passengerDtos == null) {
            passengerDtos = new ArrayList<>(); // 빈 리스트로 초기화
        }
        
        if (passengerDtos.size() > expectedPassengerCount) {
            throw new IllegalArgumentException("입력된 승객 수가 예상 승객 수를 초과합니다.");
        }
        
        // 입력된 승객들의 정보가 완전한지 검증 (빈 필드가 있는 경우는 허용)
        for (AirPassengerUpdateRequestDto dto : passengerDtos) {
            // 빈 필드가 있어도 일단 생성은 허용 (나중에 수정 가능)
            // 단, 입력된 정보가 있다면 유효한지 검증
            if (dto.getName() != null && dto.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("승객 이름이 비어있습니다.");
            }
            if (dto.getNumber() != null && dto.getNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("승객 연락처가 비어있습니다.");
            }
            if (dto.getEmail() != null && dto.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("승객 이메일이 비어있습니다.");
            }
        }
        
        PurchaseAir purchase = new PurchaseAir(seatClass, productPurchaseId, price, 
                                             member_id, name, number, email, 
                                             LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                                             expectedPassengerCount);
        
        // 승객 정보 입력 마감일 설정 (구매일로부터 7일 후)
        purchase.setPassengerInfoDeadline(LocalDateTime.now().plusDays(7));
        
        // 1단계: 입력된 승객 정보 추가 (빈 필드가 있어도 객체 생성)
        for (AirPassengerUpdateRequestDto dto : passengerDtos) {
            PassengerAir passenger = new PassengerAir();
            
            // 입력된 정보만 설정, 빈 필드는 null로 유지
            passenger.setName(dto.getName());
            passenger.setNumber(dto.getNumber());
            passenger.setEmail(dto.getEmail());
            passenger.setBirth(dto.getBirth());
            if (dto.getSex() != null && !dto.getSex().trim().isEmpty()) {
                try {
                    passenger.setSex(Sex.valueOf(dto.getSex().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    System.out.println("유효하지 않은 성별 값: " + dto.getSex());
                }
            }
            passenger.setPassport_num(dto.getPassportNum());
            passenger.setLastName(dto.getLastName());
            passenger.setFirstName(dto.getFirstName());
            passenger.setExpire(dto.getExpire());
            passenger.setAirPurchase(purchase);
            
            // 국적은 유효한 경우에만 설정
            if (dto.getNationality() != null && !dto.getNationality().trim().isEmpty()) {
                CountryCode nationality = countryCodeRepository.findByCode(dto.getNationality())
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 국적 코드입니다."));
                passenger.setNationality(nationality);
            }
            
            purchase.getPassengerAirs().add(passenger);
        }
        
        // 2단계: 예상 승객 수만큼 빈 승객 객체 생성 (부족한 경우)
        while (purchase.getPassengerAirs().size() < expectedPassengerCount) {
            PassengerAir emptyPassenger = new PassengerAir();
            emptyPassenger.setAirPurchase(purchase);
            // 모든 필드를 null로 유지 (빈 객체)
            purchase.getPassengerAirs().add(emptyPassenger);
        }
        
        System.out.println("=== 빈 객체 생성 완료 ===");
        System.out.println("예상 승객 수: " + expectedPassengerCount);
        System.out.println("생성된 승객 수: " + purchase.getPassengerAirs().size());
        System.out.println("입력된 승객 수: " + passengerDtos.size());
        System.out.println("빈 객체 수: " + (purchase.getPassengerAirs().size() - passengerDtos.size()));
        
        // 승객 정보 완료 상태 확인 및 설정
        checkPassengerInfoComplete(purchase);
        
        // 초기에는 항상 HOLDING 상태 (승객 정보 입력 대기)
        purchase.setPurchaseStatus(PurchaseStatus.HOLDING);
        
        return airPurchaseRepository.save(purchase);
    }

    /**
     * 2단계: 항공권 승객 정보 수정/추가 (마감일 전까지 수정 가능)
     * 빈 승객 객체를 찾아서 정보를 채우거나, 기존 승객 정보를 수정합니다.
     */
    @Transactional
    public void addPassengerInfo(Long purchaseId, AirPassengerUpdateRequestDto passengerDto) {
        PurchaseAir purchase = airPurchaseRepository.findByIdWithPassengers(purchaseId)
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
        PassengerAir emptyPassenger = purchase.getPassengerAirs().stream()
                .filter(p -> p.getName() == null || p.getName().trim().isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("더 이상 승객 정보를 추가할 수 없습니다."));
        
        // 승객 정보 설정
        emptyPassenger.setName(passengerDto.getName());
        emptyPassenger.setNumber(passengerDto.getNumber());
        emptyPassenger.setEmail(passengerDto.getEmail());
        emptyPassenger.setBirth(passengerDto.getBirth());
        if (passengerDto.getSex() != null && !passengerDto.getSex().trim().isEmpty()) {
            try {
                emptyPassenger.setSex(Sex.valueOf(passengerDto.getSex().toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.out.println("유효하지 않은 성별 값: " + passengerDto.getSex());
            }
        }
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
        
        // 승객 정보 완료 여부만 확인 (결제 상태는 별도로 관리)
        
        airPurchaseRepository.save(purchase);
    }

    /**
     * 결제 완료 처리
     * 승객 정보가 완료된 경우에만 결제가 가능합니다.
     */
    @Transactional
    public void completePayment(Long purchaseId) {
        PurchaseAir purchase = airPurchaseRepository.findByIdWithPassengers(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));
        
        // 승객 정보가 완료되었는지 확인
        if (!purchase.isPassengerInfoComplete()) {
            throw new IllegalStateException("모든 승객 정보를 입력한 후 결제를 진행해주세요.");
        }
        
        // 결제 완료 상태로 변경
        purchase.setPurchaseStatus(PurchaseStatus.PAID);
        
        airPurchaseRepository.save(purchase);
    }

    /**
     * 승객 정보 완료 상태 확인
     */
    private void checkPassengerInfoComplete(PurchaseAir purchase) {
        // 예상 승객 수와 실제 승객 수가 일치하고, 모든 승객의 필수 정보가 완전한지 확인
        if (purchase.getPassengerAirs().size() == purchase.getExpectedPassengerCount()) {
            boolean allComplete = purchase.getPassengerAirs().stream()
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
            // 승객 수가 부족하면 미완료 상태
            purchase.setIsPassengerInfoComplete(false);
        }
    }

    /**
     * 승객 정보 수정 (관리자용)
     */
    @Transactional
    public void updatePassenger(Long purchaseId, Long passengerId, AirPassengerUpdateRequestDto updateRequest) {
        // 구매 정보 조회
        PurchaseAir purchase = airPurchaseRepository.findByProductPurchaseId(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));

        // 승객 정보 조회
        PassengerAir passenger = purchase.getPassengerAirs().stream()
                .filter(p -> p.getId().equals(passengerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("승객 정보를 찾을 수 없습니다."));

        // 디버깅 로그
        System.out.println("=== PassengerAir 정보 수정 디버깅 ===");
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
        passenger.updateInfo(updateRequest, newCountry, null);
        
        System.out.println("수정 후 승객 정보: " + passenger.getName() + ", " + passenger.getNumber() + ", " + passenger.getEmail());
        System.out.println("===============================");

        // 승객 정보 완료 상태 재확인
        checkPassengerInfoComplete(purchase);

        // 로그
        System.out.printf("[관리자] 승객 정보 수정: 구매ID=%d, 승객ID=%d, 승객명=%s | 시간=%s\n",
                purchaseId, passengerId, passenger.getName(), LocalDateTime.now());
    }

    /**
     * 국적 코드 목록 조회 (검색 기능 포함)
     */
    public List<CountryCodeDto> getCountries(String search) {
        System.out.println("=== getCountries 호출됨 ===");
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

