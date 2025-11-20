package renewal.awesome_travel_backoffice.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import renewal.awesome_travel_backoffice.product.dto.ProductFilterDTO;
import renewal.awesome_travel_backoffice.product.repository.ProductAdminRepository;
import renewal.awesome_travel_backoffice.product.repository.ProductSpecification;
import renewal.common.entity.Product;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductAdminRepository productAdminRepo;

    public Page<Product> searchProducts(ProductFilterDTO filter, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);

        // 상태 필터 처리
        if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
            if ("active".equals(filter.getStatus())) {
                spec = spec.and(ProductSpecification.isActive(true));
            } else if ("inactive".equals(filter.getStatus())) {
                spec = spec.and(ProductSpecification.isActive(false));
            }
            // "all"인 경우 상태 필터를 적용하지 않음 (모든 상품 조회)
        } else {
            // 기본적으로 활성화된 상품만 조회
            spec = spec.and(ProductSpecification.isActive(true));
        }

        if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
            spec = spec.and(ProductSpecification.titleContains(filter.getTitle()));
        }
        if (filter.getMaxPrice() != null || filter.getMinPrice() != null) {
            spec = spec.and(ProductSpecification.priceBetween(filter.getMinPrice(),filter.getMaxPrice()));
        }
        if (filter.getInfoKeyword() != null && !filter.getInfoKeyword().isEmpty()) {
            spec = spec.and(ProductSpecification.infoContains(filter.getInfoKeyword()));
        }
        if (filter.getAvgFrom() != null || filter.getAvgTo() != null ) {
            spec = spec.and(ProductSpecification.avgBetween(filter.getAvgFrom(),filter.getAvgTo()));
        }
        if (filter.getCountry() != null && !filter.getCountry().isEmpty()) {
            spec = spec.and(ProductSpecification.tourCountryEquals(filter.getCountry()));
        }
        if (filter.getStartDateFrom() != null && filter.getStartDateTo() != null) {
            spec = spec.and(ProductSpecification.tourStartDateBetween(filter.getStartDateFrom(), filter.getStartDateTo()));
        }
        if (filter.getEndDateFrom() != null && filter.getEndDateTo() != null) {
            spec = spec.and(ProductSpecification.tourEndDateBetween(filter.getEndDateFrom(), filter.getEndDateTo()));
        }

        return productAdminRepo.findAll(spec, pageable);
    }

    /**
     * 상품 ID로 상품 조회
     */
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productAdminRepo.findById(id);
    }

    /**
     * 상품 저장
     */
    @Transactional
    public Product save(Product product) {
        return productAdminRepo.save(product);
    }

    /**
     * 상품 삭제
     */
    @Transactional
    public void deleteById(Long id) {
        productAdminRepo.deleteById(id);
    }

    /**
     * 평점 업데이트 (댓글 추가/수정/삭제 시 호출)
     */
    @Transactional
    public void updateRating(Long productId, int rating, boolean isAdd) {
        Product product = productAdminRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        
        if (isAdd) {
            product.UpdateAvg(rating);
        } else {
            // 댓글 삭제 시 평점 감소 (실제 구현에서는 더 복잡한 로직 필요)
            switch (rating) {
                case 1 -> product.setStar1(Math.max(0, product.getStar1() - 1));
                case 2 -> product.setStar2(Math.max(0, product.getStar2() - 1));
                case 3 -> product.setStar3(Math.max(0, product.getStar3() - 1));
                case 4 -> product.setStar4(Math.max(0, product.getStar4() - 1));
                case 5 -> product.setStar5(Math.max(0, product.getStar5() - 1));
            }
        }
        
        productAdminRepo.save(product);
    }

    /**
     * 평점 통계 조회
     */
    @Transactional(readOnly = true)
    public ProductRatingStats getRatingStats(Long productId) {
        Product product = productAdminRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        
        return ProductRatingStats.builder()
                .totalReviews(product.getTotalReviews())
                .averageRating(product.getAverageRating())
                .star1(product.getStar1())
                .star2(product.getStar2())
                .star3(product.getStar3())
                .star4(product.getStar4())
                .star5(product.getStar5())
                .star1Percentage(product.getStarPercentage(1))
                .star2Percentage(product.getStarPercentage(2))
                .star3Percentage(product.getStarPercentage(3))
                .star4Percentage(product.getStarPercentage(4))
                .star5Percentage(product.getStarPercentage(5))
                .build();
    }

    /**
     * 평점 통계 DTO
     */
    @lombok.Builder
    @lombok.Getter
    public static class ProductRatingStats {
        private final Long totalReviews;
        private final Double averageRating;
        private final Long star1;
        private final Long star2;
        private final Long star3;
        private final Long star4;
        private final Long star5;
        private final Double star1Percentage;
        private final Double star2Percentage;
        private final Double star3Percentage;
        private final Double star4Percentage;
        private final Double star5Percentage;
    }
}

// private final HotelRepository hotelRepository;

// private final PackageRepository packageRepository;

// private final PackageHotelRepository packageHotelRepository;

// @Transactional
// public Long addHotelToPackage(Long packageId, PackageHotelRequest dto) {
// Package pack = packageRepository.findById(packageId)
// .orElseThrow(() -> new IllegalArgumentException("패키지를 찾을 수 없습니다."));

// Hotel hotel = hotelRepository.findById(dto.getHotelId())
// .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다."));

// PackageHotel packageHotel = new PackageHotel(
// pack,
// hotel,
// dto.getCheckIn(),
// dto.getCheckOut(),
// dto.getRoomType(),
// dto.getPrice(),
// dto.getReservedRooms()
// );

// packageHotelRepository.save(packageHotel);
// return packageHotel.getId();
// }
