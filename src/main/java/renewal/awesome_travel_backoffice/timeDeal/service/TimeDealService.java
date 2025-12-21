package renewal.awesome_travel_backoffice.timeDeal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.timeDeal.dto.TimeDealFilterDTO;
import renewal.awesome_travel_backoffice.timeDeal.repository.TimeDealSpecification;
import renewal.common.entity.TimeDeal;
import renewal.common.repository.TimeDealRepository;

@Service
@RequiredArgsConstructor
public class TimeDealService {

    private final TimeDealRepository timeDealRepository;

    @Transactional(readOnly = true)
    public Page<TimeDeal> searchTimeDeals(TimeDealFilterDTO filter, Pageable pageable) {
        Specification<TimeDeal> spec = buildSpecification(filter);
        return timeDealRepository.findAll(spec, pageable);
    }

    private Specification<TimeDeal> buildSpecification(TimeDealFilterDTO filter) {
        Specification<TimeDeal> spec = Specification.where(null);

        if (filter.getDiscountType() != null) {
            spec = spec.and(TimeDealSpecification.discountTypeEquals(filter.getDiscountType()));
        }

        if (filter.getActive() != null) {
            spec = spec.and(TimeDealSpecification.isActive(filter.getActive()));
        }

        if (filter.getStartDateFrom() != null || filter.getStartDateTo() != null) {
            spec = spec.and(TimeDealSpecification.startTimeBetween(
                filter.getStartDateFrom(), filter.getStartDateTo()));
        }

        if (filter.getEndDateFrom() != null || filter.getEndDateTo() != null) {
            spec = spec.and(TimeDealSpecification.endTimeBetween(
                filter.getEndDateFrom(), filter.getEndDateTo()));
        }

        if (filter.getMinValue() != null || filter.getMaxValue() != null) {
            spec = spec.and(TimeDealSpecification.valueBetween(
                filter.getMinValue(), filter.getMaxValue()));
        }

        return spec;
    }
}

