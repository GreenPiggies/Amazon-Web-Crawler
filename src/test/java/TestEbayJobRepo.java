import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import wc.jobrepo.AmazonJobRepo;
import wc.jobrepo.EbayJobRepo;
import wc.quartz.job.AmazonJob;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

public class TestEbayJobRepo {
    private EbayJobRepo repo;

    @Before
    public void setup() {
        repo = EbayJobRepo.getInstance();
    }

    @Test
    public void testHasNoNextLink() {
        repo.getNextLink();
        assertFalse(repo.hasNextLink());
    }

    @Test
    public void testHasNextLink() {
        repo.addLink("link");
        assertTrue(repo.hasNextLink());
    }

    @Test
    public void testNoCrawlersRunning() {
        assertFalse(repo.stillRunning());
    }

    @Test
    public void testCertainCrawlerIsRunning() {
        repo.start(0);
        assertTrue(repo.isRunning(0));
    }

    @Test
    public void testCertainCrawlerIsNotRunning() {
        assertFalse(repo.isRunning(1));
    }

    @Test
    public void testCrawlersAreRunning() {
        repo.start(0);
        assertTrue(repo.stillRunning());
    }

}