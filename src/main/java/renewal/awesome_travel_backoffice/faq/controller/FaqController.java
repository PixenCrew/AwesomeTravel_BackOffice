package renewal.awesome_travel_backoffice.faq.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import renewal.awesome_travel_backoffice.faq.dto.request.FaqRequestDto;
import renewal.awesome_travel_backoffice.faq.dto.response.FaqResponseDto;
import renewal.awesome_travel_backoffice.faq.service.FaqService;
import renewal.common.entity.Faq.FaqCategory;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/faqs")
public class FaqController {

    private final FaqService faqService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody FaqRequestDto dto) {
        return ResponseEntity.ok(faqService.createFaq(dto));
    }

    @GetMapping
    public ResponseEntity<Page<FaqResponseDto>> getAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(faqService.getAllFaqs(pageable));
    }

    @GetMapping("/category")
    public ResponseEntity<Page<FaqResponseDto>> getByCategory(
            @RequestParam FaqCategory category,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(faqService.getFaqsByCategory(category, pageable));
    }


    @GetMapping("/{id}")
    public ResponseEntity<FaqResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(faqService.getFaq(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody FaqRequestDto dto) {
        faqService.updateFaq(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        faqService.deleteFaq(id);
        return ResponseEntity.ok().build();
    }
}

