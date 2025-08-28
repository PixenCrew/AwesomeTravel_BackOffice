package renewal.awesome_travel_backoffice.image;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import renewal.awesome_travel_backoffice.image.service.FileService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final FileService fileService;

    @PostMapping
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        String savedFileName = fileService.uploadFile(file);
        return "/images/" + savedFileName; // 클라이언트에 반환되는 이미지 URL
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteImage(@RequestParam("target") String target) throws Exception {
        boolean deleted = fileService.deleteFile(target);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
