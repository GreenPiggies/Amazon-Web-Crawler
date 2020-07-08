import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The AmazonController class is the "runner" class for my web crawler.
 */
public class EbayController{
    public static void main(String[] args) throws Exception {




        CrawlConfig config = new CrawlConfig();

        config.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.74 Safari/537.36 Edg/79.0.309.43");
        config.setMaxDownloadSize(2000000000);
        // this folder stores all the data that the crawler needs to store
        config.setCrawlStorageFolder("/tmp/ebay-web-crawler/");

        // a politeness delay, to make sure we don't ping the servers too much
        config.setPolitenessDelay(5000);

        // how far we can crawl from the "seed" pages (think DFS depth)
        config.setMaxDepthOfCrawling(32767);

        // max number of pages we can fetch
        config.setMaxPagesToFetch(1000000);

        // binary content includes pictures, etc etc.
        config.setIncludeBinaryContentInCrawling(false);

        // resumable crawling is for starting/stopping the crawler.
        config.setResumableCrawling(false);

        // setting up all the objects we need to start our controller
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        // instantiate our controller
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // add our controller seed (start page)
        controller.addSeed("https://www.ebay.com/itm/Logitech-G640-Large-Cloth-Gaming-Mousepad/303427616970");

        // number of threads used during crawling
        int numberOfCrawlers = 8;

        // construct a new AtomicInteger, which we will use to keep track of the number of pages seem.
        AtomicInteger numPagesSeen = new AtomicInteger();

        // create our factory of web crawlers
        CrawlController.WebCrawlerFactory<EbayCrawler> factory = () -> new EbayCrawler(numPagesSeen);

        // start crawling
        controller.start(factory, numberOfCrawlers);

    }
}
