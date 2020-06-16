import java.util.concurrent.atomic.AtomicInteger;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class BasicCrawlController {

    public static void main(String[] args) throws Exception {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder("/tmp/crawler4j/");
        config.setPolitenessDelay(1000);
        config.setMaxDepthOfCrawling(2);
        config.setMaxPagesToFetch(1000);
        config.setIncludeBinaryContentInCrawling(false);

        // Do you need to set a proxy? If so, you can use:
        // config.setProxyHost("proxyserver.example.com");
        // config.setProxyPort(8080);

        // If your proxy also needs authentication:
        // config.setProxyUsername(username); config.getProxyPassword(password);

        config.setResumableCrawling(false);
        // config.setHaltOnError(true);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // Starting seeds for the crawler.
        controller.addSeed("https://www.ics.uci.edu/");
        controller.addSeed("https://www.ics.uci.edu/~lopes/");
        controller.addSeed("https://www.ics.uci.edu/~welling/");

        // Number of threads to use during crawling.
        int numberOfCrawlers = 8;

        // To demonstrate an example of how you can pass objects to crawlers, we use an AtomicInteger that crawlers
        // increment whenever they see a url which points to an image.
        AtomicInteger numSeenImages = new AtomicInteger();

        // The factory which creates instances of crawlers.
        CrawlController.WebCrawlerFactory<BasicCrawler> factory = () -> new BasicCrawler(numSeenImages);

        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(factory, numberOfCrawlers);
    }

}
