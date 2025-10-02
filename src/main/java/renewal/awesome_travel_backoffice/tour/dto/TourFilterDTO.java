package renewal.awesome_travel_backoffice.tour.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourFilterDTO {
    private String name;
    private List<String> companies;
    private String city;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDateTo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateTo;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String pointLocation;
    private Long startCount;
    private Long endCount;

    // 연결된 Product 번호 있는지
    private boolean findOrphan = false;

}
