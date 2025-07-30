package renewal.awesome_travel_backoffice.tour.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourFilterDTO {
    private String name;
    private List<String> companies;
    private String country;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
    private LocalDate endDateFrom;
    private LocalDate endDateTo;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String pointLocation;
    private Long startCount;
    private Long endCount;
}
