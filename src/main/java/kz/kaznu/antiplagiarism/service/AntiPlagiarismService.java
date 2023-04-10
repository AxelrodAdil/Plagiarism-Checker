package kz.kaznu.antiplagiarism.service;

import kz.kaznu.antiplagiarism.exception.BadRequestException;
import kz.kaznu.antiplagiarism.model.Result;
import kz.kaznu.antiplagiarism.repos.ResultRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AntiPlagiarismService {

    private final ResultRepo resultRepo;
    private final CheckTextService checkTextService;

    static final List<String> allowedExtensionList = Arrays.asList("pdf", "docx");

    public String getResult(String text, MultipartFile file, String language) {
        log.info("GetResult text={}, file={}", text, file);
        if (!file.isEmpty()) {
            text = extractTextFromFile(file);
            log.info("GetResult-final text={}", text);
        }
        try {
            var resultCharacter = checkTextService.start(text, language);
            double resultPercentage = 100 - (int) resultCharacter[0];
            var dateNow = new Date();
            var formatForDateNow = new SimpleDateFormat("E yyyy.MM.dd");
            var result = getNewResult(resultPercentage, formatForDateNow.format(dateNow));
            log.info("GetResult result={}", result);
            resultRepo.save(result);
            return String.valueOf(result.getResult());
        } catch (Exception e) {
            log.error("AntiPlagiarismService-getResult", e);
            throw new RuntimeException();
        }
    }

    private Result getNewResult(Double resultPercentage, String date) {
        Result result = new Result();
        result.setDate(date);
        result.setResult(resultPercentage);
        return result;
    }

    private String extractTextFromFile(MultipartFile file) {
        var extension = getExtension(file);
        return extension.equals("docx") ? extractTextFromDocxFile(file) : extractTextFromPdfFile(file);
    }

    private String extractTextFromDocxFile(MultipartFile file) {
//        The file format for .docx files is different from the older .doc file format.
//        .docx files are based on the Office Open XML (OOXML) format, which uses a different file structure
//        than the older binary formats.
        try {
            var text = new StringBuilder();
            try (InputStream inputStream = file.getInputStream()) {
                XWPFDocument document = new XWPFDocument(inputStream);
                document.getParagraphs().stream().map(XWPFParagraph::getText).forEach(text::append);
            }
            log.info("AntiPlagiarismService-extractTextFromDocxFile text={}", text);
            return text.toString();
        } catch (IOException e) {
            log.error("An error occurred while executing the extractTextFromDocxFile method. ", e);
            throw new BadRequestException("An error occurred while executing the AntiPlagiarismService-extractTextFromDocxFile method.");
        }
    }

    private String extractTextFromPdfFile(MultipartFile file) {
        try {
            var inputStream = file.getInputStream();
            try (PDDocument document = PDDocument.load(inputStream)) {
                if (!document.isEncrypted()) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(document);
                } else {
                    throw new IOException("Cannot extract text from encrypted PDF file");
                }
            }
        } catch (IOException e) {
            log.error("An error occurred while executing the extractTextFromPdfFile method. ", e);
            throw new BadRequestException("An error occurred while executing the AntiPlagiarismService-extractTextFromPdfFile method.");
        }
    }

    private String getExtension(MultipartFile file){
        return FilenameUtils.getExtension(file.getOriginalFilename());
    }

    public void validateInputTextAndFile(String text, MultipartFile file) {
        if (!file.isEmpty()) {
            String extension = getExtension(file);
            log.info("AntiPlagiarismService-validateInputTextAndFile extension={}", extension);
            if (!allowedExtensionList.contains(extension)){
                log.error("Not allowed extension {}", extension);
                throw new BadRequestException("Not allowed extension");
            }
        } else if (text == null || text.isEmpty()) {
            throw new BadRequestException("Not received required parameters");
        }
    }
}
