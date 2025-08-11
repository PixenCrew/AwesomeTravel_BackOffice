package renewal.awesome_travel_backoffice.product.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterDTO {
    private String title;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String infoKeyword;
    private double avgFrom;
    private double avgTo;

    // 연결된 Tour속성에서 검색
    private String country;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
    private LocalDate endDateFrom;
    private LocalDate endDateTo;
}
