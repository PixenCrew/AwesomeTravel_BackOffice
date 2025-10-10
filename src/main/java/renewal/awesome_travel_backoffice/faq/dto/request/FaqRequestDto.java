package renewal.awesome_travel_backoffice.faq.dto.request;

import renewal.common.entity.Faq.FaqCategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaqRequestDto {
    private String question;
    private String answer;
    private FaqCategory category;
    private Boolean visible = true;
}
