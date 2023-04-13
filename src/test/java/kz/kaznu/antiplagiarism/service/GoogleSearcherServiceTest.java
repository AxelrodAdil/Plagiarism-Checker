package kz.kaznu.antiplagiarism.service;

import kz.kaznu.antiplagiarism.model.dto.ResultDto;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleSearcherServiceTest {

    private static final String TEXT_FOR_CHECK = "This is a test paragraph";
    private static final String LANGUAGE = "en";

    @Spy
    GoogleSearcherService googleSearcherService = new GoogleSearcherService();

    @Test
    @DisplayName("Test for findUrlsBySentence method")
    public void test_findUrlsBySentence() {
        Element mockedElement1 = mock(Element.class);
        when(mockedElement1.attr("href")).thenReturn("/url?q=https://www.example.com&sa=example");
        Element mockedElement2 = mock(Element.class);
        when(mockedElement2.attr("href")).thenReturn("/url?q=https://www.example.com/2&sa=example-2");
        Element mockedElement3 = mock(Element.class);
        when(mockedElement3.attr("href")).thenReturn("/url?q=https://www.example.com/3&sa=example-3");
        Element mockedElement4 = mock(Element.class);
        when(mockedElement4.attr("href")).thenReturn("/url?q=https://www.example.com/pdf&sa=example-pdf");
        Element mockedElement5 = mock(Element.class);
        when(mockedElement5.attr("href")).thenReturn("/url?q=https://www.example.com/youtube&sa=example-youtube");
        Element mockedElement6 = mock(Element.class);
        when(mockedElement6.attr("href")).thenReturn("/url?q=https://www.example.com/webcache&sa=example-webcache");
        Element mockedElement7 = mock(Element.class);
        when(mockedElement7.attr("href")).thenReturn("/url?q=https://www.example.com/long-url-that-is-definitely-too" +
                "-long-for-this-purpose&sa=example-long-url-that-is-definitely-too-long-for-this-purpose" +
                "-long-url-that-is-definitely-too-long-for-this-purpose");

        List<Element> elementList = new ArrayList<>();
        elementList.add(mockedElement1);
        elementList.add(mockedElement2);
        elementList.add(mockedElement3);
        elementList.add(mockedElement4);
        elementList.add(mockedElement5);
        elementList.add(mockedElement6);
        elementList.add(mockedElement7);
        doReturn(elementList).when(googleSearcherService).getHrefElements(anyString());

        ArrayList<String> result = googleSearcherService.findUrlsBySentence(TEXT_FOR_CHECK);
        verify(mockedElement1, times(1)).attr("href");
        verify(mockedElement2, times(1)).attr("href");
        verify(mockedElement3, times(1)).attr("href");
        verify(mockedElement4, times(1)).attr("href");
        verify(mockedElement5, times(1)).attr("href");
        verify(mockedElement6, times(1)).attr("href");
        verify(mockedElement7, times(1)).attr("href");
        assertEquals(3, result.size());
        assertTrue(result.contains("https://www.example.com"));
        assertTrue(result.contains("https://www.example.com/2"));
        assertTrue(result.contains("https://www.example.com/3"));
    }

    @Test
    void getResultOfScan() {
        var urls = new ArrayList<>(Arrays.asList("https://www.example.com", "https://www.example.com/2"));
        Element mockedElement1 = mock(Element.class);
        when(mockedElement1.text()).thenReturn("This is a test paragraph on example.com");
        Element mockedElement2 = mock(Element.class);
        when(mockedElement2.text()).thenReturn("This is another test paragraph on example.com");
        List<Element> elementList = new ArrayList<>();
        elementList.add(mockedElement1);
        elementList.add(mockedElement2);
        doReturn(elementList).when(googleSearcherService).getParagraphElements(urls.get(0));

        Element mockedElement3 = mock(Element.class);
        when(mockedElement3.text()).thenReturn("This is a test paragraph on example.com/2");
        Element mockedElement4 = mock(Element.class);
        when(mockedElement4.text()).thenReturn("This is another test paragraph on example.com/2");
        List<Element> elementList2 = new Elements();
        elementList2.add(mockedElement3);
        elementList2.add(mockedElement4);
        doReturn(elementList2).when(googleSearcherService).getParagraphElements(urls.get(1));

        ResultDto result = googleSearcherService.getResultOfScan(TEXT_FOR_CHECK, urls, LANGUAGE);
        assertEquals(50, result.getPercentage());
        assertEquals(2, result.getUrls().size());
        assertEquals("https://www.example.com", result.getUrls().get(0));
        assertEquals("https://www.example.com/2", result.getUrls().get(1));
        verify(googleSearcherService, times(2)).getParagraphElements(anyString());
        verify(mockedElement1, times(1)).text();
        verify(mockedElement2, times(1)).text();
        verify(mockedElement3, times(1)).text();
        verify(mockedElement4, times(1)).text();
    }
}