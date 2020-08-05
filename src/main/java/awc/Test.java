package awc;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class Test {

    public static void main(String[] args) throws Exception {

        PrintWriter writer = new PrintWriter(new FileWriter(new File("amazon_test_new.txt")));

        URL url = new URL("https://www.amazon.com/dp/B07FZ8S74R/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36 Edg/84.0.522.52");


        int status = con.getResponseCode();

        BufferedReader in;
        if ("gzip".equals(con.getContentEncoding())) {
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

        System.out.println(con.getContentEncoding());

        con.disconnect();

        writer.print(content.toString());


        System.out.println(content.toString());



    }

}

