package renewal.awesome_travel_backoffice.product.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import renewal.awesome_travel_backoffice.config.AuditingFields;
import renewal.awesome_travel_backoffice.hotel.entity.Hotel;
import renewal.awesome_travel_backoffice.tour.entity.Tour;

@Entity
@Table
@Getter 
@Setter
@NoArgsConstructor
public class Product extends AuditingFields{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    private String title;
    private Long price;

    // // 1. tour
    // @OneToOne(mappedBy = "id")
    // private Tour tour;
    
    @OneToOne
    @JoinColumn(name = "tour_id") // Product 테이블에 tour_id FK 생성
    private Tour tour;

    // 2. hotel
    @OneToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
    
    // // 3. air
    // @OneToOne(mappedBy = "air_id")
    // private Air air;
    
    // 4. info

    // 이미지 URL들
    @ElementCollection
    private List<String> images = new ArrayList<>();

    // 상품정보
    private LinkedHashMap<String,String> include;
    private LinkedHashMap<String,String> exclude;
    private LinkedHashMap<String,String> term;

    // 일정표

    // 리뷰 요약
    private Long totalReview;
    private BigDecimal avgerageReview;
    private Long star1;
    private Long star2;
    private Long star3;
    private Long star4;
    private Long star5;

    

}
