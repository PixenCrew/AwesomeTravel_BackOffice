package renewal.awesome_travel_backoffice.image.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.java.Log;

@Service
@Log
public class FileService {

    @Value("${imageLocation}")
    private String uploadDir;

    public String uploadFile(MultipartFile file) throws Exception {
        // 경로 없는경우 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        UUID uuid = UUID.randomUUID();
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadDir + "/" + savedFileName;
        log.info(fileUploadFullUrl);

        Path path = Paths.get(fileUploadFullUrl);
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(file.getBytes());
        fos.close();
        return savedFileName;
    }

    public boolean deleteFile(String filePath) throws Exception {
        Path path = Paths.get(".").resolve(filePath.substring(1));
        log.info("삭제 대상 : " + path.toString());

        if (Files.exists(path)) {
            Files.delete(path);
            log.info("파일을 삭제했습니다.");
            return true;
        } else {
            log.info("파일이 존재하지 않습니다.");
            return false;
        }
    }

    // // 파일 이동 ( 원본 경로, 원본경로 기준 대상 폴더)
    // public void moveFile(String filePath, String destinationFolder) throws
    // Exception {
    // Path sourcePath = Paths.get(filePath);
    // Path destinationPath = Paths.get(destinationFolder + "/" + filePath);

    // try {
    // // 파일 이동
    // Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    // log.info("파일이 성공적으로 이동되었습니다.");
    // } catch (IOException e) {
    // System.err.println("파일 이동 중 오류 발생: " + e.getMessage());
    // }

    // }
}
