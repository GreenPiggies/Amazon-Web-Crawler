import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonJob implements Job {

    private static final String baseURL = "https://nlp.netbase.com/sentiment?languageTag=en&mode=index&syntax=twitter&text=";

    public void execute(JobExecutionContext context) throws JobExecutionException {

        String url = AmazonScheduler.list.removeFirst();

        AmazonScheduler.visited.add(url);

        // read in the files (from amazon, myself?)

        String html = "";


        URL urlRequest = null;
        try {
            urlRequest = new URL(url);
            HttpURLConnection con = (HttpURLConnection) urlRequest.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            // read in the output
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            html = content.toString();
            System.out.println(html);
        } catch (Exception e) {
            e.printStackTrace();
        }



        DataParser parser = new AmazonDataParser(html);
        html.replaceAll("[ \t\n\r]+","\n");

        Pattern p = Pattern.compile("https://www.amazon.com/");

        Matcher match = p.matcher(html);


        while (match.find()) {
            StringBuffer buff = new StringBuffer();
            buff.append("https://www.amazon.com/");
            int idx = match.end();
            while (idx < html.length() && html.charAt(idx) != ')' && html.charAt(idx) != '\'' && html.charAt(idx) != '\"' && html.charAt(idx) != '?') {
                buff.append(html.charAt(idx));
                idx++;
            }
            String outgoingURL = buff.toString();
            if (outgoingURL.contains("/dp/") && !AmazonScheduler.list.contains(outgoingURL)) {
                AmazonScheduler.list.add(outgoingURL);
            }
        }


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
        pageEntry.set("RECORD_ID", "page");
        pageEntry.set("RECORD_URL", url);
        pageEntry.set("RECORD_TITLE", parser.getName());
        pageEntry.set("META_TAGS2", "AmazonReview");

        // get product id
        StringBuffer productIDBuffer = new StringBuffer();
        int index = url.indexOf("/dp/") + 4;
        System.out.println(index);
        while (index < url.length() && url.charAt(index) != '/' && url.charAt(index) != '?') {
            productIDBuffer.append(url.charAt(index));
            index++;
        }

        pageEntry.set("META_TAGS", productIDBuffer.toString());
        System.out.println("META TAG: " + productIDBuffer.toString());
        AmazonScheduler.writer.println(pageEntry.toString());

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
//            r.setRecordID("page" + seenPages + "-review" + count++);
            r.setProductID(productIDBuffer.toString());
            if (r.getReviewText().length() > 1000) {
                reviewText = r.getReviewText().substring(0, 1000); // truncate to 1000 characters
            } else {
                reviewText = r.getReviewText();
            }
            r.setReviewText(reviewText);
            String urlEncodedReview = ""; // encodeValue(reviewText);
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

            AmazonScheduler.writer.println(reviewEntry);
            AmazonScheduler.writer.flush();
        }


    }
}


