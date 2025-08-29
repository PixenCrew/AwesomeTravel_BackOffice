package renewal.awesome_travel_backoffice.specialRequest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.awesome_travel_backoffice.specialRequest.dto.SpecialRequestDto;
import renewal.awesome_travel_backoffice.specialRequest.repository.SpecialRequestRepository;
import renewal.common.entity.SpecialRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecialRequestService {

    private final SpecialRequestRepository specialRequestRepository;

    // 관리자용 - 특별요청 등록
    @Transactional
    public Long createRequest(SpecialRequestDto dto) {
        SpecialRequest request = new SpecialRequest(dto.getRequestType(), dto.getDescription());
        return specialRequestRepository.save(request).getId();
    }

    // 관리자용 - 전체 요청 조회
    public List<SpecialRequestDto> getAllForAdmin() {
        return specialRequestRepository.findAll().stream()
                .map(req -> new SpecialRequestDto(
                        req.getId(),
                        req.getRequestType(),
                        req.getDescription()
                ))
                .collect(Collectors.toList());
    }

    // 관리자용 - 특별요청 수정
    @Transactional
    public void updateRequest(Long id, SpecialRequestDto dto) {
        SpecialRequest request = specialRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요청 항목이 존재하지 않습니다."));
        request.update(dto.getRequestType(), dto.getDescription());
    }

    // 관리자용 - 특별요청 삭제
    @Transactional
    public void deleteRequest(Long id) {
        specialRequestRepository.deleteById(id);
    }
}
