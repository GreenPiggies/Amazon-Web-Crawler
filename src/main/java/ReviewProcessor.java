import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class ReviewProcessor {

    static String[] keywords = {"Action", "Activity", "Agent", "Aspect", "AsptQuality", "EventType", "ObjQuality", "Object", "Sentiment", "SubjQuality", "Use"};

    public static Entry getSentiment(Review review, String requestURL) {
        Entry entry = new Entry();
        if (review == null || requestURL == null) return entry;
        try {
            // first add review info to the entry
            entry.set("RECORD_ID", review.getRecordID());

            entry.set("RECORD_DATETIME", review.getReviewDate());
            entry.set("RECORD_TITLE", review.getReviewTitle());
            entry.set("RECORD_URL", review.getReviewURL());
            entry.set("RECORD_TEXT", review.getReviewText());
            entry.set("AUTHOR_NAME", review.getName());
            entry.set("META_TAGS", review.getProductID());


            // establish website connection
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // encode authorization and add it to the request header
//            String auth = "<redacted>";
//            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
//            String authHeaderValue = "Basic " + new String(encodedAuth);
//            con.setRequestProperty("Authorization", authHeaderValue);
//            int status = con.getResponseCode();
//
//            // read in the output
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuffer content = new StringBuffer();
//            while ((inputLine = in.readLine()) != null) {
//                content.append(inputLine);
//            }
//            in.close();
//            con.disconnect();
//            String info = content.toString();
//            System.out.println(info);
//
//            // Processing, convert to CSV format
//
//            int idx = info.indexOf("\"events\":[[[");
//            if (idx == -1) {
//                System.out.println("failure...");
//            }
//
//            boolean start = false;
//            StringBuffer buff = new StringBuffer();
//            StringBuffer secondBuff = new StringBuffer();
//            while (idx < info.length()) {
//
//                if (info.charAt(idx) == '{') {
//                    start = true;
//                } else if (info.charAt(idx) == '}' && start) { // end of event
//                    start = false;
//                    String event = buff.toString().substring(1);
//                    buff = new StringBuffer();
//                    System.out.println(event);
//                    // reuse buff
//                    for (int i = 0; i < keywords.length; i++) {
//                        StringBuffer thirdBuff = new StringBuffer();
//                        int j = event.indexOf(keywords[i]);
//                        if (j != -1) {
//                            System.out.println(j);
//                            j += 3 + keywords[i].length();
//                            while (event.charAt(j) != '\"') {
//                                thirdBuff.append(event.charAt(j));
//                                j++;
//                            }
//                            System.out.println("thirdbuff: " + thirdBuff.toString());
//                        }
//
//                        buff.append(thirdBuff.toString() + ",");
//                    }
//                    System.out.println("buff: " + buff.toString());
//                    secondBuff.append(buff.toString().substring(0, buff.toString().length() - 1) + '\n'); // remove dangling apostrophe
//                    buff = new StringBuffer();
//                }
//                if (start) {
//                    buff.append(info.charAt(idx));
//                }
//                idx++;
//            }
//            System.out.println("secondbuff: " + secondBuff.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entry;
    }

    public static void main(String[] args) throws Exception {
        URL url = new URL("https://nlp.netbase.com/sentiment?languageTag=en&mode=index&syntax=twitter&text=that's%20pretty%20epic");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        String auth = "<redacted>";
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedAuth);
        con.setRequestProperty("Authorization", authHeaderValue);

        int status = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        System.out.println(content);

        con.disconnect();






    }
}
