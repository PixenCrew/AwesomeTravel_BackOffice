package renewal.awesome_travel_backoffice.popup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.popup.dto.request.PopupRequestDto;
import renewal.awesome_travel_backoffice.popup.dto.request.PopupSearchRequest;
import renewal.awesome_travel_backoffice.popup.dto.response.PopupResponseDto;
import renewal.awesome_travel_backoffice.popup.entity.Popup;
import renewal.awesome_travel_backoffice.popup.repository.PopupRepository;

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
        // 간단한 구현 - 실제로는 Specification이나 QueryDSL을 사용하는 것이 좋습니다
        Page<Popup> popups;
        
        if (searchRequest.getActive() != null) {
            popups = popupRepository.findByActive(searchRequest.getActive(), pageable);
        } else if (searchRequest.getIncludeInactive() != null && searchRequest.getIncludeInactive()) {
            popups = popupRepository.findAll(pageable);
        } else {
            popups = popupRepository.findByActive(true, pageable);
        }
        
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
