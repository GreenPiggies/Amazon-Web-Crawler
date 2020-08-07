package wc.crawler;

import wc.*;
import wc.csv.Entry;
import wc.csv.Review;
import wc.dataparser.DataParser;
import wc.dataparser.EbayDataParser;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The awc.crawler.AmazonCrawler class constructs an Amazon Web Crawler that keeps track of the number of pages it has crawled through.
 */
@Deprecated
public class EbayCrawler extends WebCrawler {

    private static Pattern productIDPattern = Pattern.compile("itm/[^/]+/");

    // not being used atm, might remove it later
    private static final Pattern WEBSITE_EXTENSIONS = Pattern.compile(".*\\.(org|com|gov)$");

    // keeps count on the number of crawled (seen) pages
    private final AtomicInteger seenPages;

    private static final String baseURL = "https://nlp.netbase.com/sentiment?languageTag=en&mode=index&syntax=twitter&text=";

    private PrintWriter writer;


    /**
     * Constructs a MyCrawler object.
     * @param pages An AtomicInteger that keeps track of the number of pages visited by the crawler.
     */
    public EbayCrawler(AtomicInteger pages, PrintWriter writer) {
        seenPages = pages;
        this.writer = writer;
    }

    /**
     * Returns whether or not the specified URL should be visited, given the page it has been found in.
     * @param referringPage The page from which the specified URL was found.
     * @param url The specified URL.
     * @return True if the URL should be visited, false otherwise.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        if (referringPage == null || url == null || url.getURL() == null) return false;
        // atm referringPage isn't used, might be used later
        String href = url.getURL().toLowerCase();

        // pretty simple heuristic, visit the page if its an amazon link.
        return href.startsWith("https://www.ebay.com") && href.contains("/itm/"); // means its a product
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    /**
     * Visits a web page.
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

            logger.debug("Pages visited: " + seenPages.incrementAndGet());


            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml().trim();

            DataParser parser = new EbayDataParser(html);

            // this code gets the outgoing URLs, which we want to crawl
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
            for (WebURL link : links) {
                System.out.println(link.getURL());
            }

            logger.debug("Text length: {}", text.length());
            logger.debug("Html length: {}", html.length());
//            logger.debug("Number of outgoing links: {}", links.size());

            html.replaceAll("[ \t\n\r]+","\n");

            Entry pageEntry = new Entry();
            pageEntry.set("RECORD_ID", "page" + seenPages);
            pageEntry.set("RECORD_URL", url);
            pageEntry.set("RECORD_TITLE", parser.getName());
            pageEntry.set("META_TAGS2", "EbayReview");

            // product ID
            Matcher temp = productIDPattern.matcher(url);
            String productID = "";
            int index = -1;
            if (temp.find()) {
                index = temp.end();
            }
            StringBuffer buff = new StringBuffer();
            while (index < url.length() && url.charAt(index) != '/' && url.charAt(index) != '?') {
                buff.append(url.charAt(index));
                index++;
            }
            productID = buff.toString();
            pageEntry.set("META_TAGS", productID);

            writer.println(pageEntry);
            writer.flush();

            System.out.println("-------");
            System.out.println("title: " + parser.extractName());
            System.out.println("-------");
            System.out.println("price: " + parser.extractPrice());
            System.out.println("-------");
            System.out.println("main image: " + parser.getMainImage());
            System.out.println("reviews: ");

            int count = 0;
            for (Review r : parser.getReviews()) {
                String reviewText;
                r.setRecordID("page" + seenPages + "-review" + count++);
                r.setProductID(productID);
                if (r.getReviewText().length() > 1000) {
                    reviewText = r.getReviewText().substring(0, 1000); // truncate to 1000 characters
                } else {
                    reviewText = r.getReviewText();
                }
                r.setReviewText(reviewText);
                String urlEncodedReview = encodeValue(reviewText);
                String requestURL = baseURL + urlEncodedReview;

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Entry reviewEntry = ReviewProcessor.getSentiment(r, requestURL);
                reviewEntry.set("META_TAGS2", "EbayReview");
                System.out.println("---------");
                System.out.println(reviewEntry);
                System.out.println("---------");

                writer.println(reviewEntry);
                writer.flush();

                System.out.println(r);
            }
            System.out.println("alternate images: ");
            for (String s : parser.getAlternateImages()) {
                System.out.println(s);
            }



        }

        // some metadata about the page
        Header[] responseHeaders = page.getFetchResponseHeaders();
        if (responseHeaders != null) {
            logger.debug("Response headers:");
            for (Header header : responseHeaders) {
                logger.debug("\t{}: {}", header.getName(), header.getValue());
            }
        }

        logger.debug("=============");
    }

}
