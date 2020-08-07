import wc.dataparser.AmazonDataParser;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

public class TestAmazonDataParser {

    private AmazonDataParser parser;
    private HtmlParseData data;
    private String testHtml;

    @Before
    public void setup() {
        data = mock(HtmlParseData.class);
        parser = new AmazonDataParser(data.getHtml());
        try {
            BufferedReader buff = new BufferedReader(new FileReader("amazon_test.txt"));
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
        when(data.getHtml()).thenReturn("fjdklasfjdklsajflk;asdf");
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
        assertEquals("Echo Dot (3rd Gen) - Smart speaker with Alexa - Charcoal", parser.getName());
    }

    @Test
    public void testGetPriceInvalidHTML() {
        when(data.getHtml()).thenReturn("jkflda;jfkld;sa");
        assertEquals(0.0, parser.getPrice(), 0.0); // zero delta
    }

    @Test
    public void testGetPriceNullHTML() {
        when(data.getHtml()).thenReturn(null);
        assertEquals(0.0, parser.getPrice(), 0.0); // zero delta
    }

    @Test
    public void testGetPriceValidHTML() {
        when(data.getHtml()).thenReturn(testHtml);
        assertEquals(49.99, parser.getPrice(), 0.0);
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
        assertEquals("https://images-na.ssl-images-amazon.com/images/I/61qDKbBlcgL._AC_SY300_.jpg", parser.getMainImage());
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
        assertEquals(7, parser.getAlternateImages().size());
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
        assertEquals(3, parser.getReviews().size());
    }
}
