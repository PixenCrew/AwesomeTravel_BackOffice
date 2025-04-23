package renewal.awesome_travel_backoffice.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.product.dto.request.PackageHotelRequest;
import renewal.awesome_travel_backoffice.hotel.entity.Hotel;
import renewal.awesome_travel_backoffice.hotel.repository.HotelRepository;
import renewal.awesome_travel_backoffice.product.entity.PackageHotel;
import renewal.awesome_travel_backoffice.product.repository.PackageHotelRepository;
import renewal.awesome_travel_backoffice.product.repository.PackageRepository;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final HotelRepository hotelRepository;

    private final PackageRepository packageRepository;

    private final PackageHotelRepository packageHotelRepository;



    @Transactional
    public Long addHotelToPackage(Long packageId, PackageHotelRequest dto) {
        Package pack = packageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("패키지를 찾을 수 없습니다."));

        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다."));

        PackageHotel packageHotel = new PackageHotel(
                pack,
                hotel,
                dto.getCheckIn(),
                dto.getCheckOut(),
                dto.getRoomType(),
                dto.getPrice(),
                dto.getReservedRooms()
        );

        packageHotelRepository.save(packageHotel);
        return packageHotel.getId();
    }
}
