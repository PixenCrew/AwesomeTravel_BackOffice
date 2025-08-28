package renewal.awesome_travel_backoffice.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import renewal.awesome_travel_backoffice.comment.entity.Comment;
import renewal.awesome_travel_backoffice.user.utils.Provider;
import renewal.awesome_travel_backoffice.user.utils.Role;
import renewal.awesome_travel_backoffice.user.utils.Status;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기본 로그인 정보
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255)
    private String password; // 소셜 로그인은 null 가능

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider; // LOCAL, GOOGLE, NAVER

    @Column(length = 100)
    private String providerId; // 소셜로그인 고유 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role; // USER, ADMIN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status; // ACTIVE, WITHDRAWN, BANNED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

    // 여권 정보 (nullable)
    @Column(length = 20)
    private String passportNumber;

    private LocalDate passportIssuedDate;
    private LocalDate passportExpiryDate;

    @Column(length = 3)
    private String passportCountry; // 국가코드 (예: KOR)

    @Column(length = 50)
    private String englishFirstName;

    @Column(length = 50)
    private String englishLastName;

    private Boolean emailVerified;
    private Boolean marketingConsent;

//    @OneToMany(mappedBy = "user")
//    private List<Wait> waitList = new ArrayList<>();

    @OneToMany(mappedBy = "id")
    private List<Comment> comment = new ArrayList<>();

//    @OneToMany(mappedBy = "user")
//    private List<AirPurchase> airPurchases = new ArrayList<>();

//    @OneToMany(mappedBy = "user")
//    private List<ProductPurchase> productPurchases = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = this.status == null ? Status.ACTIVE : this.status;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
