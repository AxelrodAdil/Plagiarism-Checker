package kz.kaznu.antiplagiarism.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Slf4j
public class GoogleSearcher {

    public ArrayList<String> findUrlsBySentence(String text) throws IOException {
        String searchUrl = "https://www.google.com/search?q=" + text;
        Document document = Jsoup.connect(searchUrl).userAgent("Mozilla Chrome/4.0.249.0 Safari/532.5").followRedirects(true)
                .referrer("http://www.google.com").get();
        document.charset(StandardCharsets.UTF_8);
        Elements hrefs = document.select("a[href]");
        var urls = new ArrayList<String>();
        hrefs.stream().filter(href -> href.attr("href").contains("/url?q=")
                & !href.attr("href").contains("pdf")
                & !href.attr("href").contains("youtube")
                & href.attr("href").length() < 200).forEachOrdered(href -> {
            var lastCharacter = (href.attr("href").indexOf("&sa"));
            var result = new char[lastCharacter - 7];
            href.attr("href").getChars(7, lastCharacter, result, 0);
            urls.add(String.valueOf(result));
        });
        return urls;
    }

    public Object[] getResultOfScan(String textForCheck, ArrayList<String> urls, String language) {
        var shingle = new Shingle();
        Object[] mainResultArray = new Object[2];
        var resultOfUrls = new ArrayList<String>();
        var resultPercentage = 0d;
        var hashesOfCheckingText = shingle.getArrayListOfGeneratedShingles(textForCheck, language);
        var resultOfText = new StringBuilder();
        for (var url : urls) {
            log.info("GoogleSearcher-getResultOfScan url={}", url);
            try {
                var document = Jsoup.connect(url).userAgent("Yandex").followRedirects(true)
                        .referrer("http://www.google.com").get();
                Elements paragraphs = document.select("p");
                for (Element paragraph : paragraphs) {
                    resultOfText.append(" ").append(paragraph.text());
                }
                resultOfText = new StringBuilder(shingle.getCanonizedText(resultOfText.toString(), language));
                var resultOfHashes = shingle.getArrayListOfGeneratedShingles(resultOfText.toString(), language);
                if (shingle.getResultOfComparison(hashesOfCheckingText, resultOfHashes) > 0) {
                    resultOfUrls.add(url);
                }
                resultPercentage = Math.max(shingle.getResultOfComparison(hashesOfCheckingText, resultOfHashes), resultPercentage);
                resultOfText.setLength(0);
            } catch (Exception e) {
                log.error("An error occurred while executing the getResultOfScan method. ", e);
            }
        }
        mainResultArray[0] = (int) resultPercentage;
        mainResultArray[1] = resultOfUrls;
        return mainResultArray;
    }
}
