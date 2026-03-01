package renewal.awesome_travel_backoffice.refund.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import renewal.awesome_travel_backoffice.refund.service.RefundService;
import renewal.common.entity.Refund;

@Controller
@RequestMapping("/refund")
@RequiredArgsConstructor
public class RefundViewController {

    private static final Logger log = LoggerFactory.getLogger(RefundViewController.class);

    private final RefundService refundService;

    @GetMapping
    public String refundManagement(
            @RequestParam(required = false) Refund.RefundStatus status,
            @RequestParam(required = false) Refund.RefundType refundType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "requestDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model
    ) {
        // 정렬 설정
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortField).ascending() 
            : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 20, sort);

        // 환불 목록 조회
        Page<Refund> refundPage = refundService.getRefunds(status, refundType, pageable);

        // 환불 통계 조회
        RefundService.RefundStats stats = new RefundService.RefundStats();
        stats.setRequestedCount(refundService.getRefundCountByStatus(Refund.RefundStatus.REQUESTED));
        stats.setApprovedCount(refundService.getRefundCountByStatus(Refund.RefundStatus.APPROVED));
        stats.setProcessedCount(refundService.getRefundCountByStatus(Refund.RefundStatus.COMPLETED));
        stats.setRejectedCount(refundService.getRefundCountByStatus(Refund.RefundStatus.REJECTED));

        if (log.isDebugEnabled()) {
            log.debug("환불 관리 페이지: status={}, refundType={}", status, refundType);
        }

        // 모델에 데이터 추가
        model.addAttribute("refundPage", refundPage);
        model.addAttribute("refundStats", stats);
        model.addAttribute("status", status);
        model.addAttribute("refundType", refundType);
        model.addAttribute("refundTypes", Refund.RefundType.values());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("title", "환불 관리");
        model.addAttribute("content", "components/refund/refundManagement");
        model.addAttribute("isSelectionPage", false);

        return "layout";
    }
}
