package renewal.awesome_travel_backoffice.airPurchase.entity;

import java.time.LocalDate;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import renewal.awesome_travel_backoffice.airPurchase.utiles.Sex;
import renewal.awesome_travel_backoffice.country.entity.Country;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class   BasePassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected  Long id;

    //국내 국제 필수 입력값
    @Column(nullable = false)
    protected  String name; //풀네임 (한국인>한글, 외국인>영문)

    @Column(nullable = false)
    protected  String number; //전화번호

    @Column(nullable = false)
    protected  String email; //이메일

    @Column(nullable = false)
    protected  LocalDate birth; //생년월일

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected Sex sex; //성별

    @ManyToOne
    @JoinColumn(name = "nationality_code", referencedColumnName = "countryCode", nullable = false)
    protected Country nationality; //국적 REPUBLIC OF KOREA

    //국제선
    @Column(nullable = true)
    protected  String passport_num; //여권번호

    @Column(nullable = true)
    protected  String lastName; //영문 성

    @Column(nullable = true)
    protected  String firstName; //영문 이름

    @Column(nullable = true)
    protected  LocalDate expire; //만료일

    public BasePassenger(String name, String number, String email, LocalDate birth, Sex sex, Country nationality, String passport_num, String lastName, String firstName, LocalDate expire) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.birth = birth;
        this.sex = sex;
        this.nationality = nationality;
        this.passport_num = passport_num;
        this.lastName = lastName;
        this.firstName = firstName;
        this.expire = expire;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

}
