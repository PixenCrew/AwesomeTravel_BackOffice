package renewal.awesome_travel_backoffice.notice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.awesome_travel_backoffice.notice.dto.request.NoticeRequestDto;
import renewal.awesome_travel_backoffice.notice.dto.request.NoticeSearchRequest;
import renewal.awesome_travel_backoffice.notice.dto.response.NoticeResponseDto;
import renewal.awesome_travel_backoffice.notice.entity.Notice;
import renewal.awesome_travel_backoffice.notice.repository.NoticeQueryRepository;
import renewal.awesome_travel_backoffice.notice.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    private final NoticeQueryRepository noticeQueryRepository;

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
                dto.getEndAt()
        );
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

    public Page<NoticeResponseDto> search(NoticeSearchRequest noticeSearchRequest, Pageable pageable) {
        return noticeQueryRepository.search(noticeSearchRequest, pageable);
    }


    @Transactional
    public void update(Long id, NoticeRequestDto dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));
        notice.update(dto);
    }

    @Transactional
    public void partialUpdate(Long id, NoticeRequestDto dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));
        notice.updatePartially(dto);
    }


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
                .createdAt(notice.getCreatedAt())
                .modifiedAt(notice.getModifiedAt())
                .build();
    }

}

