package renewal.awesome_travel_backoffice.airPurchase.entity;

import java.time.LocalDateTime;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import renewal.awesome_travel_backoffice.airPurchase.utiles.PurchaseStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class BasePurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    protected Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected PurchaseStatus purchaseStatus;


    @Column(nullable = false)
    protected Long price; //결제금액

    @Column(nullable = false)
    protected Long member_id; //구매자

    @Column(nullable = false)
    protected String name;

    @Column(nullable = false)
    protected String number;

    @Column(nullable = false)
    protected String email;

    @Column(nullable = false)
    protected LocalDateTime purchaseDate; //구매일

    @Column
    protected LocalDateTime paymentDueDate; //결제기한

    public BasePurchase(Long price, Long member_id, String name, String number, String email, LocalDateTime purchaseDate, LocalDateTime paymentDueDate) {
        this.price = price;
        this.member_id = member_id;
        this.name = name;
        this.number = number;
        this.email = email;
        this.purchaseDate = purchaseDate;
        this.paymentDueDate = paymentDueDate;
    }

}
