package renewal.awesome_travel_backoffice.country.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountryDto {

    private String CountryCode;

    private String CountryName;

    private String CountryNameLocal;
}
