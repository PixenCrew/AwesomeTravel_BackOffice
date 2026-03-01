package renewal.awesome_travel_backoffice.notice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.notice.dto.request.NoticeRequestDto;
import renewal.awesome_travel_backoffice.notice.dto.request.NoticeSearchRequest;
import renewal.awesome_travel_backoffice.notice.dto.response.NoticeResponseDto;
import renewal.awesome_travel_backoffice.notice.service.NoticeService;
import renewal.common.entity.Notice.NoticeCategory;
import renewal.common.entity.Notice.SearchType;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeViewController {

    private static final Logger log = LoggerFactory.getLogger(NoticeViewController.class);

    private final NoticeService noticeService;

    @GetMapping
    public String noticeList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean fix,
            @RequestParam(required = false) String includeHidden,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        
        try {
            NoticeSearchRequest searchRequest = new NoticeSearchRequest();
            searchRequest.setKeyword(keyword);
            if (searchType != null && !searchType.isEmpty()) {
                try {
                    searchRequest.setSearchType(SearchType.valueOf(searchType));
                } catch (IllegalArgumentException e) {
                    // 잘못된 searchType은 무시
                }
            }
            if (category != null && !category.isEmpty()) {
                try {
                    searchRequest.setCategory(NoticeCategory.valueOf(category));
                } catch (IllegalArgumentException e) {
                    // 잘못된 category는 무시
                }
            }
            searchRequest.setFix(fix);
            // 기본적으로는 공개 공지만 표시, 체크박스로 숨김 공지사항 포함 가능
            // includeHidden이 "on" 문자열로 오는 경우 처리
            boolean includeHiddenValue = includeHidden != null && 
                    (includeHidden.equals("true") || includeHidden.equals("on") || includeHidden.equals("1"));
            searchRequest.setIncludeHidden(includeHiddenValue);
            
            Page<NoticeResponseDto> notices = noticeService.search(searchRequest, pageable);
            
            model.addAttribute("notices", notices);
            model.addAttribute("searchRequest", searchRequest);
            model.addAttribute("now", java.time.LocalDateTime.now()); // 현재 시간을 모델에 추가
            model.addAttribute("title", "공지사항 관리");
            model.addAttribute("content", "components/notice/noticeList");
            
            return "layout";
        } catch (Exception e) {
            log.warn("공지사항 리스트 조회 오류: {}", e.getMessage(), e);
            model.addAttribute("error", "공지사항 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("title", "공지사항 관리");
            model.addAttribute("content", "components/notice/noticeList");
            return "layout";
        }
    }

    @GetMapping("/create")
    public String noticeCreateForm(Model model) {
        model.addAttribute("title", "공지사항 작성");
        model.addAttribute("content", "components/notice/noticeForm");
        return "layout";
    }

    @GetMapping("/{id}")
    public String noticeDetail(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.getById(id);
        model.addAttribute("notice", notice);
        model.addAttribute("now", java.time.LocalDateTime.now()); // 현재 시간을 모델에 추가
        model.addAttribute("title", "공지사항 상세");
        model.addAttribute("content", "components/notice/noticeDetail");
        return "layout";
    }

    @GetMapping("/edit/{id}")
    public String noticeEditForm(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.getById(id);
        model.addAttribute("notice", notice);
        model.addAttribute("title", "공지사항 수정");
        model.addAttribute("content", "components/notice/noticeForm");
        return "layout";
    }

    @PostMapping
    public String createNotice(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam String content,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Boolean fix,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) Boolean visible,
            @RequestParam(required = false) String startAt,
            @RequestParam(required = false) String endAt,
            Model model) {
        
        try {
            // NoticeRequestDto 생성
            NoticeRequestDto dto = new NoticeRequestDto();
            dto.setTitle(title);
            dto.setContent(content);
            dto.setCategory(NoticeCategory.valueOf(category));
            dto.setImageUrl(imageUrl);
            dto.setFix(fix != null ? fix : false);
            dto.setPriority(priority);
            dto.setVisible(visible != null ? visible : false);
            
            // 날짜 변환 (datetime-local 형식: "yyyy-MM-ddTHH:mm")
            if (startAt != null && !startAt.isEmpty()) {
                try {
                    dto.setStartAt(java.time.LocalDateTime.parse(startAt));
                } catch (Exception e) {
                    log.warn("날짜 파싱 실패 (startAt): {}", startAt);
                }
            }
            if (endAt != null && !endAt.isEmpty()) {
                try {
                    dto.setEndAt(java.time.LocalDateTime.parse(endAt));
                } catch (Exception e) {
                    log.warn("날짜 파싱 실패 (endAt): {}", endAt);
                }
            }
            
            // 공지사항 생성
            Long noticeId = noticeService.create(dto);
            
            return "redirect:/notice?message=success";
        } catch (Exception e) {
            log.warn("공지사항 등록 오류: {}", e.getMessage(), e);
            model.addAttribute("error", "공지사항 등록 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("title", "공지사항 작성");
            model.addAttribute("content", "components/notice/noticeForm");
            return "layout";
        }
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.POST, RequestMethod.PUT})
    public String updateNotice(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Boolean fix,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) Boolean visible,
            @RequestParam(required = false) String startAt,
            @RequestParam(required = false) String endAt,
            @RequestParam(required = false) String _method,
            Model model) {
        
        // PUT 요청인 경우에만 수정 로직 실행
        if (!"PUT".equals(_method)) {
            return "redirect:/notice";
        }
        
        // 필수 파라미터 검증
        if (title == null || title.trim().isEmpty()) {
            model.addAttribute("error", "제목은 필수입니다.");
            NoticeResponseDto notice = noticeService.getById(id);
            model.addAttribute("notice", notice);
            model.addAttribute("title", "공지사항 수정");
            model.addAttribute("content", "components/notice/noticeForm");
            return "layout";
        }
        
        try {
            // NoticeRequestDto 생성
            NoticeRequestDto dto = new NoticeRequestDto();
            dto.setTitle(title);
            dto.setContent(content);
            dto.setCategory(NoticeCategory.valueOf(category));
            dto.setImageUrl(imageUrl);
            dto.setFix(fix != null ? fix : false);
            dto.setPriority(priority);
            dto.setVisible(visible != null ? visible : false);
            
            // 날짜 변환 (datetime-local 형식: "yyyy-MM-ddTHH:mm")
            if (startAt != null && !startAt.isEmpty()) {
                try {
                    dto.setStartAt(java.time.LocalDateTime.parse(startAt));
                } catch (Exception e) {
                    log.warn("날짜 파싱 실패 (startAt): {}", startAt);
                }
            }
            if (endAt != null && !endAt.isEmpty()) {
                try {
                    dto.setEndAt(java.time.LocalDateTime.parse(endAt));
                } catch (Exception e) {
                    log.warn("날짜 파싱 실패 (endAt): {}", endAt);
                }
            }
            
            // 공지사항 수정
            noticeService.update(id, dto);
            
            return "redirect:/notice?message=success";
        } catch (Exception e) {
            model.addAttribute("error", "공지사항 수정 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("title", "공지사항 수정");
            model.addAttribute("content", "components/notice/noticeForm");
            return "layout";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteNotice(@PathVariable Long id) {
        try {
            noticeService.delete(id);
            return "redirect:/notice?message=success";
        } catch (Exception e) {
            return "redirect:/notice?error=삭제 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
