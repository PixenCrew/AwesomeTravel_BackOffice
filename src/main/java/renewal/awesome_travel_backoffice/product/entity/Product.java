package renewal.awesome_travel_backoffice.product.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import renewal.awesome_travel_backoffice.tour.entity.Tour;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Product extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    private String title;
    private Long price;

    @OneToOne
    @JoinColumn(name = "tour_id") // Product 테이블에 tour_id FK 생성
    private Tour tour;

    // 이미지 URL들
    @ElementCollection
    private List<String> images = new ArrayList<>();

    // 상품정보
    @ElementCollection
    private Map<String, String> info = new HashMap<>();

    // 일정표

    // 리뷰 요약
    private Long totalReview = 0L; // 총 리뷰 수
    @Column(precision = 3, scale = 2)
    private BigDecimal avgerageReview = new BigDecimal("0.00"); // 평점 평균
    private Long star1 = 0L; // 1점 리뷰 수
    private Long star2 = 0L; // 2점 리뷰 수
    private Long star3 = 0L; // ...
    private Long star4 = 0L;
    private Long star5 = 0L;

    // private Long[] stars = new Long[]{0L,0L,0L,0L,0L};

    public void UpdateAvg(int star) {
        totalReview++;
        switch (star) {
            case 1:
                star1++;
                break;
            case 2:
                star2++;
                break;
            case 3:
                star3++;
                break;
            case 4:
                star4++;
                break;
            case 5:
                star5++;
                break;
        }

        Long sum = star1 + star2 * 2 + star3 * 3 + star4 * 4 + star5 * 5;
        avgerageReview = BigDecimal.valueOf(sum)
                .divide(
                        BigDecimal.valueOf(totalReview),
                        2,
                        RoundingMode.HALF_UP);
    }

}
