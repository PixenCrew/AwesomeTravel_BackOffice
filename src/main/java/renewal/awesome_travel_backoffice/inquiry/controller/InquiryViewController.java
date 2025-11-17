package renewal.awesome_travel_backoffice.inquiry.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.inquiry.dto.response.InquiryResponseDto;
import renewal.awesome_travel_backoffice.inquiry.repository.InquiryAnswerRepository;
import renewal.awesome_travel_backoffice.inquiry.repository.InquiryRepository;
import renewal.awesome_travel_backoffice.inquiry.service.InquiryService;
import renewal.common.entity.Inquiry;
import renewal.common.entity.InquiryAnswer;

@Controller
@RequestMapping("/inquiry")
@RequiredArgsConstructor
public class InquiryViewController {
    private final InquiryService inquiryService;
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final InquiryRepository inquiryRepository;

    @GetMapping
    public String inquiryList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "searchType", required = false) String searchType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "error", required = false) String error,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10);

        // 검색 조건에 따른 조회
        Page<InquiryResponseDto> inquiryPage;

        // 답변 상태 필터링
        Boolean isAnswered = null;
        if ("PENDING".equalsIgnoreCase(status)) {
            isAnswered = false;
        } else if ("ANSWERED".equalsIgnoreCase(status)) {
            isAnswered = true;
        }

        // 검색어 처리 (null이거나 빈 문자열인 경우 null로 변환)
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        // 검색타입 처리
        String searchTypeParam = (searchType != null && !searchType.trim().isEmpty()) ? searchType.trim() : null;

        // 카테고리와 상태 필터링
        String searchCategory = (category != null && !category.trim().isEmpty()) ? category.trim() : null;
        String searchStatus = (status != null && !status.trim().isEmpty()) ? status.trim() : null;

        // 검색 조건이 있으면 검색, 없으면 전체 조회
        if (searchKeyword != null || isAnswered != null || searchCategory != null || searchStatus != null
                || startDate != null || endDate != null) {
            inquiryPage = inquiryService.searchInquiriesAdmin(searchKeyword, searchTypeParam, isAnswered,
                    searchCategory, searchStatus, startDate, endDate, pageable);
        } else {
            inquiryPage = inquiryService.getAllInquiries(pageable);
        }

        model.addAttribute("title", "1:1 문의 관리");
        model.addAttribute("content", "components/inquiry/inquiry");
        model.addAttribute("totalCount", inquiryPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", inquiryPage.getTotalPages());
        model.addAttribute("inquiries", inquiryPage);

        // 에러 메시지 처리
        if ("notFound".equals(error)) {
            model.addAttribute("errorMessage", "요청하신 문의를 찾을 수 없습니다.");
        }

        return "layout";
    }

    @GetMapping("/{id}")
    public String inquiryDetail(@PathVariable(name = "id") Long id, Model model) {
        // 서비스에 단건 조회가 없으므로 repository 직접 사용
        Inquiry inquiryEntity = inquiryRepository.findById(id).orElse(null);
        InquiryResponseDto inquiry = (inquiryEntity == null) ? null
                : InquiryResponseDto.builder()
                        .id(inquiryEntity.getId())
                        .userId(inquiryEntity.getUser().getId())
                        .userName(inquiryEntity.getUser().getName())
                        .title(inquiryEntity.getTitle())
                        .content(inquiryEntity.getContent())
                        .category(inquiryEntity.getCategory())
                        .status(inquiryEntity.getStatus())
                        .stage(inquiryEntity.getStage())
                        .productId(inquiryEntity.getProductId())
                        .purchaseId(inquiryEntity.getPurchaseId())
                        .isAnswered(inquiryEntity.isAnswered())
                        .createdAt(inquiryEntity.getCreatedAt())
                        .build();
        if (inquiry == null) {
            // 문의가 없을 경우 목록 페이지로 리다이렉트
            return "redirect:/inquiry?error=notFound";
        }

        Optional<InquiryAnswer> answerOpt = inquiryAnswerRepository.findByInquiryId(id);

        model.addAttribute("inquiry", inquiry);
        model.addAttribute("answer", answerOpt.orElse(null));
        model.addAttribute("title", "문의 상세");
        model.addAttribute("content", "components/inquiry/inquiryDetail");
        return "layout";
    }

    @PostMapping("/{id}/answer")
    public String addOrUpdateAnswer(@PathVariable("id") Long id,
            @RequestParam("adminId") Long adminId,
            @RequestParam("content") String content,
            RedirectAttributes redirectAttributes) {

        Inquiry inquiry = inquiryRepository.findById(id).orElse(null);
        if (inquiry != null) {

            // 기존 답변 조회
            InquiryAnswer answer = inquiryAnswerRepository.findByInquiryId(id)
                    .orElse(null);

            if (answer == null) {
                // 없으면 새로 생성
                answer = InquiryAnswer.create(id, adminId, content);
            } else {
                // 있으면 내용 교체
                answer.setContent(content);
                answer.setAdminId(adminId);
            }

            inquiryAnswerRepository.save(answer);

            // 문의 상태 갱신
            inquiry.markAnswered();
            inquiryRepository.save(inquiry);
        }

        redirectAttributes.addFlashAttribute("message", "답변이 등록/갱신되었습니다.");
        return "redirect:/inquiry/" + id;
    }

    @PostMapping("/{id}/status/toggle")
    public String toggleStatus(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        // InquiryStatus 필드/메서드가 엔티티에 없으므로 answered 플래그로 토글
        Inquiry inquiry = inquiryRepository.findById(id).orElse(null);
        if (inquiry != null) {
            if (!inquiry.isAnswered()) {
                inquiry.markAnswered();
                redirectAttributes.addFlashAttribute("message", "상태가 '처리중'으로 변경되었습니다.");
            } else {
                inquiry.cancelAnswered();
                redirectAttributes.addFlashAttribute("message", "상태가 '대기중'으로 변경되었습니다.");
            }
            inquiryRepository.save(inquiry);
        }
        return "redirect:/inquiry/" + id;
    }

    @PostMapping("/{id}/status/complete")
    public String markAsCompleted(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        // 엔티티에 markCompleted가 없으므로 markAnswered로 대체
        Inquiry inquiry = inquiryRepository.findById(id).orElse(null);
        if (inquiry != null) {
            inquiry.markAnswered();
            inquiryRepository.save(inquiry);
            redirectAttributes.addFlashAttribute("message", "문의가 완료 처리되었습니다.");
        }
        return "redirect:/inquiry/" + id;
    }

    @PostMapping("/answer/{answerId}/delete")
    public String deleteAnswer(@PathVariable(name = "answerId") Long answerId,
            @RequestParam(name = "inquiryId") Long inquiryId,
            RedirectAttributes redirectAttributes) {
        inquiryService.deleteAnswer(answerId);
        redirectAttributes.addFlashAttribute("message", "답변이 삭제되었습니다.");
        return "redirect:/inquiry/" + inquiryId;
    }
}
