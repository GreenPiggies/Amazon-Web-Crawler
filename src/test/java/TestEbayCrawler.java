import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestEbayCrawler {

    private EbayCrawler crawler;
    private AtomicInteger seenPages;
    private WebURL url;
    private String testURL;

    @Before
    public void setup() {
        seenPages = new AtomicInteger();
        crawler = new EbayCrawler(seenPages, mock(PrintWriter.class));
        url = mock(WebURL.class);
        testURL = "https://www.ebay.com/itm/Logitech-G640-Large-Cloth-Gaming-Mousepad/303427616970";
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
        when(url.getURL()).thenReturn("https://www.ebay.com/");
        assertFalse(crawler.shouldVisit(mock(Page.class), url));
    }

    @Test
    public void testShouldVisitValidURL() {
        when(url.getURL()).thenReturn(testURL);
        assertTrue(crawler.shouldVisit(mock(Page.class), url));
    }

    @Test
    public void testShouldVisitNullPage() {
        assertFalse(crawler.shouldVisit(null, url));
    }
}
