package renewal.awesome_travel_backoffice.promotion.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.promotion.dto.PromotionFilterDTO;
import renewal.awesome_travel_backoffice.promotion.repository.PromotionSpecification;
import renewal.common.entity.Promotion;
import renewal.common.repository.PromotionRepository;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    @Transactional(readOnly = true)
    public Page<Promotion> searchPromotions(PromotionFilterDTO filter, Pageable pageable) {
        Specification<Promotion> spec = buildSpecification(filter);
        return promotionRepository.findAll(spec, pageable);
    }

    private Specification<Promotion> buildSpecification(PromotionFilterDTO filter) {
        Specification<Promotion> spec = Specification.where(null);

        if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
            spec = spec.and(PromotionSpecification.titleContains(filter.getTitle()));
        }

        if (filter.getMenuCodeCode() != null && !filter.getMenuCodeCode().isEmpty()) {
            spec = spec.and(PromotionSpecification.menuCodeEquals(filter.getMenuCodeCode()));
        }

        if (filter.getActive() != null) {
            spec = spec.and(PromotionSpecification.isActive(filter.getActive()));
        }

        if (filter.getStartDateFrom() != null || filter.getStartDateTo() != null) {
            spec = spec.and(PromotionSpecification.startTimeBetween(
                filter.getStartDateFrom(), filter.getStartDateTo()));
        }

        if (filter.getEndDateFrom() != null || filter.getEndDateTo() != null) {
            spec = spec.and(PromotionSpecification.endTimeBetween(
                filter.getEndDateFrom(), filter.getEndDateTo()));
        }

        return spec;
    }
}

