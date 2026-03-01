package renewal.awesome_travel_backoffice.faq.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import renewal.awesome_travel_backoffice.faq.dto.request.FaqRequestDto;
import renewal.awesome_travel_backoffice.faq.dto.response.FaqResponseDto;
import renewal.awesome_travel_backoffice.faq.repositiry.FaqRepository;
import renewal.common.entity.Faq.FaqCategory;
import renewal.common.entity.Faq;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaqService {

    private static final Logger log = LoggerFactory.getLogger(FaqService.class);

    private final FaqRepository faqRepository;

    public Long createFaq(FaqRequestDto dto) {
        Faq faq = new Faq(dto.getQuestion(), dto.getAnswer(), dto.getCategory());
        return faqRepository.save(faq).getId();
    }

    public Page<FaqResponseDto> getAllFaqs(Pageable pageable) {
        return faqRepository.findAll(pageable)
                .map(this::toDto);
    }

    public Page<FaqResponseDto> getFaqsByCategory(FaqCategory category, Pageable pageable) {
        return faqRepository.findByCategory(category, pageable)
                .map(this::toDto);
    }

    public Page<FaqResponseDto> searchFaqs(String keyword, String searchType, String category, Boolean isVisible, String startDate, String endDate, Pageable pageable) {
        // String을 enum으로 변환
        FaqCategory categoryEnum = null;
        if (category != null && !category.trim().isEmpty()) {
            try {
                categoryEnum = FaqCategory.valueOf(category);
            } catch (IllegalArgumentException e) {
                // 잘못된 카테고리 값은 무시
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
        
        return faqRepository.searchFaqs(keyword, searchType, categoryEnum, isVisible, startDateTime, endDateTime, pageable).map(this::toDto);
    }


    public FaqResponseDto getFaq(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ가 존재하지 않습니다."));
        return toDto(faq);
    }

    public void updateFaq(Long id, FaqRequestDto dto) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ가 존재하지 않습니다."));
        faq.update(dto.getQuestion(), dto.getAnswer(), dto.getCategory());
        // 체크박스가 체크되지 않으면 null이 전달되므로 false로 처리
        boolean newVisible = dto.getVisible() != null ? dto.getVisible() : false;
        if (log.isDebugEnabled()) {
            log.debug("Update FAQ: id={}, visible from DTO={}, setting to={}", id, dto.getVisible(), newVisible);
        }
        faq.setVisible(newVisible);
        faqRepository.save(faq);
    }

    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }

    public void toggleStatus(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ가 존재하지 않습니다."));
        if (log.isDebugEnabled()) {
            log.debug("FAQ toggle: id={}, visible {} -> {}", id, faq.getVisible(), !faq.getVisible());
        }
        faq.setVisible(!faq.getVisible());
        // 변경사항을 저장
        faqRepository.save(faq);
    }

    private FaqResponseDto toDto(Faq faq) {
        return FaqResponseDto.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .category(faq.getCategory())
                .visible(faq.getVisible())
                .createdAt(faq.getCreatedAt())
                .build();
    }
}

