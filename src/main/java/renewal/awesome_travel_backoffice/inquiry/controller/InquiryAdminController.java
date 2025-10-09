package renewal.awesome_travel_backoffice.inquiry.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import renewal.awesome_travel_backoffice.inquiry.dto.request.InquiryAnswerRequestDto;
import renewal.awesome_travel_backoffice.inquiry.dto.response.InquiryResponseDto;
import renewal.awesome_travel_backoffice.inquiry.service.InquiryService;

import lombok.RequiredArgsConstructor;

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
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) Boolean isAnswered,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(inquiryService.searchInquiriesAdmin(keyword, searchType, isAnswered, category, status, startDate, endDate, pageable));
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

