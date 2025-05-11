package renewal.awesome_travel_backoffice.image;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/upload")
@RequiredArgsConstructor
public class ImageUploadController {

    @Value("${imageLocation}")
    private String uploadDir;

    @PostMapping("/image")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists())
            dir.mkdirs();

        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String uuid = UUID.randomUUID() + ext;
        Path savePath = Paths.get(uploadDir, uuid);
        file.transferTo(savePath.toFile());

        return "/images/" + uuid; // 클라이언트에 반환되는 이미지 URL
    }
}
