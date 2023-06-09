package kz.kaznu.antiplagiarism.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public final class ShingleUtils {

    private ShingleUtils() {
    }

    private static final String[] STOP_SYMBOLS = {".", ",", "!", "?", ":", ";", "-", "\\", "/", "*", "(", ")", "+", "@",
            "#", "$", "%", "^", "&", "=", "'", "\"", "[", "]", "{", "}", "|"};
    private static final String[] STOP_WORDS_KZ = {"әлде", "біз", "бірақ", "бірде", "болды", "болдыр", "болмаса", "болмасын",
            "болса", "болсын", "бір", "бірге", "бірнеше", "бірқанша", "егер", "енді", "және", "үшін", "қарқын", "мұнда",
            "ол", "оның", "оған", "саған", "себебі", "сіз", "сондықтан", "сонда", "сонша", "тіпті", "шейін"};
    private static final String[] STOP_WORDS_EN = {"a", "an", "the", "in", "on", "at", "to", "for", "of", "by", "with",
            "without", "than", "that", "this", "these", "those", "and", "or", "but", "nor", "yet", "so", "it", "he", "she",
            "they", "we", "you", "who", "what", "where", "when", "why", "how", "is", "am", "are", "was", "were", "be", "being",
            "been", "have", "has", "had", "do", "does", "did", "can", "could", "will", "would", "should", "may", "might", "must",
            "some", "any", "many", "few", "several", "each", "every", "all", "both", "much", "more", "most", "other", "another"};
    private static final String[] STOP_WORDS_RU = {"это", "как", "так", "и", "в", "над", "к", "до", "не", "на", "но", "за",
            "то", "с", "ли", "а", "во", "от", "со", "для", "о", "же", "ну", "вы",
            "бы", "что", "кто", "он", "она"};

    private static final String LANGUAGE_KZ = "kk";
    private static final String LANGUAGE_RU = "ru";

    private static final int SHINGLE_LEN = 2;

    private static String getCanonizedText(String text, String[] stopWordsArray) {
        for (var stopWord : stopWordsArray) {
            text = text.replace(" " + stopWord + " ", " ");
        }
        return text;
    }

    public static String getCanonizedText(String text, String language) {
        for (var stopSymbol : STOP_SYMBOLS) {
            text = text.replace(stopSymbol, "");
        }
        switch (language) {
            case LANGUAGE_RU -> text = getCanonizedText(text, STOP_WORDS_RU);
            case LANGUAGE_KZ -> text = getCanonizedText(text, STOP_WORDS_KZ);
            default -> text = getCanonizedText(text, STOP_WORDS_EN);
        }
        return text;
    }

    public static ArrayList<Integer> getArrayListOfGeneratedShingles(String textForCheck, String language) {
        var shinglesList = new ArrayList<Integer>();
        var canonizedText = getCanonizedText(textForCheck.toLowerCase(), language);
        var canonizedTextWords = canonizedText.split("[ \\n]+");
        var shinglesNumber = canonizedTextWords.length - SHINGLE_LEN;
        var shingleStringBuilder = new StringBuilder();
        for (int i = 0; i <= shinglesNumber; i++) {
            for (int j = 0; j < SHINGLE_LEN; j++) {
                shingleStringBuilder.append(canonizedTextWords[i + j]).append(" ");
            }
            shinglesList.add(shingleStringBuilder.toString().hashCode());
            shingleStringBuilder.setLength(0);
        }
        return shinglesList;
    }

    public static double getResultOfComparison(ArrayList<Integer> textShinglesFirst, ArrayList<Integer> textShinglesSecond) {
        if (textShinglesFirst == null || textShinglesSecond == null) return 0.0;
        var textShinglesNumber = textShinglesFirst.size();
        double similarShinglesNumber = textShinglesFirst.stream()
                .filter(integer -> textShinglesSecond.stream()
                        .anyMatch(integer::equals))
                .count();
        return ((similarShinglesNumber / textShinglesNumber) * 100);
    }
}
