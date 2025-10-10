package renewal.awesome_travel_backoffice.popup.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PopupSearchRequest {
    
    private String keyword;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
    private LocalDate endDateFrom;
    private LocalDate endDateTo;
    private Boolean active;
    private Boolean includeInactive = false;
}
