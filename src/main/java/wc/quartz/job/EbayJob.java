package wc.quartz.job;

import wc.ReviewProcessor;
import wc.csv.Entry;
import wc.csv.Review;
import wc.dataparser.AmazonDataParser;
import wc.dataparser.EbayDataParser;
import wc.jobrepo.AmazonJobRepo;
import org.quartz.*;
import wc.jobrepo.EbayJobRepo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class EbayJob implements Job {

    private static final String baseURL = "https://nlp.netbase.com/sentiment?languageTag=en&mode=index&syntax=twitter&text=";

    private static final EbayJobRepo repo = EbayJobRepo.getInstance();

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public static String getProductID(String url) {
        if (url == null) return null;
        StringBuffer productIDBuffer = new StringBuffer();
        int index = url.indexOf("/itm/") + 5;
        if (index == 3) return "";
        while (index < url.length() && url.charAt(index) != '/') {
            index++;
        }
        index++;
        while (index < url.length() && url.charAt(index) != '/' && url.charAt(index) != '?') {
            productIDBuffer.append(url.charAt(index));
            index++;
        }
        return productIDBuffer.toString();
    }

    static String getHtml(String link) {
        URL url = null;
        try {
            url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36 Edg/84.0.522.52");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            if ("gzip".equals(con.getContentEncoding())) { // gzip encoding on amazon html
//                in = new BufferedReader(new InputStreamReader(new GZIPInputStream(con.getInputStream())));
//            }
//            else {
//                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            }

            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        EbayJobRepo repo = EbayJobRepo.getInstance();

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        repo.start(dataMap.getInt("crawler number"));

        String url = dataMap.getString("url");

        String productID = getProductID(url);

        System.out.println("product id: " + productID);

        repo.visited(productID);

        String html = getHtml(url);

        EbayDataParser parser = new EbayDataParser(html);

        Entry pageEntry = new Entry();
        pageEntry.set("RECORD_ID", "page" + repo.size());
        pageEntry.set("RECORD_URL", url);
        pageEntry.set("RECORD_TITLE", parser.getName());
        pageEntry.set("META_TAGS2", "EbayReview");



        pageEntry.set("META_TAGS", productID);
        System.out.println("META TAG: " + productID);
        repo.addData(pageEntry);

        System.out.println("-------");
        System.out.println("title: " + parser.getName());
//        System.out.println("-------");
//        System.out.println("price: " + parser.getPrice());
//        System.out.println("-------");



//        System.out.println("alternate images: ");
//        for (String s : parser.getAlternateImages()) {
//            System.out.println(s);
//        }
//
//        System.out.println("reviews: ");

        int count = 0;
        for (Review r : parser.getReviews()) {
            String reviewText;
            r.setRecordID("page" + repo.size() + "-review" + count++);
            r.setProductID(productID);
            if (r.getReviewText().length() > 1000) {
                reviewText = r.getReviewText().substring(0, 1000); // truncate to 1000 characters
            } else {
                reviewText = r.getReviewText();
            }
            r.setReviewText(reviewText);
            String urlEncodedReview = encodeValue(reviewText);
            String requestURL = baseURL + urlEncodedReview;
            Entry reviewEntry = ReviewProcessor.getSentiment(r, requestURL);
            reviewEntry.set("META_TAGS2", "EbayReview");
//            System.out.println("---------");
//            System.out.println(reviewEntry);
//            System.out.println("---------");


            repo.addData(reviewEntry);
        }

        Set<String> tags = new HashSet<String>();
        for (String link : parser.getOutgoingLinks()) {
            if (link.startsWith("https://www.ebay.com") && link.contains("/itm/")) {
                // clean up the link
                int idx = link.indexOf("/itm/");
                idx += 5;
                while (idx < link.length() && link.charAt(idx) != '/') {
                    idx++;
                }
                idx++;
                while (idx < link.length() && link.charAt(idx) != '/' && link.charAt(idx) != '?') {
                    idx++;
                }
                String tag = getProductID(link);
                if (!repo.hasVisited(tag) && !tags.contains(tag)) {
                    repo.addLink(link.substring(0, idx));
                    tags.add(tag);
                    System.out.println(link.substring(0, idx));
                }
            }
        }

        repo.end(dataMap.getInt("crawler number"));

    }

}