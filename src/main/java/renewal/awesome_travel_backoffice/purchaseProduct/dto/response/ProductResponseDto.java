package renewal.awesome_travel_backoffice.purchaseProduct.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {

    private Long productId; // 상품 ID
    private String title; // 상품명
    private Long price; // 상품 가격
    private String country; // 국가
    private String city; // 도시
    private String startDate; // 출발일
    private String endDate; // 도착일
    private Integer duration; // 여행 기간
    private Double averageRating; // 평균 평점
    private Long totalReviews; // 총 리뷰 수
    private Long totalCapacity; // 총 정원
    private Long remainingCapacity; // 남은 정원
}
