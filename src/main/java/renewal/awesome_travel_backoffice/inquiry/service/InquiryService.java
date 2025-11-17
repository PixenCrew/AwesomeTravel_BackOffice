package renewal.awesome_travel_backoffice.inquiry.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.inquiry.dto.request.InquiryAnswerRequestDto;
import renewal.awesome_travel_backoffice.inquiry.dto.response.InquiryResponseDto;
import renewal.awesome_travel_backoffice.inquiry.repository.InquiryAnswerRepository;
import renewal.awesome_travel_backoffice.inquiry.repository.InquiryRepository;
import renewal.awesome_travel_backoffice.notification.repository.NotificationRepository;
import renewal.common.entity.Inquiry;
import renewal.common.entity.InquiryAnswer;
import renewal.common.entity.Notification;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final NotificationRepository notificationRepository;

    public Page<InquiryResponseDto> getAllInquiries(Pageable pageable) {
        return inquiryRepository.findAll(pageable).map(this::toDto);
    }

    public Page<InquiryResponseDto> searchInquiriesAdmin(String keyword, String searchType, Boolean isAnswered,
            String category, String status, String startDate, String endDate, Pageable pageable) {
        // String을 enum으로 변환
        Inquiry.InquiryCategory categoryEnum = null;
        if (category != null && !category.trim().isEmpty()) {
            try {
                categoryEnum = Inquiry.InquiryCategory.valueOf(category);
            } catch (IllegalArgumentException e) {
                // 잘못된 카테고리 값은 무시
            }
        }

        Inquiry.InquiryStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = Inquiry.InquiryStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                // 잘못된 상태 값은 무시
            }
        }

        // 날짜 변환
        java.time.LocalDateTime startDateTime = null;
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                startDateTime = java.time.LocalDate.parse(startDate).atStartOfDay();
            } catch (Exception e) {
                // 잘못된 날짜 형식은 무시
            }
        }

        java.time.LocalDateTime endDateTime = null;
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                endDateTime = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
            } catch (Exception e) {
                // 잘못된 날짜 형식은 무시
            }
        }

        return inquiryRepository.searchAdmin(keyword, searchType, isAnswered, categoryEnum, statusEnum, startDateTime,
                endDateTime, pageable).map(this::toDto);
    }

    public Long createAnswer(Long inquiryId, Long adminId, InquiryAnswerRequestDto dto) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow();
        InquiryAnswer answer = InquiryAnswer.create(inquiryId, adminId, dto.getContent());
        inquiryAnswerRepository.save(answer);
        inquiry.markAnswered();
        notificationRepository.save(Notification.create(
                inquiry.getUser().getId(), // 연관관계 기반
                "작성하신 문의에 답변이 등록되었습니다."));
        return answer.getId();
    }

    public void updateAnswer(Long answerId, InquiryAnswerRequestDto dto) {
        InquiryAnswer answer = inquiryAnswerRepository.findById(answerId).orElseThrow();
        if (dto.getContent() != null)
            answer.updateContent(dto.getContent());
    }

    public void deleteAnswer(Long answerId) {
        InquiryAnswer answer = inquiryAnswerRepository.findById(answerId).orElseThrow();
        Inquiry inquiry = inquiryRepository.findById(answer.getInquiryId()).orElseThrow();
        inquiry.cancelAnswered();
        inquiryAnswerRepository.delete(answer);
    }

    private InquiryResponseDto toDto(Inquiry inquiry) {
        return InquiryResponseDto.builder()
                .id(inquiry.getId())
                .userId(inquiry.getUser().getId())
                .userName(inquiry.getUser().getName())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .category(inquiry.getCategory())
                .status(inquiry.getStatus())
                .stage(inquiry.getStage())
                .productId(inquiry.getProductId())
                .purchaseId(inquiry.getPurchaseId())
                .isAnswered(inquiry.isAnswered())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
}
