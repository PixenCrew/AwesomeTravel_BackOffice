package renewal.awesome_travel_backoffice.notice.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import renewal.awesome_travel_backoffice.image.LocalUploader;
import renewal.awesome_travel_backoffice.notice.dto.request.NoticeRequestDto;
import renewal.awesome_travel_backoffice.notice.dto.request.NoticeSearchRequest;
import renewal.awesome_travel_backoffice.notice.dto.response.NoticeResponseDto;
import renewal.awesome_travel_backoffice.notice.service.NoticeService;
import renewal.awesome_travel_backoffice.notice.utils.NoticeCategory;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    private final LocalUploader localUploader;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody NoticeRequestDto dto) {
        return ResponseEntity.ok(noticeService.create(dto));
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestPart MultipartFile image) {
        String imageUrl = localUploader.upload(image);
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseDto>> getAll() {
        return ResponseEntity.ok(noticeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<NoticeResponseDto>> search(
            NoticeSearchRequest noticeSearchRequest,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(noticeService.search(noticeSearchRequest, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody NoticeRequestDto dto) {
        noticeService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/fix/toggle")
    public ResponseEntity<Void> toggleFix(@PathVariable Long id) {
        noticeService.toggleFix(id);
        return ResponseEntity.ok().build();
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = Arrays.stream(NoticeCategory.values())
                .map(NoticeCategory::getDisplayName)
                .toList();
        return ResponseEntity.ok(categories);
    }

}

