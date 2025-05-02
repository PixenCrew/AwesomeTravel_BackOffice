package renewal.awesome_travel_backoffice.specialRequest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.specialRequest.dto.SpecialRequestDto;
import renewal.awesome_travel_backoffice.specialRequest.service.SpecialRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/special-requests")
public class SpecialRequestAdminController {

    private final SpecialRequestService specialRequestService;

    // 특별요청 등록
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody SpecialRequestDto dto) {
        Long id = specialRequestService.createRequest(dto);
        return ResponseEntity.ok(id);
    }

    // 관리자용 - 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<SpecialRequestDto>> getAll() {
        List<SpecialRequestDto> list = specialRequestService.getAllForAdmin();
        return ResponseEntity.ok(list);
    }

    // 특별요청 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody SpecialRequestDto dto) {
        specialRequestService.updateRequest(id, dto);
        return ResponseEntity.ok().build();
    }

    // 특별요청 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        specialRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}