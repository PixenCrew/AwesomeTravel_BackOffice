package renewal.awesome_travel_backoffice.review.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProductReviewGroupDto {
    private Long productId;
    private String productTitle;
    private Long productPrice;
    private int reviewCount;
    private double averageRating;
    @Builder.Default
    private List<ReviewResponseDto> reviews = new ArrayList<>();
}

