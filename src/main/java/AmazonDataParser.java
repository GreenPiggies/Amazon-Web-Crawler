import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import javax.xml.bind.DatatypeConverter;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonDataParser implements DataParser {

    private HtmlParseData data;

    static int numImages = 0;

    static final Pattern titlePattern = Pattern.compile("<span id=\"productTitle\".[^>]+>");
    static final Pattern dealPricePattern = Pattern.compile("<span id=\"priceblock_dealprice\"[^>]+>");
    static final Pattern regularPricePattern = Pattern.compile("<span id=\"priceblock_ourprice\"[^>]+>");
    static final Pattern mainImagePattern = Pattern.compile("<div id=\"imgTagWrapperId\"[^>]+>");
    static final Pattern imagePattern = Pattern.compile("src=\"");


    private String html;

    String title;
    double price;
    WebURL image;
    List<WebURL> altImages;

    public AmazonDataParser(HtmlParseData data) {
        this.data = data;
        html = data.getHtml();
    }

    public String print() {
        return html;
    }

    private String getStart(Pattern p) {
        String pattern = p.toString();
        StringBuilder strBuilder = new StringBuilder();
        int idx = 0;
        while (idx < pattern.length() && pattern.charAt(idx) != ' ') {
            strBuilder.append(pattern.charAt(idx));
            idx++;
        }
        return strBuilder.toString();
    }

    /**
     * Helper method to extract the html within a given element.
     * @param p The pattern of the element.
     * @return A string containing the contents of that element, or null if the specified pattern doesn't exist.
     */
    private String getContent(Pattern p) {
        Matcher matcher = p.matcher(html);

        while (matcher.find()) {
            System.out.println("found");
            int index = matcher.end();
            StringBuilder strBuilder = new StringBuilder();
            while (index < html.length()) {
                char c = html.charAt(index);
                System.out.print(c);
                if (c == '<') {
                    break;
                } else {
                    strBuilder.append(c);
                    index++;
                }
            }
            System.out.println();
            System.out.println(strBuilder.toString().trim().length());
            return strBuilder.toString().trim();
        }
        return null;
    }

    /**
     * Given an img element, this method extracts the URL of the image.
     * @param img The String containing the img element.
     * @return The URL of the image, or null if the URL does not exist.
     */
    private String getImage(String img) {
        Matcher matcher = imagePattern.matcher(img);

        String base64 = null;

        while (matcher.find()) {
            int index = matcher.end();
            StringBuilder strBuilder = new StringBuilder();
            while (index < img.length()) {
                char c = img.charAt(index);
                if (c == '\"') {
                    break;
                } else {
                    strBuilder.append(c);
                    index++;
                }
            }
            base64 = (strBuilder.toString().trim());
            break;
        }
        String path = null;
        if (base64 == null) {
            return null;
        } else {
            String[] strings = base64.split(",");
            String extension;
            switch (strings[0]) {//check image's extension
                case "data:image/jpeg;base64":
                    extension = "jpeg";
                    break;
                case "data:image/png;base64":
                    extension = "png";
                    break;
                default://should write cases for more images types
                    extension = "jpg";
                    break;
            }
            //convert base64 string to binary data
            byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
            path = "C:\\Users\\hungw\\Desktop\\" + numImages + "." + extension;
            File file = new File(path);
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                outputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        numImages++;
        return path;
    }

    public String getTitle() {
        if (title == null) {
            title = getContent(titlePattern);
        }
        return title;
    }

    public String getMainImage() {
        Matcher matcher = mainImagePattern.matcher(html);
        int index = -1;
        while (matcher.find()) {
            index = matcher.end();
            break;
        }
        System.out.println(index);
        System.out.println(html.substring(index).substring(0, 200));
        return getImage(html.substring(index));
    }

    public double getPrice() {
        String temp = getContent(dealPricePattern);
        if (temp == null) {
            temp = getContent(regularPricePattern);
        }

        return Double.parseDouble(temp.substring(1));
    }
}
