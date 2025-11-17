package renewal.awesome_travel_backoffice.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.awesome_travel_backoffice.review.dto.response.ReviewReportResponseDto;
import renewal.awesome_travel_backoffice.review.repository.ReviewReportRepository;
import renewal.common.entity.ReviewReport;
import renewal.common.entity.ReviewReport.ReportReason;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewReportService {

    private final ReviewReportRepository reviewReportRepository;

    /**
     * 어드민 - 댓글 신고 전체 조회 (필터/페이징)
     */
    @Transactional(readOnly = true)
    public Page<ReviewReportResponseDto> getAllReports(ReportReason reason, String keyword,
            java.time.LocalDateTime startDate, java.time.LocalDateTime endDate, Pageable pageable) {
        return reviewReportRepository.searchReports(reason, keyword, startDate, endDate, pageable)
                .map(this::toResponseDto);
    }

    /**
     * 어드민 - 신고 삭제 (무효 처리 등)
     */
    @Transactional
    public void deleteReport(Long reportId) {
        if (!reviewReportRepository.existsById(reportId)) {
            throw new IllegalArgumentException("신고 내역이 존재하지 않습니다.");
        }
        reviewReportRepository.deleteById(reportId);
    }

    /**
     * DTO 변환 메서드
     */
    private ReviewReportResponseDto toResponseDto(ReviewReport report) {
        return ReviewReportResponseDto.builder()
                .reportId(report.getId())
                .reviewId(report.getReview().getId())
                .reviewContent(report.getReview().getContent())
                .rating(report.getReview().getRating())
                .reporterName(report.getReporter().getName())
                .reportedUserName(report.getReview().getWriter().getName())
                .reason(report.getReason())
                .reportedAt(report.getReportedAt())
                .build();
    }
}

