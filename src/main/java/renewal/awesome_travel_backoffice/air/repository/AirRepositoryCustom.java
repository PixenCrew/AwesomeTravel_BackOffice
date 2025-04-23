package renewal.awesome_travel_backoffice.air.repository;

import org.springframework.data.domain.Page;
import renewal.awesome_travel_backoffice.air.dto.request.AirSearchRequestDto;
import renewal.awesome_travel_backoffice.air.dto.response.AirResponseDto;

public interface AirRepositoryCustom {
    Page<AirResponseDto> searchAdminAirList(AirSearchRequestDto req);
}
