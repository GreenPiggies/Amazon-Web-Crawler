import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import wc.quartz.job.AmazonJob;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

public class TestAmazonJob {
    private AmazonJob job;

    @Before
    public void setup() {
        job = new AmazonJob();
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
        assertEquals("123456789", job.getProductID("https://www.amazon.com/dp/123456789/"));
    }

}
