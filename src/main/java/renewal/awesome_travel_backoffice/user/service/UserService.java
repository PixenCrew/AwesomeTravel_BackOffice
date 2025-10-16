package renewal.awesome_travel_backoffice.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.user.dto.request.UserRequestDto;
import renewal.awesome_travel_backoffice.user.dto.response.UserResponseDto;
import renewal.awesome_travel_backoffice.user.repository.UserRepository;
import renewal.common.entity.User;
import renewal.common.entity.User.UserStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    
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
    
    // 회원 등록
    @Transactional
    public Long createUser(UserRequestDto userRequestDto) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        
        User user = User.builder()
                .email(userRequestDto.getEmail())
                .name(userRequestDto.getName())
                .phone(userRequestDto.getPhone())
                .birthDate(userRequestDto.getBirthDate())
                .provider(userRequestDto.getProvider())
                .providerId(userRequestDto.getSocialId())
                .role(userRequestDto.getRole())
                .status(userRequestDto.getStatus())
                .passportNumber(userRequestDto.getPassportNumber())
                .passportIssuedDate(userRequestDto.getPassportIssuedDate())
                .passportExpiryDate(userRequestDto.getPassportExpiryDate())
                .passportCountry(userRequestDto.getPassportCountry())
                .englishFirstName(userRequestDto.getEnglishFirstName())
                .englishLastName(userRequestDto.getEnglishLastName())
                .emailVerified(userRequestDto.getEmailVerified() != null ? userRequestDto.getEmailVerified() : false)
                .marketingConsent(userRequestDto.getMarketingConsent() != null ? userRequestDto.getMarketingConsent() : false)
                .build();
        
        User savedUser = userRepository.save(user);
        return savedUser.getId();
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
        
        user.updateUserInfo(
                userRequestDto.getEmail(),
                userRequestDto.getName(),
                userRequestDto.getPhone(),
                userRequestDto.getBirthDate(),
                userRequestDto.getProvider(),
                userRequestDto.getSocialId(),
                userRequestDto.getRole(),
                userRequestDto.getStatus(),
                userRequestDto.getPassportNumber(),
                userRequestDto.getPassportIssuedDate(),
                userRequestDto.getPassportExpiryDate(),
                userRequestDto.getPassportCountry(),
                userRequestDto.getEnglishFirstName(),
                userRequestDto.getEnglishLastName(),
                userRequestDto.getEmailVerified(),
                userRequestDto.getMarketingConsent()
        );
        
        userRepository.save(user);
    }
    
    // 회원 삭제
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. ID: " + id));
        
        userRepository.delete(user);
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
                .passportNumber(user.getPassportNumber())
                .passportIssuedDate(user.getPassportIssuedDate())
                .passportExpiryDate(user.getPassportExpiryDate())
                .passportCountry(user.getPassportCountry())
                .englishFirstName(user.getEnglishFirstName())
                .englishLastName(user.getEnglishLastName())
                .emailVerified(user.getEmailVerified())
                .marketingConsent(user.getMarketingConsent())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getModifiedAt())
                .build();
    }
}
