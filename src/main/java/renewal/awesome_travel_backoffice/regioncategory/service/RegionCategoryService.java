package renewal.awesome_travel_backoffice.regioncategory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.regioncategory.repository.RegionCategoryRepository;
import renewal.common.entity.RegionCategory;
import renewal.common.entity.RegionCategory.CategoryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionCategoryService {

    private final RegionCategoryRepository regionCategoryRepository;

    public Page<RegionCategory> getAllCategories(Pageable pageable) {
        return regionCategoryRepository.findAllCategories(pageable);
    }

    public List<RegionCategory> getAllCategoriesList() {
        return regionCategoryRepository.findAllCategoriesList();
    }

    public Optional<RegionCategory> getCategoryById(Long id) {
        return regionCategoryRepository.findById(id);
    }

    public Page<RegionCategory> getCategoriesByType(CategoryType type, Pageable pageable) {
        return regionCategoryRepository.findByType(type, pageable);
    }

    public List<RegionCategory> getCategoriesByTypeList(CategoryType type) {
        return regionCategoryRepository.findByTypeList(type);
    }

    public List<RegionCategory> getChildCategories(Long parentId) {
        return regionCategoryRepository.findByParentId(parentId);
    }

    public List<RegionCategory> getTopLevelCategories() {
        return regionCategoryRepository.findTopLevel();
    }

    public Page<RegionCategory> searchByName(String name, Pageable pageable) {
        return regionCategoryRepository.findByNameContaining(name, pageable);
    }

    public Page<RegionCategory> searchByCode(String code, Pageable pageable) {
        return regionCategoryRepository.findByCodeContaining(code, pageable);
    }

    // 하위 카테고리 ID 모두 가져오기 (재귀)
    public List<Long> getAllChildCategoryIds(Long categoryId) {
        List<Long> result = new ArrayList<>();
        result.add(categoryId);
        
        List<RegionCategory> children = regionCategoryRepository.findByParentId(categoryId);
        for (RegionCategory child : children) {
            result.addAll(getAllChildCategoryIds(child.getId()));
        }
        
        return result;
    }

    // 상위 카테고리 체인 가져오기 (Breadcrumb용)
    public List<RegionCategory> getParentChain(Long categoryId) {
        List<RegionCategory> chain = new ArrayList<>();
        Optional<RegionCategory> current = regionCategoryRepository.findById(categoryId);
        
        while (current.isPresent()) {
            chain.add(0, current.get()); // 앞에 추가
            if (current.get().getParentId() != null) {
                current = regionCategoryRepository.findById(current.get().getParentId());
            } else {
                break;
            }
        }
        
        return chain;
    }

    @Transactional
    public RegionCategory createCategory(RegionCategory category) {
        return regionCategoryRepository.save(category);
    }

    @Transactional
    public RegionCategory updateCategory(Long id, RegionCategory updatedCategory) {
        RegionCategory category = regionCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + id));

        category.setCode(updatedCategory.getCode());
        category.setName(updatedCategory.getName());
        category.setNameEng(updatedCategory.getNameEng());
        category.setType(updatedCategory.getType());
        category.setParentId(updatedCategory.getParentId());
        category.setDisplayOrder(updatedCategory.getDisplayOrder());
        category.setCountryCode(updatedCategory.getCountryCode());

        return regionCategoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        // 하위 카테고리가 있는지 확인
        List<RegionCategory> children = regionCategoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("하위 카테고리가 있어 삭제할 수 없습니다.");
        }
        regionCategoryRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return regionCategoryRepository.existsById(id);
    }
}
