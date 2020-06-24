import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The MyController class is the "runner" class for my web crawler.
 */
public class MyController {
    public static void main(String[] args) throws Exception {


        //TEST

        File dir = new File("/tmp/");
        dir.mkdirs();

        File file = new File(".");
        for(String fileNames : file.list()) System.out.println(fileNames);

        // END TEST


        CrawlConfig config = new CrawlConfig();

        config.setUserAgentString("intern bot");

        // this folder stores all the data that the crawler needs to store
        config.setCrawlStorageFolder("/tmp/amazon-web-crawler/");

        // a politeness delay, to make sure we don't ping the servers too much
        config.setPolitenessDelay(5000);

        // how far we can crawl from the "seed" pages (think DFS depth)
        config.setMaxDepthOfCrawling(2);

        // max number of pages we can fetch
        config.setMaxPagesToFetch(1000);

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
        controller.addSeed("https://www.amazon.com/Angry-Birds-Stella-Plush-Toy/dp/B00T3TXBFA/");

        // number of threads used during crawling
        int numberOfCrawlers = 8;

        // construct a new AtomicInteger, which we will use to keep track of the number of pages seem.
        AtomicInteger numPagesSeen = new AtomicInteger();

        // create our factory of web crawlers
        CrawlController.WebCrawlerFactory<MyCrawler> factory = () -> new MyCrawler(numPagesSeen);

        // start crawling
        controller.start(factory, numberOfCrawlers);

    }
}
