package renewal.awesome_travel_backoffice.promotion.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromotionFilterDTO {
    private String title; // 제목 검색
    
    private String menuCodeCode; // 메뉴 코드 필터
    
    private Boolean active; // 상태 필터 (true: 진행중, false: 종료/예정)
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDateFrom; // 시작일 (최소)
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDateTo; // 시작일 (최대)
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateFrom; // 종료일 (최소)
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateTo; // 종료일 (최대)
}

