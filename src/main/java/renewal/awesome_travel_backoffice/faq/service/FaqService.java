package renewal.awesome_travel_backoffice.faq.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import renewal.awesome_travel_backoffice.faq.dto.request.FaqRequestDto;
import renewal.awesome_travel_backoffice.faq.dto.response.FaqResponseDto;
import renewal.awesome_travel_backoffice.faq.entity.Faq;
import renewal.awesome_travel_backoffice.faq.repositiry.FaqRepository;
import renewal.awesome_travel_backoffice.faq.utils.FaqCategory;

@Service
@RequiredArgsConstructor
public class FaqService {

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


    public FaqResponseDto getFaq(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ가 존재하지 않습니다."));
        return toDto(faq);
    }

    public void updateFaq(Long id, FaqRequestDto dto) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ가 존재하지 않습니다."));
        faq.update(dto.getQuestion(), dto.getAnswer(),dto.getCategory());
    }

    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }

    private FaqResponseDto toDto(Faq faq) {
        return FaqResponseDto.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .category(faq.getCategory())
                .createdAt(faq.getCreatedAt())
                .build();
    }
}

