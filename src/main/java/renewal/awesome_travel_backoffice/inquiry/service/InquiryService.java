package renewal.awesome_travel_backoffice.inquiry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import renewal.awesome_travel_backoffice.inquiry.dto.request.InquiryAnswerRequestDto;
import renewal.awesome_travel_backoffice.inquiry.dto.response.InquiryResponseDto;
import renewal.awesome_travel_backoffice.inquiry.entity.Inquiry;
import renewal.awesome_travel_backoffice.inquiry.entity.InquiryAnswer;
import renewal.awesome_travel_backoffice.inquiry.repository.InquiryAnswerRepository;
import renewal.awesome_travel_backoffice.inquiry.repository.InquiryRepository;
import renewal.awesome_travel_backoffice.notification.entity.Notification;
import renewal.awesome_travel_backoffice.notification.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final NotificationRepository notificationRepository;

    public Page<InquiryResponseDto> getAllInquiries(Pageable pageable) {
        return inquiryRepository.findAll(pageable).map(this::toDto);
    }

    public Page<InquiryResponseDto> searchInquiriesAdmin(String keyword, Boolean isAnswered, Pageable pageable) {
        return inquiryRepository.searchAdmin(keyword, isAnswered, pageable).map(this::toDto);
    }

    public Long createAnswer(Long inquiryId, Long adminId, InquiryAnswerRequestDto dto) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow();
        InquiryAnswer answer = InquiryAnswer.create(inquiryId, adminId, dto.getContent());
        inquiryAnswerRepository.save(answer);
        inquiry.markAnswered();
        notificationRepository.save(Notification.create(
                inquiry.getUser().getId(),  // 연관관계 기반
                "작성하신 문의에 답변이 등록되었습니다."
        ));
        return answer.getId();
    }

    public void updateAnswer(Long answerId, InquiryAnswerRequestDto dto) {
        InquiryAnswer answer = inquiryAnswerRepository.findById(answerId).orElseThrow();
        if (dto.getContent() != null) answer.updateContent(dto.getContent());
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
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .isAnswered(inquiry.isAnswered())
                .createdAt(inquiry.getCreatedAt())
                .answeredAt(inquiry.getAnsweredAt())
                .build();
    }
}


