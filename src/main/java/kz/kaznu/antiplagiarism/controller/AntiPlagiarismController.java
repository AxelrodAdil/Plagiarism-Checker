package kz.kaznu.antiplagiarism.controller;

import kz.kaznu.antiplagiarism.service.AntiPlagiarismService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AntiPlagiarismController {

    private final AntiPlagiarismService antiPlagiarismService;

    @PostMapping(
            value = "/upload",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public String getResult(@RequestPart(value = "text", required = false) String text,
                            @RequestPart(value = "file", required = false) MultipartFile file) {
        antiPlagiarismService.validateInputTextAndFile(text, file);
        return antiPlagiarismService.getResult(text, file);
    }
}
