import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.http.Header;

import java.io.*;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * The AmazonCrawler class constructs an Amazon Web Crawler that keeps track of the number of pages it has crawled through.
 */
public class AmazonCrawler extends WebCrawler {

    // not being used atm, might remove it later
    private static final Pattern WEBSITE_EXTENSIONS = Pattern.compile(".*\\.(org|com|gov)$");

    private static final String baseURL = "https://nlp.netbase.com/sentiment?languageTag=en&mode=index&syntax=twitter&text=";

    // keeps count on the number of crawled (seen) pages
    private final AtomicInteger seenPages;

    private PrintWriter writer;


    /**
     * Constructs a MyCrawler object.
     * @param pages An AtomicInteger that keeps track of the number of pages visited by the crawler.
     */
    public AmazonCrawler(AtomicInteger pages, PrintWriter writer) {
        seenPages = pages;
        this.writer = writer;
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
            String html = htmlParseData.getHtml().trim();
            DataParser parser = new AmazonDataParser(htmlParseData);
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

            Entry pageEntry = new Entry();
            pageEntry.set("RECORD_ID", "page " + seenPages);

            // getting the date
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
//            System.out.println(dtf.format(now));

            pageEntry.set("RECORD_DATETIME", dtf.format(now));
            pageEntry.set("RECORD_URL", url);
            pageEntry.set("RECORD_TITLE", parser.getName());
            pageEntry.set("META_TAGS2", "AmazonReview");

            // get product id
            StringBuffer productIDBuffer = new StringBuffer();
            int index = url.indexOf("/dp/") + 4;
            while (index < url.length() && url.charAt(index) != '/' && url.charAt(index) != '?') {
                productIDBuffer.append(url.charAt(index));
                index++;
            }

            pageEntry.set("META_TAGS", productIDBuffer.toString());
            System.out.println("META TAG: " + productIDBuffer.toString());
            writer.println(pageEntry.toString());

            System.out.println("-------");
            System.out.println("title: " + parser.getName());
            System.out.println("-------");
            System.out.println("price: " + parser.getPrice());
            System.out.println("-------");



            System.out.println("alternate images: ");
            for (String s : parser.getAlternateImages()) {
                System.out.println(s);
            }

            System.out.println("reviews: ");

            int count = 0;
            for (Review r : parser.getReviews()) {
                String reviewText;
                r.setRecordID("page" + seenPages + "-review" + count++);
                r.setProductID(productIDBuffer.toString());
                if (r.getReviewText().length() > 1000) {
                    reviewText = r.getReviewText().substring(0, 1000); // truncate to 1000 characters
                } else {
                    reviewText = r.getReviewText();
                }
                System.out.println(reviewText);
                String urlEncodedReview = encodeValue(reviewText);
                String requestURL = baseURL + urlEncodedReview;
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Entry reviewEntry = ReviewProcessor.getSentiment(r, requestURL);
                reviewEntry.set("META_TAGS2", "AmazonReview");
                System.out.println("---------");
                System.out.println(reviewEntry);
                System.out.println("---------");

                writer.println(reviewEntry);
                writer.flush();
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
