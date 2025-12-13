package renewal.awesome_travel_backoffice.image.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.image.entity.UploadedFile;
import renewal.awesome_travel_backoffice.image.service.FileUploadService;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileViewController {

    private final FileUploadService fileUploadService;

    /**
     * 파일 목록 화면
     */
    @GetMapping
    public String fileList(Model model) {
        List<UploadedFile> files = fileUploadService.listFiles();
        
        model.addAttribute("files", files);
        model.addAttribute("title", "파일 관리");
        model.addAttribute("content", "components/file/fileList");
        
        return "layout";
    }
}

