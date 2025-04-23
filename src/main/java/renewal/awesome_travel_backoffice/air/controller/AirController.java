package renewal.awesome_travel_backoffice.air.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.air.dto.request.AirRequestDto;
import renewal.awesome_travel_backoffice.air.dto.request.AirSearchRequestDto;
import renewal.awesome_travel_backoffice.air.dto.response.AirResponseDto;
import renewal.awesome_travel_backoffice.air.service.AirService;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;

@RestController
@RequestMapping("/admin/air")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AirController {

    private final AirService airService;

    @PostMapping
    public ResponseEntity<AirResponseDto> createAir(@RequestBody AirRequestDto dto) {
        return ResponseEntity.ok(airService.createAir(dto));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AirResponseDto>> searchAirList(@ModelAttribute AirSearchRequestDto req) {
        Page<AirResponseDto> result = airService.getAirList(req);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirResponseDto> updateAir(@PathVariable Long id, @RequestBody AirRequestDto dto) {
        return ResponseEntity.ok(airService.updateAir(id, dto));
    }

    @PatchMapping("/{id}/update-details")
    public ResponseEntity<Void> updateDetails(@PathVariable Long id, @RequestBody AirRequestDto dto) {
        airService.updateDetails(id, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam AirStatus status) {
        airService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAir(@PathVariable Long id) {
        airService.deleteAir(id);
        return ResponseEntity.noContent().build();
    }
}
