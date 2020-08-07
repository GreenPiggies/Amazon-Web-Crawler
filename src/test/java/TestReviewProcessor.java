import wc.csv.Entry;
import wc.csv.Review;
import wc.ReviewProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestReviewProcessor {
    private Review review;
    private String requestURL;

    @Before
    public void setup() {
        review = mock(Review.class);
        requestURL = "";
    }

    @Test
    public void testValidReview() {
        when(review.getRecordID()).thenReturn("testRecordID");
        when(review.getReviewDate()).thenReturn("July 26, 2020");
        when(review.getReviewTitle()).thenReturn("awc.csv.Review Title");
        when(review.getReviewURL()).thenReturn("https://www.amazon.com/");
        when(review.getReviewText()).thenReturn("reviewText");
        when(review.getName()).thenReturn("John Smith");
        when(review.getProductID()).thenReturn("00000000");

        Entry e = ReviewProcessor.getSentiment(review, requestURL);
        assertEquals("testRecordID", e.get("RECORD_ID"));
        assertEquals("July 26, 2020", e.get("RECORD_DATETIME"));
        assertEquals("awc.csv.Review Title", e.get("RECORD_TITLE"));
        assertEquals("https://www.amazon.com/", e.get("RECORD_URL"));
        assertEquals("reviewText", e.get("RECORD_TEXT"));
        assertEquals("John Smith", e.get("AUTHOR_NAME"));
        assertEquals("00000000", e.get("META_TAGS"));
    }

    @Test
    public void testNullReview() {
        review = null;

        Entry e = ReviewProcessor.getSentiment(review, requestURL);
        assertEquals("", e.get("RECORD_ID"));
        assertEquals("", e.get("RECORD_DATETIME"));
        assertEquals("", e.get("RECORD_TITLE"));
        assertEquals("", e.get("RECORD_URL"));
        assertEquals("", e.get("RECORD_TEXT"));
        assertEquals("", e.get("AUTHOR_NAME"));
        assertEquals("", e.get("META_TAGS"));
    }
}
