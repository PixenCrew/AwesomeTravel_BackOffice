package renewal.awesome_travel_backoffice.hotel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.hotel.dto.reqeust.HotelRequest;
import renewal.awesome_travel_backoffice.hotel.dto.response.HotelResponse;
import renewal.awesome_travel_backoffice.hotel.service.HotelService;

@Controller
@RequestMapping("/hotel")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    // @PostMapping
    // public ResponseEntity<Long> createHotel(@RequestBody HotelRequest hotelRequest) {
    //     Long hotelId = hotelService.createHotel(hotelRequest);
    //     return ResponseEntity.ok(hotelId);
    // }

    // @GetMapping
    // public ResponseEntity<Page<HotelResponse>> getHotels(Pageable pageable) {
    //     Page<HotelResponse> hotels = hotelService.getHotels(pageable);
    //     return ResponseEntity.ok(hotels);
    // }

    // @GetMapping("/{id}")
    // public ResponseEntity<HotelResponse> getHotelById(@PathVariable Long id) {
    //     HotelResponse hotel = hotelService.getHotelById(id);
    //     return ResponseEntity.ok(hotel);
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<Void> updateHotel(@PathVariable Long id,
    //                                         @RequestBody HotelRequest request) {
    //     hotelService.updateHotel(id, request);
    //     return ResponseEntity.ok().build();
    // }

    // @PatchMapping("/{id}")
    // public ResponseEntity<Void> updateHotelPartially(@PathVariable Long id,
    //                                                  @RequestBody HotelRequest request) {
    //     hotelService.updateHotelPartially(id, request);
    //     return ResponseEntity.ok().build();
    // }


    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
    //     hotelService.deleteHotel(id);
    //     return ResponseEntity.noContent().build(); // 204
    // }




}
