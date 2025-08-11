package renewal.awesome_travel_backoffice.product.service;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.product.dto.ProductFilterDTO;
import renewal.awesome_travel_backoffice.product.entity.Product;
import renewal.awesome_travel_backoffice.product.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;


    public Page<Product> searchProducts(ProductFilterDTO filter, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);
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
