package renewal.awesome_travel_backoffice.product.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductFilterDTO {
    private String title;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String infoKeyword;
    private BigDecimal avgFrom;
    private BigDecimal avgTo;

    // 연결된 Tour속성에서 검색
    private String country;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDateTo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateTo;
    
    // 상품 상태 필터
    private String status; // "all", "active", "inactive"
}
