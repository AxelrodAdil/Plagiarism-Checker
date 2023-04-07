package kz.kaznu.antiplagiarism.service;

import kz.kaznu.antiplagiarism.utils.GoogleSearcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
@Slf4j
public class CheckTextService {

    public Object[] start(String userText) throws IOException {
        var mainResultArray = new Object[2];
        var sentences = userText.split("\\.");
        log.info("CheckTextService mainResultArray={}, sentences={}", mainResultArray, sentences);
        var resultOfText = 0;
        var urlsList = new ArrayList<String>();
        for (int i = 0; i < sentences.length; i++) {
            var googleSearcher = new GoogleSearcher();
            var urlFoundedInGoogle = googleSearcher.findUrlsBySentence(userText);
            log.info("CheckTextService urlFoundedInGoogle={}", urlFoundedInGoogle);
            var result = googleSearcher.getResultOfScan(userText, urlFoundedInGoogle);
            log.info("CheckTextService ResultOfScan={}", result);
            resultOfText += (int) result[0];
            urlsList.addAll((ArrayList<String>) result[1]);
        }
        mainResultArray[0] = resultOfText / sentences.length;
        mainResultArray[1] = urlsList;
        log.info("CheckTextService mainResultArray={}", mainResultArray);
        return mainResultArray;
    }
}
