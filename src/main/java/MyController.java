import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.concurrent.atomic.AtomicInteger;

public class MyController {
    public static void main(String[] args) throws Exception {
        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder("/tmp/amazon-web-crawler/");

        config.setPolitenessDelay(1000);

        config.setMaxDepthOfCrawling(2);

        config.setMaxPagesToFetch(1000);

        config.setIncludeBinaryContentInCrawling(false);

        config.setResumableCrawling(false);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("https://www.amazon.com/Echo-Dot/dp/B07PDHSLM6/");

        int numberOfCrawlers = 8; // number of threads used during crawling

        AtomicInteger numPagesSeen = new AtomicInteger();

        CrawlController.WebCrawlerFactory<MyCrawler> factory = () -> new MyCrawler(numPagesSeen);

        controller.start(factory, numberOfCrawlers);



    }
}
