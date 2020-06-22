import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonDataParser implements DataParser {

    private HtmlParseData data;

    String html;

    public AmazonDataParser(HtmlParseData data) {
        this.data = data;
        html = data.getHtml();
    }

    public String print() {
        return html;
    }

    public String getTitle() {
        Pattern pattern = Pattern.compile("<span id=\"productTitle\".[^>]+>");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            int endIndex = matcher.end();
            System.out.println("end index: " + endIndex);
            StringBuilder strBuilder = new StringBuilder();
            while (endIndex < html.length()) {
                char c = html.charAt(endIndex);
                if (c == '<') {
                    break;
                } else {
                    strBuilder.append(c);
                    endIndex++;
                }
            }
            return strBuilder.toString().trim();
        }

        return "";
    }

    public WebURL getImage() {
        return new WebURL();
    }

    public double getPrice() {

        return 0.0;
    }
}
