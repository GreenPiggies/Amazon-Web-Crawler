import org.junit.Before;
import org.junit.Test;
import wc.quartz.job.AmazonJob;
import wc.quartz.job.EbayJob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestEbayJob {
    private EbayJob job;

    @Before
    public void setup() {
        job = new EbayJob();
    }

    @Test
    public void testGetProductIDNullURL() {
        assertNull(job.getProductID(null));
    }

    @Test
    public void testGetProductIDInvalidURL() {
        assertEquals("", job.getProductID(""));
    }

    @Test
    public void testGetProductIDValidURL() {
        assertEquals("123456789", job.getProductID("https://www.ebay.com/itm/Cool-Item/123456789/"));
    }

}
