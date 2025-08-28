package renewal.awesome_travel_backoffice.faq.dto.request;

import renewal.awesome_travel_backoffice.faq.utils.FaqCategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaqRequestDto {
    private String question;
    private String answer;
    private FaqCategory category;
}
