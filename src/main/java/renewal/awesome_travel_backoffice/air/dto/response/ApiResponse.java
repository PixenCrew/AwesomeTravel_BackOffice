package renewal.awesome_travel_backoffice.air.dto.response;

import lombok.Getter;
import lombok.Setter;
import renewal.awesome_travel_backoffice.air.entity.FlightItem;

import java.util.List;

@Getter
@Setter
public class ApiResponse {

    private int page;
    private int perPage;
    private int totalCount;
    private int currentCount;
    private int matchCount;
    private List<FlightItem> data;
}
