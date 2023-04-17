package kz.kaznu.antiplagiarism.controller;

import kz.kaznu.antiplagiarism.model.dto.ResultDto;
import kz.kaznu.antiplagiarism.service.AntiPlagiarismService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AntiPlagiarismController {

    private final AntiPlagiarismService antiPlagiarismService;

//    @CrossOrigin(origins = "*")
    @PostMapping(
            value = "/upload",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<ResultDto> getResult(@RequestPart(value = "text", required = false) String text,
                                               @RequestPart(value = "file", required = false) MultipartFile file,
                                               @RequestPart(value = "lang", required = false) String language) {
        antiPlagiarismService.validateInputTextAndFile(text, file);
        var resultDto = antiPlagiarismService.getResultDto(text, file, language);
        return ResponseEntity.ok(resultDto);
    }
}
