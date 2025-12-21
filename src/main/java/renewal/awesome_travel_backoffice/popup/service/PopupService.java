package renewal.awesome_travel_backoffice.popup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.popup.dto.request.PopupRequestDto;
import renewal.awesome_travel_backoffice.popup.dto.request.PopupSearchRequest;
import renewal.awesome_travel_backoffice.popup.dto.response.PopupResponseDto;
import renewal.awesome_travel_backoffice.popup.repository.PopupRepository;
import renewal.awesome_travel_backoffice.popup.repository.PopupSpecification;
import renewal.common.entity.Popup;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupService {
    
    private final PopupRepository popupRepository;
    
    // 모든 팝업 조회 (페이징)
    public Page<PopupResponseDto> getAllPopups(Pageable pageable) {
        return popupRepository.findAll(pageable)
                .map(PopupResponseDto::from);
    }
    
    // 검색 조건에 따른 팝업 조회
    public Page<PopupResponseDto> searchPopups(PopupSearchRequest searchRequest, Pageable pageable) {
        Specification<Popup> spec = Specification.where(null);
        
        // 키워드 검색 (제목)
        if (searchRequest.getKeyword() != null && !searchRequest.getKeyword().trim().isEmpty()) {
            spec = spec.and(PopupSpecification.titleContains(searchRequest.getKeyword()));
        }
        
        // 노출기간이 필터 기간과 겹치는 팝업 검색
        // 시작일 (부터)와 시작일 (까지)를 사용하여 노출기간이 겹치는 팝업만 표시
        if (searchRequest.getStartDateFrom() != null || searchRequest.getStartDateTo() != null) {
            spec = spec.and(PopupSpecification.displayPeriodOverlaps(
                    searchRequest.getStartDateFrom(), 
                    searchRequest.getStartDateTo()
            ));
        }
        
        // 활성화 상태 필터
        if (searchRequest.getActive() != null) {
            spec = spec.and(PopupSpecification.isActive(searchRequest.getActive()));
        } else if (searchRequest.getIncludeInactive() == null || !searchRequest.getIncludeInactive()) {
            // 기본적으로 활성화된 팝업만 조회 (includeInactive가 false이거나 null인 경우)
            spec = spec.and(PopupSpecification.isActive(true));
        }
        // includeInactive가 true이고 active가 null이면 모든 팝업 조회 (필터 없음)
        
        Page<Popup> popups = popupRepository.findAll(spec, pageable);
        return popups.map(PopupResponseDto::from);
    }
    
    // ID로 팝업 조회
    public PopupResponseDto getById(Long id) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("팝업을 찾을 수 없습니다. ID: " + id));
        return PopupResponseDto.from(popup);
    }
    
    // 현재 노출되어야 하는 활성화된 팝업들 조회
    public List<PopupResponseDto> getCurrentActivePopups() {
        LocalDate currentDate = LocalDate.now();
        return popupRepository.findCurrentActivePopups(currentDate)
                .stream()
                .map(PopupResponseDto::from)
                .collect(Collectors.toList());
    }
    
    // 팝업 생성
    @Transactional
    public Long create(PopupRequestDto requestDto) {
        Popup popup = new Popup();
        popup.setDisplayOrder(requestDto.getDisplayOrder());
        popup.setTitle(requestDto.getTitle());
        popup.setStartDate(requestDto.getStartDate());
        popup.setEndDate(requestDto.getEndDate());
        popup.setActive(requestDto.isActive());
        popup.setFile(requestDto.getFile());
        popup.setUrl(requestDto.getUrl());
        
        Popup savedPopup = popupRepository.save(popup);
        return savedPopup.getId();
    }
    
    // 팝업 수정
    @Transactional
    public void update(Long id, PopupRequestDto requestDto) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("팝업을 찾을 수 없습니다. ID: " + id));
        
        popup.setDisplayOrder(requestDto.getDisplayOrder());
        popup.setTitle(requestDto.getTitle());
        popup.setStartDate(requestDto.getStartDate());
        popup.setEndDate(requestDto.getEndDate());
        popup.setActive(requestDto.isActive());
        popup.setFile(requestDto.getFile());
        popup.setUrl(requestDto.getUrl());
        
        popupRepository.save(popup);
    }
    
    // 팝업 삭제
    @Transactional
    public void delete(Long id) {
        if (!popupRepository.existsById(id)) {
            throw new RuntimeException("팝업을 찾을 수 없습니다. ID: " + id);
        }
        popupRepository.deleteById(id);
    }
    
    // 팝업 활성화/비활성화 토글
    @Transactional
    public void toggleActive(Long id) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("팝업을 찾을 수 없습니다. ID: " + id));
        
        popup.setActive(!popup.isActive());
        popupRepository.save(popup);
    }
}
