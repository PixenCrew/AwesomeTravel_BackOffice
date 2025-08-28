package renewal.awesome_travel_backoffice.qna.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import renewal.awesome_travel_backoffice.qna.dto.request.QnaAnswerRequestDto;
import renewal.awesome_travel_backoffice.qna.dto.request.QnaAnswerUpdateRequestDto;
import renewal.awesome_travel_backoffice.qna.dto.response.QnaResponseDto;
import renewal.awesome_travel_backoffice.qna.service.QnaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/qna")
public class QnaAdminController {

    private final QnaService qnaService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/answers")
    public ResponseEntity<Long> createAnswer(@PathVariable Long id, @RequestParam Long responderId, @RequestBody QnaAnswerRequestDto dto) {
        return ResponseEntity.ok(qnaService.createAnswer(id, responderId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<Page<QnaResponseDto>> searchQnaAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isAnswered,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(qnaService.searchQnaAdmin(keyword, isAnswered, pageable));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/answers/{answerId}")
    public ResponseEntity<Void> updateAnswer(@PathVariable Long answerId, @RequestBody QnaAnswerUpdateRequestDto dto) {
        qnaService.updateAnswerPartial(answerId, dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long answerId) {
        qnaService.deleteAnswer(answerId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<QnaResponseDto>> getAllAdmin(@RequestParam(required = false) Boolean isAnswered, Pageable pageable) {
        return ResponseEntity.ok(qnaService.getAllQnaAdmin(isAnswered, pageable));
    }

}
