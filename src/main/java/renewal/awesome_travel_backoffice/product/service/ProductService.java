package renewal.awesome_travel_backoffice.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import renewal.awesome_travel_backoffice.product.dto.ProductFilterDTO;
import renewal.awesome_travel_backoffice.product.repository.ProductRepository;
import renewal.awesome_travel_backoffice.product.repository.ProductSpecification;
import renewal.common.entity.Product;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;

    public Page<Product> searchProducts(ProductFilterDTO filter, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);

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

        return productRepo.findAll(spec, pageable);
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
