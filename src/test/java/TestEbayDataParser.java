import wc.dataparser.EbayDataParser;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestEbayDataParser {

    private EbayDataParser parser;
    private HtmlParseData data;
    private String testHtml;

    @Before
    public void setup() {
        data = mock(HtmlParseData.class);
        parser = new EbayDataParser(data.getHtml());
        try {
            BufferedReader buff = new BufferedReader(new FileReader("ebay_test.txt"));
            StringBuffer str = new StringBuffer();
            String line;
            while ((line = buff.readLine()) != null) {
                str.append(line);
            }
            testHtml = str.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetNameInvalidHTML() {
        when(data.getHtml()).thenReturn("");
        assertNull(parser.getName());
    }

    @Test
    public void testGetNameNullHTML() {
        when(data.getHtml()).thenReturn(null);
        assertNull(parser.getName());
    }

    @Test
    public void testGetNameValidHTML() {
        when(data.getHtml()).thenReturn(testHtml);
        assertEquals("Logitech G640 Large Cloth Gaming Mousepad", parser.getName());
    }

    @Test
    public void testGetBuyPriceInvalidHTML() {
        when(data.getHtml()).thenReturn("");
        assertEquals(0.0, parser.getPrice(), 0.0);
    }

    @Test
    public void testGetBuyPriceNullHTML() {
        when(data.getHtml()).thenReturn(null);
        assertEquals(0.0, parser.getPrice(), 0.0);
    }

    @Test
    public void testGetBuyPriceValidHTML() {
        when(data.getHtml()).thenReturn(testHtml);
        assertEquals(24.98, parser.getPrice(), 0.0);
    }

    @Test
    public void testGetReviewsInvalidHTML() {
        when(data.getHtml()).thenReturn("jkflda;jfkld;sa");
        assertEquals(0, parser.getReviews().size());
    }

    @Test
    public void testGetReviewsNullHTML() {
        when(data.getHtml()).thenReturn(null);
        assertNull(parser.getReviews());
    }

    @Test
    public void testGetReviewsValidHTML() {
        when(data.getHtml()).thenReturn(testHtml);
        assertEquals(1, parser.getReviews().size());
    }

    @Test
    public void testGetAltImagesInvalidHTML() {
        when(data.getHtml()).thenReturn("jkflda;jfkld;sa");
        assertEquals(0, parser.getAlternateImages().size());
    }

    @Test
    public void testGetAltImagesNullHTML() {
        when(data.getHtml()).thenReturn(null);
        assertNull(parser.getAlternateImages());
    }

    @Test
    public void testGetAltImagesValidHTML() {
        when(data.getHtml()).thenReturn(testHtml);
        assertEquals(3, parser.getAlternateImages().size());
    }

    @Test
    public void testGetMainImageInvalidHTML() {
        when(data.getHtml()).thenReturn("jkflda;jfkld;sa");
        assertNull(parser.getMainImage());
    }

    @Test
    public void testGetMainImageNullHTML() {
        when(data.getHtml()).thenReturn(null);
        assertNull(parser.getMainImage());
    }

    @Test
    public void testGetMainImageValidHTML() {
        when(data.getHtml()).thenReturn(testHtml);
        assertEquals("https://i.ebayimg.com/images/g/vbMAAOSwCOJeDDDr/s-l300.jpg", parser.getMainImage());
    }



}
