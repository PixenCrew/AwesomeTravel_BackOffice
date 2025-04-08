package renewal.awesome_travel_backoffice.air.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.air.dto.request.SeatClassRequestDto;
import renewal.awesome_travel_backoffice.air.dto.response.SeatClassResponseDto;
import renewal.awesome_travel_backoffice.air.service.SeatClassService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/seat-class")
@PreAuthorize("hasRole('ADMIN')")
public class SeatClassController {

    private final SeatClassService seatClassService;

    @PostMapping("/{airId}")
    public ResponseEntity<SeatClassResponseDto> create(
            @PathVariable Long airId,
            @RequestBody SeatClassRequestDto dto
    ) {
        return ResponseEntity.ok(seatClassService.create(airId, dto));
    }

    @GetMapping("/air/{airId}")
    public ResponseEntity<List<SeatClassResponseDto>> getByAir(@PathVariable Long airId) {
        return ResponseEntity.ok(seatClassService.getByAir(airId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatClassResponseDto> update(
            @PathVariable Long id,
            @RequestBody SeatClassRequestDto dto
    ) {
        return ResponseEntity.ok(seatClassService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seatClassService.delete(id);
        return ResponseEntity.noContent().build();
    }
}