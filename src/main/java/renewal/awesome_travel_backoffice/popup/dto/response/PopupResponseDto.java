package renewal.awesome_travel_backoffice.popup.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import renewal.common.entity.Popup;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PopupResponseDto {
    
    private Long id;
    private Integer displayOrder;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private String file;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String createdBy;
    private String modifiedBy;
    
    public static PopupResponseDto from(Popup popup) {
        PopupResponseDto dto = new PopupResponseDto();
        dto.setId(popup.getId());
        dto.setDisplayOrder(popup.getDisplayOrder());
        dto.setTitle(popup.getTitle());
        dto.setStartDate(popup.getStartDate());
        dto.setEndDate(popup.getEndDate());
        dto.setActive(popup.isActive());
        dto.setFile(popup.getFile());
        dto.setUrl(popup.getUrl());
        dto.setCreatedAt(popup.getCreatedAt());
        dto.setModifiedAt(popup.getModifiedAt());
        dto.setCreatedBy(popup.getCreatedBy());
        dto.setModifiedBy(popup.getModifiedBy());
        return dto;
    }
}
