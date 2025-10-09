package renewal.awesome_travel_backoffice.regioncategory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.regioncategory.service.RegionCategoryService;
import renewal.common.entity.RegionCategory;
import renewal.common.entity.RegionCategory.CategoryType;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/region-category")
@RequiredArgsConstructor
public class RegionCategoryController {

    private final RegionCategoryService regionCategoryService;

    @GetMapping
    public String categoryList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "displayOrder") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String searchKeyword,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RegionCategory> categoryPage;

        if (type != null && !type.isEmpty() && searchKeyword != null && !searchKeyword.isEmpty()) {
            categoryPage = regionCategoryService.searchByName(searchKeyword, pageable);
        } else if (type != null && !type.isEmpty()) {
            categoryPage = regionCategoryService.getCategoriesByType(CategoryType.valueOf(type), pageable);
        } else if (searchKeyword != null && !searchKeyword.isEmpty()) {
            categoryPage = regionCategoryService.searchByName(searchKeyword, pageable);
        } else {
            categoryPage = regionCategoryService.getAllCategories(pageable);
        }

        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("allCategories", regionCategoryService.getAllCategoriesList());
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("selectedType", type);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("title", "지역 카테고리 관리");
        model.addAttribute("content", "components/regioncategory/regioncategory");

        return "layout";
    }

    @GetMapping("/{id}")
    public String categoryDetail(@PathVariable Long id, Model model) {
        Optional<RegionCategory> category = regionCategoryService.getCategoryById(id);
        if (category.isEmpty()) {
            return "redirect:/region-category?error=카테고리를 찾을 수 없습니다.";
        }

        List<RegionCategory> parentChain = regionCategoryService.getParentChain(id);
        List<RegionCategory> children = regionCategoryService.getChildCategories(id);

        model.addAttribute("category", category.get());
        model.addAttribute("parentChain", parentChain);
        model.addAttribute("children", children);
        model.addAttribute("title", "지역 카테고리 상세");
        model.addAttribute("content", "components/regioncategory/regioncategoryDetail");

        return "layout";
    }

    @GetMapping("/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new RegionCategory());
        model.addAttribute("allCategories", regionCategoryService.getAllCategoriesList());
        model.addAttribute("title", "새 카테고리 등록");
        model.addAttribute("content", "components/regioncategory/regioncategoryForm");

        return "layout";
    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        Optional<RegionCategory> category = regionCategoryService.getCategoryById(id);
        if (category.isEmpty()) {
            return "redirect:/region-category?error=카테고리를 찾을 수 없습니다.";
        }

        model.addAttribute("category", category.get());
        model.addAttribute("allCategories", regionCategoryService.getAllCategoriesList());
        model.addAttribute("title", "카테고리 수정");
        model.addAttribute("content", "components/regioncategory/regioncategoryForm");

        return "layout";
    }

    @PostMapping
    public String saveCategory(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String code,
            @RequestParam String name,
            @RequestParam(required = false) String nameEng,
            @RequestParam String type,
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "0") Integer displayOrder,
            @RequestParam(required = false) String countryCode,
            Model model) {

        try {
            RegionCategory category = new RegionCategory(
                code, name, nameEng, CategoryType.valueOf(type), 
                parentId, displayOrder, countryCode
            );

            if (id != null) {
                category.setId(id);
                regionCategoryService.updateCategory(id, category);
            } else {
                regionCategoryService.createCategory(category);
            }

            return "redirect:/region-category?message=success";
        } catch (Exception e) {
            model.addAttribute("error", "저장 중 오류: " + e.getMessage());
            model.addAttribute("category", new RegionCategory(code, name, nameEng, CategoryType.valueOf(type), parentId, displayOrder, countryCode));
            model.addAttribute("allCategories", regionCategoryService.getAllCategoriesList());
            model.addAttribute("title", id != null ? "카테고리 수정" : "새 카테고리 등록");
            model.addAttribute("content", "components/regioncategory/regioncategoryForm");
            return "layout";
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            regionCategoryService.deleteCategory(id);
            return ResponseEntity.ok("카테고리가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }
}
