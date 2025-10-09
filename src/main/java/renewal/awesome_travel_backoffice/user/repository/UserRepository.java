package renewal.awesome_travel_backoffice.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import renewal.common.entity.User;
import renewal.common.entity.User.UserStatus;
import renewal.common.entity.User.MemberGrade;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 이메일로 회원 조회
    Optional<User> findByEmail(String email);
    
    // 이메일 중복 체크
    boolean existsByEmail(String email);
    
    // 상태별 회원 조회
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    
    // 등급별 회원 조회
    Page<User> findByGrade(MemberGrade grade, Pageable pageable);
    
    // 이름, 이메일, 전화번호로 검색
    @Query("SELECT u FROM User u WHERE " +
           "u.name LIKE %:keyword% OR " +
           "u.email LIKE %:keyword% OR " +
           "u.phone LIKE %:keyword%")
    Page<User> findByNameContainingOrEmailContainingOrPhoneContaining(
            @Param("keyword") String keyword, 
            @Param("keyword") String keyword2, 
            @Param("keyword") String keyword3, 
            Pageable pageable);
    
    // 이름, 이메일, 전화번호로 검색 + 상태 필터
    @Query("SELECT u FROM User u WHERE " +
           "(u.name LIKE %:keyword% OR " +
           "u.email LIKE %:keyword% OR " +
           "u.phone LIKE %:keyword%) AND " +
           "u.status = :status")
    Page<User> findByNameContainingOrEmailContainingOrPhoneContainingAndStatus(
            @Param("keyword") String keyword, 
            @Param("keyword") String keyword2, 
            @Param("keyword") String keyword3, 
            @Param("status") UserStatus status,
            Pageable pageable);
    
    // 이름, 이메일, 전화번호로 검색 + 등급 필터
    @Query("SELECT u FROM User u WHERE " +
           "(u.name LIKE %:keyword% OR " +
           "u.email LIKE %:keyword% OR " +
           "u.phone LIKE %:keyword%) AND " +
           "u.grade = :grade")
    Page<User> findByNameContainingOrEmailContainingOrPhoneContainingAndGrade(
            @Param("keyword") String keyword, 
            @Param("keyword") String keyword2, 
            @Param("keyword") String keyword3, 
            @Param("grade") MemberGrade grade,
            Pageable pageable);
}
