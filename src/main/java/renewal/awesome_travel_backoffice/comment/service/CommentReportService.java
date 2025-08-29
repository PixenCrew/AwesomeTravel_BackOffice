package renewal.awesome_travel_backoffice.comment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.awesome_travel_backoffice.comment.dto.response.CommentReportResponseDto;
import renewal.awesome_travel_backoffice.comment.repository.CommentReportRepository;
import renewal.awesome_travel_backoffice.comment.utiles.ReportReason;
import renewal.common.entity.CommentReport;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final CommentReportRepository commentReportRepository;

    /**
     * 어드민 - 댓글 신고 전체 조회 (필터/페이징)
     */
    @Transactional(readOnly = true)
    public Page<CommentReportResponseDto> getAllReports(ReportReason reason, Pageable pageable) {
        return commentReportRepository.searchReports(reason, pageable)
                .map(this::toResponseDto);
    }

    /**
     * 어드민 - 신고 삭제 (무효 처리 등)
     */
    @Transactional
    public void deleteReport(Long reportId) {
        if (!commentReportRepository.existsById(reportId)) {
            throw new IllegalArgumentException("신고 내역이 존재하지 않습니다.");
        }
        commentReportRepository.deleteById(reportId);
    }

    /**
     * DTO 변환 메서드
     */
    private CommentReportResponseDto toResponseDto(CommentReport report) {
        return CommentReportResponseDto.builder()
                .reportId(report.getId())
                .commentId(report.getComment().getId())
                .commentContent(report.getComment().getContent())
                .rating(report.getComment().getRating())
                .reporterName(report.getReporter().getName())
                .reportedUserName(report.getComment().getWriter().getName())
                .reason(report.getReason())
                .reportedAt(report.getReportedAt())
                .build();
    }
}

