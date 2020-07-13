import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.http.Header;

import java.io.*;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.json.simple.*;

/**
 * The AmazonCrawler class constructs an Amazon Web Crawler that keeps track of the number of pages it has crawled through.
 */
public class AmazonCrawler extends WebCrawler {

    // not being used atm, might remove it later
    private static final Pattern WEBSITE_EXTENSIONS = Pattern.compile(".*\\.(org|com|gov)$");

    private static final String baseURL = "https://nlp.netbase.com/sentiment?languageTag=en&mode=index&syntax=twitter&text=";

    // keeps count on the number of crawled (seen) pages
    private final AtomicInteger seenPages;

    /**
     * Constructs a MyCrawler object.
     * @param pages An AtomicInteger that keeps track of the number of pages visited by the crawler.
     */
    public AmazonCrawler(AtomicInteger pages) {
        seenPages = pages;
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    /**
     * Returns whether or not the specified URL should be visited, given the page it has been found in.
     * @param referringPage The page from which the specified URL was found.
     * @param url The specified URL.
     * @return True if the URL should be visited, false otherwise.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        // atm referringPage isn't used, might be used later
        if (url == null || url.getURL() == null || referringPage == null) return false;

        String href = url.getURL().toLowerCase();

        // pretty simple heuristic, visit the page if its an amazon link.
        return href.startsWith("https://www.amazon.com") && href.contains("/dp/"); // means its a product
    }

    /**
     * Visits a web page.
     * Precondition: This web page should be visited and it is not null.
     * @param page The page to be visited.
     */
    @Override
    public void visit(Page page) {

        /* A lot of this website data isn't used right now, I'm in the process of going through these and figuring out exactly what they are.
         * For now, I'm just printing them out on the logger.
         * */

        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        String domain = page.getWebURL().getDomain();
        String path = page.getWebURL().getPath();
        String subDomain = page.getWebURL().getSubDomain();
        String parentUrl = page.getWebURL().getParentUrl();
        String anchor = page.getWebURL().getAnchor();

        logger.debug("Docid: {}", docid);
        logger.info("URL: {}", url);
        logger.debug("Domain: '{}'", domain);
        logger.debug("Sub-domain: '{}'", subDomain);
        logger.debug("Path: '{}'", path);
        logger.debug("Parent page: {}", parentUrl);
        logger.debug("Anchor text: {}", anchor);

        // making sure the webpage is parseable
        if (page.getParseData() instanceof HtmlParseData) {

            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml().trim();

            DataParser parser = new AmazonDataParser(htmlParseData);

//            // this code gets the outgoing URLs, which we want to crawl
//            Set<WebURL> links = htmlParseData.getOutgoingUrls();
//            for (WebURL link : links) {
//                System.out.println(link.getURL());
//            }

            logger.debug("Text length: {}", text.length());
            logger.debug("Html length: {}", html.length());
//            logger.debug("Number of outgoing links: {}", links.size());

            html.replaceAll("[ \t\n\r]+","\n");

//            int num = 0;
//            try {
//                PrintWriter writer = new PrintWriter(new FileWriter(new File("amazon_test.txt")));
//                writer.println(html.length());
//                writer.println(html);
//                writer.flush();
//                writer.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            System.out.println("-------");
            System.out.println("title: " + parser.extractName());
            System.out.println("-------");
            System.out.println("price: " + parser.extractPrice());
            System.out.println("-------");
            System.out.println("reviews: ");

            for (Review r : parser.extractReviews()) {
                System.out.println(r);
                String urlEncodedReview = encodeValue(r.getReviewText());
                String requestURL = baseURL + urlEncodedReview;
                String data = ReviewProcessor.getSentiment(requestURL);
                System.out.println(data);
                try {
                    Thread.sleep(500);
                } catch (IllegalArgumentException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
            System.out.println("alternate images: ");
            for (String s : parser.extractAlternateImages()) {
                System.out.println(s);
            }




//            System.out.println("main image link: " + parser.getMainImage());

//            // JSON CODE
//            obj.put("Name", parser.getTitle());
//            obj.put("Price", "$" + parser.getPrice());
//
//            try {
//                File file = new File("D:\\tmp-json\\" + docid + ".json");
//                file.createNewFile();
//                FileWriter fileWriter = new FileWriter(file);
//                fileWriter.write(obj.toJSONString());
//                fileWriter.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }




        }

        // some metadata about the page
        Header[] responseHeaders = page.getFetchResponseHeaders();
        if (responseHeaders != null) {
            logger.debug("Response headers:");
            for (Header header : responseHeaders) {
                logger.debug("\t{}: {}", header.getName(), header.getValue());
            }
        }

        // printout for the number of pages visited so far.
        logger.debug("Pages visited: " + seenPages.incrementAndGet());

        logger.debug("=============");
    }

}
