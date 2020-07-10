import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class TestAmazonCrawler {
    private AmazonCrawler crawler;
    private AtomicInteger seenPages;
    private WebURL url;
    private String testURL;

    @Before
    public void setup() {
        seenPages = new AtomicInteger();
        crawler = new AmazonCrawler(seenPages);
        url = mock(WebURL.class);
        testURL = "https://www.amazon.com/Echo-Dot/dp/B07FZ8S74R/";
    }

    @Test
    public void testShouldVisitBadURL() {
        when(url.getURL()).thenReturn("jkfld;ajfklds;a");
        assertFalse(crawler.shouldVisit(mock(Page.class), url));
    }

    @Test
    public void testShouldVisitNullURL() {
        when(url.getURL()).thenReturn(null);
        assertFalse(crawler.shouldVisit(mock(Page.class), url));
    }

    @Test
    public void testShouldVisitNullWebURL() {
        assertFalse(crawler.shouldVisit(mock(Page.class), null));
    }

    @Test
    public void testShouldVisitNonproductURL() {
        when(url.getURL()).thenReturn("https://www.amazon.com/");
        assertFalse(crawler.shouldVisit(mock(Page.class), url));
    }

    @Test
    public void testShouldVisitValidURL() {
        when(url.getURL()).thenReturn(testURL);
        assertTrue(crawler.shouldVisit(mock(Page.class), url));
    }

    @Test
    public void testShouldVisitNullPage() {
        when(url.getURL()).thenReturn(testURL);
        assertFalse(crawler.shouldVisit(null, url));
    }





}
