package renewal.awesome_travel_backoffice.popup.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PopupRequestDto {
    
    private Long id;
    private Integer displayOrder;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active = true;
    private String file;
    private String url;
}
