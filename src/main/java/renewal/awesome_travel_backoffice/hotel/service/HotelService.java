package renewal.awesome_travel_backoffice.hotel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.hotel.dto.reqeust.HotelRequest;
import renewal.awesome_travel_backoffice.hotel.dto.response.HotelResponse;
import renewal.awesome_travel_backoffice.hotel.entity.Hotel;
// import renewal.awesome_travel_backoffice.hotel.repository.HotelImageRepository;
import renewal.awesome_travel_backoffice.hotel.repository.HotelRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    // private final HotelImageRepository hotelImageRepository;

    // @Transactional
    // public Long createHotel(HotelRequest request) {
    //     Hotel hotel = new Hotel(
    //             request.getName(),
    //             request.getDescription(),
    //             request.getAddress(),
    //             request.getNumber(),
    //             request.getEmail(),
    //             request.getWebsite(),
    //             request.getHotelType(),
    //             request.getIsActive() != null ? request.getIsActive() : true
    //     );

    //     // 편의시설 설정
    //     hotel.getAmenities().addAll(request.getAmenities());

    //     // Hotel 먼저 저장 (ID 필요)
    //     hotelRepository.save(hotel);

    //     // 이미지 저장
    //     if (request.getImageUrls() != null) {
    //         for (String url : request.getImageUrls()) {
    //             HotelImage image = new HotelImage(url, hotel);
    //             hotel.getImages().add(image); // 양방향 연관관계 설정
    //             hotelImageRepository.save(image); // 개별 저장
    //         }
    //     }

    //     return hotel.getId();
    // }

    // public Page<HotelResponse> getHotels(Pageable pageable) {
    //     Page<Hotel> hotels = hotelRepository.findAll(pageable);

    //     return hotels.map(this::toDto);  // 자동으로 DTO 변환
    // }

    // public HotelResponse getHotelById(Long id) {
    //     Hotel hotel = hotelRepository.findById(id)
    //             .orElseThrow(() -> new IllegalArgumentException("해당 호텔이 존재하지 않습니다. ID: " + id));

    //     return toDto(hotel);
    // }

    // @Transactional
    // public void updateHotel(Long id, HotelRequest request) {
    //     Hotel hotel = hotelRepository.findById(id)
    //             .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다. ID: " + id));

    //     // 기본 필드 수정
    //     hotel.updateHotel(request);

    //     hotel.updateAmenities(request.getAmenities());

    //     hotelImageRepository.deleteAll(hotel.getImages()); // 실제 DB 삭제
    //     hotel.updateImages(request.getImageUrls());
    // }

    // @Transactional
    // public void updateHotelPartially(Long id, HotelRequest request) {
    //     Hotel hotel = hotelRepository.findById(id)
    //             .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다. ID: " + id));

    //     // 필드별 null 체크 후 변경
    //     if (request.getName() != null) hotel.updateName(request.getName());
    //     if (request.getDescription() != null) hotel.updateDescription(request.getDescription());
    //     if (request.getAddress() != null) hotel.updateAddress(request.getAddress());
    //     if (request.getNumber() != null) hotel.updateNumber(request.getNumber());
    //     if (request.getEmail() != null) hotel.updateEmail(request.getEmail());
    //     if (request.getWebsite() != null) hotel.updateWebsite(request.getWebsite());
    //     if (request.getHotelType() != null) hotel.updateHotelType(request.getHotelType());
    //     if (request.getIsActive() != null) hotel.updateIsActive(request.getIsActive());

    //     if (request.getAmenities() != null) hotel.updateAmenities(request.getAmenities());

    //     if (request.getImageUrls() != null) {
    //         hotelImageRepository.deleteAll(hotel.getImages());
    //         hotel.updateImages(request.getImageUrls());
    //     }
    // }

    // @Transactional
    // public void deleteHotel(Long id) {
    //     Hotel hotel = hotelRepository.findById(id)
    //             .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다. ID: " + id));

    //     hotelRepository.delete(hotel);
    // }


    // private HotelResponse toDto(Hotel hotel) {
    //     return HotelResponse.builder()
    //             .id(hotel.getId())
    //             .name(hotel.getName())
    //             .address(hotel.getAddress())
    //             .hotelType(hotel.getHotelType())
    //             .amenities(hotel.getAmenities())
    //             .imageUrls(
    //                     hotel.getImages().stream()
    //                             .map(HotelImage::getUrl)
    //                             .collect(Collectors.toList())
    //             )
    //             .build();
    // }

}
