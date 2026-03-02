package renewal.awesome_travel_backoffice.menuCode.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import renewal.common.entity.MenuCode;
import renewal.common.entity.Product;
import renewal.common.entity.MenuCode.MenuCodeDetail.TargetColumn;
import renewal.common.repository.ProductRepository;

/**
 * 메뉴코드 기준으로 노출되는 상품 조회 (COUNTRY/CITY 상세 항목으로 매칭)
 */
@Service
@RequiredArgsConstructor
public class MenuCodeService {

    private final ProductRepository productRepository;

    /**
     * 이 메뉴코드에 노출되는 상품 목록 (국가 또는 도시 조건에 맞는 상품, 중복 제거)
     */
    @Transactional(readOnly = true)
    public List<Product> findProductsByMenuCode(MenuCode menuCode) {
        if (menuCode == null || menuCode.getDetails() == null || menuCode.getDetails().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> countryCodes = menuCode.getDetails().stream()
                .filter(d -> d.getTargetColumn() == TargetColumn.COUNTRY && d.getValue() != null && !d.getValue().isBlank())
                .map(d -> d.getValue().trim())
                .distinct()
                .toList();

        List<String> cityCodes = menuCode.getDetails().stream()
                .filter(d -> d.getTargetColumn() == TargetColumn.CITY && d.getValue() != null && !d.getValue().isBlank())
                .map(d -> d.getValue().trim())
                .distinct()
                .toList();

        if (countryCodes.isEmpty() && cityCodes.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> seenIds = new LinkedHashSet<>();
        List<Product> result = new ArrayList<>();

        if (!countryCodes.isEmpty()) {
            List<Product> byCountry = productRepository.findAllByTour_Country_CountryCodeIn(countryCodes);
            for (Product p : byCountry) {
                if (seenIds.add(p.getId())) {
                    result.add(p);
                }
            }
        }
        if (!cityCodes.isEmpty()) {
            List<Product> byCity = productRepository.findDistinctByTour_Schedules_Locations_CityCode_CityCodeIn(cityCodes);
            for (Product p : byCity) {
                if (seenIds.add(p.getId())) {
                    result.add(p);
                }
            }
        }

        return result;
    }
}
