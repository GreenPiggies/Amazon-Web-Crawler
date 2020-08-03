import org.quartz.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class AmazonJob implements Job {

    private static final String baseURL = "https://nlp.netbase.com/sentiment?languageTag=en&mode=index&syntax=twitter&text=";


    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    static String getProductID(String url) {
        StringBuffer productIDBuffer = new StringBuffer();
        int index = url.indexOf("/dp/") + 4;
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

            BufferedReader in;
            if ("gzip".equals(con.getContentEncoding())) { // gzip encoding on amazon html
                in = new BufferedReader(new InputStreamReader(new GZIPInputStream(con.getInputStream())));
            }
            else {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }

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
        String url = MessageQueue.getInstance().queue.remove();

        String productID = getProductID(url);

        MessageQueue.getInstance().seen.add(productID);


        String html = getHtml(url);

        AmazonDataParser parser = new AmazonDataParser(html);

        Entry pageEntry = new Entry();
        pageEntry.set("RECORD_ID", "page" + MessageQueue.getInstance().seen.size());
        pageEntry.set("RECORD_URL", url);
        pageEntry.set("RECORD_TITLE", parser.getName());
        pageEntry.set("META_TAGS2", "AmazonReview");



        pageEntry.set("META_TAGS", productID);
        System.out.println("META TAG: " + productID);
        AmazonScheduler.writer.println(pageEntry);
        AmazonScheduler.writer.flush();

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
            r.setRecordID("page" + MessageQueue.getInstance().seen.size() + "-review" + count++);
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
            reviewEntry.set("META_TAGS2", "AmazonReview");
//            System.out.println("---------");
//            System.out.println(reviewEntry);
//            System.out.println("---------");


            AmazonScheduler.writer.println(reviewEntry);
            AmazonScheduler.writer.flush();
        }

        Set<String> tags = new HashSet<String>();
        for (String link : parser.getOutgoingLinks()) {
            if (link.startsWith("https://www.amazon.com") && link.contains("/dp/")) {
                // clean up the link
                int idx = link.indexOf("/dp/");
                idx += 4;
                while (idx < link.length() && link.charAt(idx) != '/' && link.charAt(idx) != '?') {
                    idx++;
                }
                String tag = getProductID(link.substring(0, idx));
                if (!MessageQueue.getInstance().seen.contains(tag) && !tags.contains(tag)) {
                    MessageQueue.getInstance().queue.add(link.substring(0, idx));
                    tags.add(tag);
                    System.out.println(link.substring(0, idx));
                }
            }
        }

    }

}