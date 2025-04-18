package renewal.awesome_travel_backoffice.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renewal.awesome_travel_backoffice.product.dto.request.PackageHotelRequest;
import renewal.awesome_travel_backoffice.product.service.PackageService;

@RestController
@RequestMapping("/admin/package")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @PostMapping("/{packageId}/hotels")
    public ResponseEntity<Long> addHotelToPackage(@PathVariable Long packageId,
                                                  @RequestBody PackageHotelRequest dto) {
        Long id = packageService.addHotelToPackage(packageId, dto);
        return ResponseEntity.ok(id);
    }

}
