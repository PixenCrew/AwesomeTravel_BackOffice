package renewal.awesome_travel_backoffice.inquiry.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.inquiry.dto.request.InquiryAnswerRequestDto;
import renewal.awesome_travel_backoffice.inquiry.dto.response.InquiryResponseDto;
import renewal.awesome_travel_backoffice.inquiry.service.InquiryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/inquiry")
@PreAuthorize("hasRole('ADMIN')")
public class InquiryAdminController {

    private final InquiryService inquiryService;

    // 전체 문의 조회
    @GetMapping
    public ResponseEntity<Page<InquiryResponseDto>> getAllInquiries(Pageable pageable) {
        return ResponseEntity.ok(inquiryService.getAllInquiries(pageable));
    }

    // 검색 + 답변 여부 필터
    @GetMapping("/search")
    public ResponseEntity<Page<InquiryResponseDto>> searchInquiriesAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isAnswered,
            Pageable pageable
    ) {
        return ResponseEntity.ok(inquiryService.searchInquiriesAdmin(keyword, isAnswered, pageable));
    }

    // 답변 등록
    @PostMapping("/{id}/answers")
    public ResponseEntity<Long> createAnswer(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestBody InquiryAnswerRequestDto dto
    ) {
        return ResponseEntity.ok(inquiryService.createAnswer(id, adminId, dto));
    }

    // 답변 수정
    @PatchMapping("/answers/{answerId}")
    public ResponseEntity<Void> updateAnswer(
            @PathVariable Long answerId,
            @RequestBody InquiryAnswerRequestDto dto
    ) {
        inquiryService.updateAnswer(answerId, dto);
        return ResponseEntity.ok().build();
    }

    // 답변 삭제
    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long answerId) {
        inquiryService.deleteAnswer(answerId);
        return ResponseEntity.ok().build();
    }
}

