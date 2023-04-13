package kz.kaznu.antiplagiarism.service;

import kz.kaznu.antiplagiarism.model.dto.ResultDto;
import kz.kaznu.antiplagiarism.utils.ShingleUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GoogleSearcherService {

    public ArrayList<String> findUrlsBySentence(String text) {
        var hrefs = getHrefElements(text);
        var urls = new ArrayList<String>();
        hrefs.stream().map(href -> href.attr("href")).filter(hrefAttr -> hrefAttr.startsWith("/url?q=")
                & !hrefAttr.contains("pdf")
                & !hrefAttr.contains("youtube")
                & !hrefAttr.contains("webcache")
                & hrefAttr.length() < 200).forEachOrdered(hrefAttr -> {
            var lastCharacter = (hrefAttr.indexOf("&sa"));
            if (lastCharacter >= 7) {
                var result = new char[lastCharacter - 7];
                hrefAttr.getChars(7, lastCharacter, result, 0);
                urls.add(String.valueOf(result));
            }
        });
        return urls;
    }

    public ResultDto getResultOfScan(String textForCheck, ArrayList<String> urls, String language) {
        var resultOfUrls = new ArrayList<String>();
        var resultPercentage = 0d;
        var hashesOfCheckingText = ShingleUtils.getArrayListOfGeneratedShingles(textForCheck, language);
        var resultOfText = new StringBuilder();
        for (var url : urls) {
            log.info("GoogleSearcherService-getResultOfScan url={}", url);
            try {
                var paragraphs = getParagraphElements(url);
                for (Element paragraph : paragraphs) {
                    resultOfText.append(" ").append(paragraph.text());
                }
                resultOfText = new StringBuilder(ShingleUtils.getCanonizedText(resultOfText.toString(), language));
                var resultOfHashes = ShingleUtils.getArrayListOfGeneratedShingles(resultOfText.toString(), language);
                var resultOfComparison = ShingleUtils.getResultOfComparison(hashesOfCheckingText, resultOfHashes);
                if (resultOfComparison > 0) resultOfUrls.add(url);
                resultPercentage = Math.max(resultOfComparison, resultPercentage);
                resultOfText.setLength(0);
            } catch (Exception e) {
                log.error("An error occurred while executing the getResultOfScan method. ", e);
            }
        }
        return ResultDto.builder().percentage(resultPercentage).urls(resultOfUrls).build();
    }

    @SneakyThrows
    public List<Element> getHrefElements(String text) {     // Elements
        String searchUrl = "https://www.google.com/search?q=" + text;
        Document document = Jsoup.connect(searchUrl).userAgent("Mozilla Chrome/4.0.249.0 Safari/532.5").followRedirects(true)
                .referrer("http://www.google.com").get();
        document.charset(StandardCharsets.UTF_8);
        return document.select("a[href]");
    }

    @SneakyThrows
    public List<Element> getParagraphElements(String url) {     // Elements
        var document = Jsoup.connect(url).userAgent("Yandex").followRedirects(true)
                .referrer("http://www.google.com").get();
        return document.select("p");
    }
}
