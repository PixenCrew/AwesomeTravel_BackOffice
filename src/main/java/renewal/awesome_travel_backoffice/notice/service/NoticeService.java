package renewal.awesome_travel_backoffice.notice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.awesome_travel_backoffice.notice.dto.request.NoticeRequestDto;
import renewal.awesome_travel_backoffice.notice.dto.request.NoticeSearchRequest;
import renewal.awesome_travel_backoffice.notice.dto.response.NoticeResponseDto;
import renewal.awesome_travel_backoffice.notice.repository.NoticeRepository;
import renewal.awesome_travel_backoffice.notice.repository.NoticeSpecification;
import renewal.common.entity.Notice;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public Long create(NoticeRequestDto dto) {
        Notice notice = new Notice(
                dto.getTitle(),
                dto.getContent(),
                dto.getFix(),
                dto.getPriority(),
                dto.getImageUrl(),
                dto.getCategory(),
                dto.getStartAt(),
                dto.getEndAt());
        
        // visible 필드 설정 (null이면 true로 기본 설정)
        notice.setVisible(dto.getVisible() != null ? dto.getVisible() : true);
        
        return noticeRepository.save(notice).getId();
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseDto> getAll() {
        return noticeRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NoticeResponseDto getById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));
        return toDto(notice);
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponseDto> search(NoticeSearchRequest noticeSearchRequest, Pageable pageable) {
        // Specification 조합
        Specification<Notice> spec = Specification.where(null);
        
        // 키워드 검색
        if (noticeSearchRequest.getKeyword() != null && !noticeSearchRequest.getKeyword().isEmpty()) {
            spec = spec.and(NoticeSpecification.keywordContains(
                noticeSearchRequest.getKeyword(), 
                noticeSearchRequest.getSearchType()
            ));
        }
        
        // 카테고리 필터
        if (noticeSearchRequest.getCategory() != null) {
            spec = spec.and(NoticeSpecification.categoryEquals(noticeSearchRequest.getCategory()));
        }
        
        // 고정 여부 필터
        if (noticeSearchRequest.getFix() != null) {
            spec = spec.and(NoticeSpecification.fixEquals(noticeSearchRequest.getFix()));
        }
        
        // 숨김 공지사항 포함 여부
        spec = spec.and(NoticeSpecification.includeHidden(noticeSearchRequest.getIncludeHidden()));
        
        // 검색 실행 및 DTO 변환
        return noticeRepository.findAll(spec, pageable)
                .map(this::toDto);
    }

    @Transactional
    public void update(Long id, NoticeRequestDto dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));

        if (dto.getTitle() != null)     notice.setTitle(dto.getTitle());
        if (dto.getContent() != null)   notice.setContent(dto.getContent());
        if (dto.getFix() != null)       notice.setFix(dto.getFix());
        if (dto.getPriority() != null)  notice.setPriority(dto.getPriority());
        if (dto.getImageUrl() != null)  notice.setImageUrl(dto.getImageUrl());
        if (dto.getCategory() != null)  notice.setCategory(dto.getCategory());
        if (dto.getStartAt() != null)   notice.setStartAt(dto.getStartAt());
        if (dto.getEndAt() != null)     notice.setEndAt(dto.getEndAt());
        if (dto.getVisible() != null)   notice.setVisible(dto.getVisible());
    }

    // @Transactional
    // public void partialUpdate(Long id, NoticeRequestDto dto) {
    // Notice notice = noticeRepository.findById(id)
    // .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));
    // notice.setPartially(dto);
    // }

    @Transactional
    public void toggleFix(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));

        notice.setFix(!notice.getFix()); // true → false, false → true
    }

    @Transactional
    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    private NoticeResponseDto toDto(Notice notice) {
        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .fix(notice.getFix())
                .priority(notice.getPriority())
                .imageUrl(notice.getImageUrl())
                .category(notice.getCategory())
                .startAt(notice.getStartAt())
                .endAt(notice.getEndAt())
                .visible(notice.getVisible())
                .createdAt(notice.getCreatedAt())
                .modifiedAt(notice.getModifiedAt())
                .createdBy(notice.getCreatedBy())
                .modifiedBy(notice.getModifiedBy())
                .build();
    }

}
