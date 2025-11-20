package renewal.awesome_travel_backoffice.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.purchaseAir.dto.response.PurchaseAirResponseDto;
import renewal.awesome_travel_backoffice.purchaseAir.service.PurchaseAirService;
import renewal.awesome_travel_backoffice.purchaseProduct.dto.response.PurchaseProductResponseDto;
import renewal.awesome_travel_backoffice.purchaseProduct.service.PurchaseProductService;
import renewal.awesome_travel_backoffice.user.dto.request.UserRequestDto;
import renewal.awesome_travel_backoffice.user.dto.response.UserResponseDto;
import renewal.awesome_travel_backoffice.user.repository.UserRepository;
import renewal.common.entity.PurchaseAir;
import renewal.common.entity.PurchaseProduct;
import renewal.common.entity.User;
import renewal.common.entity.User.UserStatus;
import renewal.common.repository.PurchaseProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final renewal.common.repository.PurchaseAirRepository commonPurchaseAirRepository;
    private final renewal.awesome_travel_backoffice.purchaseAir.repository.PurchaseAirAdminRepository adminPurchaseAirRepository;
    private final PurchaseAirService purchaseAirService;
    private final PurchaseProductRepository commonPurchaseProductRepository;
    private final PurchaseProductService purchaseProductService;
    
    // 모든 회원 조회 (페이징)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToResponseDto);
    }
    
    // 회원 검색 (키워드, 상태별)
    public Page<UserResponseDto> searchUsers(String keyword, UserStatus status, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty() && status != null) {
            return userRepository.findByNameContainingOrEmailContainingOrPhoneContainingAndStatus(
                    keyword, keyword, keyword, status, pageable)
                    .map(this::convertToResponseDto);
        } else if (keyword != null && !keyword.isEmpty()) {
            return userRepository.findByNameContainingOrEmailContainingOrPhoneContaining(
                    keyword, keyword, keyword, pageable)
                    .map(this::convertToResponseDto);
        } else if (status != null) {
            return userRepository.findByStatus(status, pageable)
                    .map(this::convertToResponseDto);
        } else {
            return getAllUsers(pageable);
        }
    }
    
    // ID로 회원 조회
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. ID: " + id));
        return convertToResponseDto(user);
    }
    
    // 회원 수정
    @Transactional
    public void updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. ID: " + id));
        
        // 이메일 중복 체크 (자신 제외)
        if (!user.getEmail().equals(userRequestDto.getEmail()) && 
            userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        
        // User 엔티티 필드 직접 업데이트
        if (userRequestDto.getEmail() != null) {
            user.setEmail(userRequestDto.getEmail());
        }
        if (userRequestDto.getName() != null) {
            user.setName(userRequestDto.getName());
        }
        if (userRequestDto.getPhone() != null) {
            user.setPhone(userRequestDto.getPhone());
        }
        if (userRequestDto.getBirthDate() != null) {
            user.setBirthDate(userRequestDto.getBirthDate());
        }
        if (userRequestDto.getStatus() != null) {
            user.setStatus(userRequestDto.getStatus());
        }
        if (userRequestDto.getEmailVerified() != null) {
            user.setEmailVerified(userRequestDto.getEmailVerified());
        }
        
        userRepository.save(user);
    }
    
    // 회원 삭제
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. ID: " + id));
        
        userRepository.delete(user);
    }
    
    // 회원의 항공 구매 내역 조회
    public List<PurchaseAirResponseDto> getUserAirPurchases(Long userId) {
        List<PurchaseAir> purchases = commonPurchaseAirRepository.findByUserId(userId);
        // passengers를 로드하기 위해 다시 조회
        return purchases.stream()
                .map(purchase -> {
                    try {
                        // passengers를 포함하여 다시 조회
                        PurchaseAir fullPurchase = adminPurchaseAirRepository.findByIdWithPassengers(purchase.getId())
                                .orElse(purchase);
                        // finalSeatClasses 초기화 (lazy loading)
                        if (fullPurchase.getFinalSeatClasses() != null) {
                            fullPurchase.getFinalSeatClasses().size();
                        }
                        return purchaseAirService.toDto(fullPurchase);
                    } catch (Exception e) {
                        // 변환 실패 시 null 반환 (필터링됨)
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
    
    // 회원의 패키지 상품 구매 내역 조회
    public List<PurchaseProductResponseDto> getUserProductPurchases(Long userId) {
        List<PurchaseProduct> purchases = commonPurchaseProductRepository.findByUserId(userId);
        return purchases.stream()
                .map(purchase -> {
                    try {
                        return purchaseProductService.toDto(purchase);
                    } catch (Exception e) {
                        // 변환 실패 시 null 반환 (필터링됨)
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
    
    // User 엔티티를 UserResponseDto로 변환
    private UserResponseDto convertToResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .provider(user.getProvider())
                .socialId(user.getProviderId())
                .role(user.getRole())
                .status(user.getStatus())
                .terms(user.getTerms())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getModifiedAt())
                .build();
    }
}
