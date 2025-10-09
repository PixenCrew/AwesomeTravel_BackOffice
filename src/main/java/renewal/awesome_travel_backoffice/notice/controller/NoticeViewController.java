package renewal.awesome_travel_backoffice.notice.controller;

import java.util.List;

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

import renewal.awesome_travel_backoffice.notice.dto.request.NoticeRequestDto;
import renewal.awesome_travel_backoffice.notice.dto.request.NoticeSearchRequest;
import renewal.awesome_travel_backoffice.notice.dto.response.NoticeResponseDto;
import renewal.awesome_travel_backoffice.notice.service.NoticeService;
import renewal.common.entity.Notice.NoticeCategory;
import renewal.common.entity.Notice.SearchType;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeViewController {

    private final NoticeService noticeService;

    @GetMapping
    public String noticeList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean fix,
            @RequestParam(required = false) Boolean includeHidden,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        
        NoticeSearchRequest searchRequest = new NoticeSearchRequest();
        searchRequest.setKeyword(keyword);
        searchRequest.setSearchType(searchType != null && !searchType.isEmpty() ? SearchType.valueOf(searchType) : null);
        searchRequest.setCategory(category != null && !category.isEmpty() ? NoticeCategory.valueOf(category) : null);
        searchRequest.setFix(fix);
        // 기본적으로는 공개 공지만 표시, 체크박스로 숨김 공지사항 포함 가능
        searchRequest.setIncludeHidden(includeHidden != null ? includeHidden : false);
        
        Page<NoticeResponseDto> notices = noticeService.search(searchRequest, pageable);
        
        model.addAttribute("notices", notices);
        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("title", "공지사항 관리");
        model.addAttribute("content", "notice/noticeList");
        
        return "layout";
    }

    @GetMapping("/create")
    public String noticeCreateForm(Model model) {
        model.addAttribute("title", "공지사항 작성");
        model.addAttribute("content", "notice/noticeForm");
        return "layout";
    }

    @GetMapping("/{id}")
    public String noticeDetail(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.getById(id);
        model.addAttribute("notice", notice);
        model.addAttribute("title", "공지사항 상세");
        model.addAttribute("content", "notice/noticeDetail");
        return "layout";
    }

    @GetMapping("/edit/{id}")
    public String noticeEditForm(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.getById(id);
        model.addAttribute("notice", notice);
        model.addAttribute("title", "공지사항 수정");
        model.addAttribute("content", "notice/noticeForm");
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
            
            // 날짜 변환
            if (startAt != null && !startAt.isEmpty()) {
                dto.setStartAt(java.time.LocalDateTime.parse(startAt));
            }
            if (endAt != null && !endAt.isEmpty()) {
                dto.setEndAt(java.time.LocalDateTime.parse(endAt));
            }
            
            // 공지사항 생성
            Long noticeId = noticeService.create(dto);
            
            return "redirect:/notice?message=success";
        } catch (Exception e) {
            model.addAttribute("error", "공지사항 등록 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("title", "공지사항 작성");
            model.addAttribute("content", "notice/noticeForm");
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
            model.addAttribute("content", "notice/noticeForm");
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
            
            // 날짜 변환
            if (startAt != null && !startAt.isEmpty()) {
                dto.setStartAt(java.time.LocalDateTime.parse(startAt));
            }
            if (endAt != null && !endAt.isEmpty()) {
                dto.setEndAt(java.time.LocalDateTime.parse(endAt));
            }
            
            // 공지사항 수정
            noticeService.update(id, dto);
            
            return "redirect:/notice?message=success";
        } catch (Exception e) {
            model.addAttribute("error", "공지사항 수정 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("title", "공지사항 수정");
            model.addAttribute("content", "notice/noticeForm");
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
