import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.json.simple.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonDataParser implements DataParser {

    private HtmlParseData data;

    private Pattern titlePattern = Pattern.compile("<span id=\"productTitle\".[^>]+>");
    private Pattern dealPricePattern = Pattern.compile("<span id=\"priceblock_dealprice\"[^>]+>");
    private Pattern regularPricePattern = Pattern.compile("<span id=\"priceblock_ourprice\"[^>]+>");

    private String html;

    public AmazonDataParser(HtmlParseData data) {
        this.data = data;
        html = data.getHtml();
    }

    public String print() {
        return html;
    }

    public String getTitle() {
        Matcher matcher = titlePattern.matcher(html);

        while (matcher.find()) {
            int index = matcher.end();
            StringBuilder strBuilder = new StringBuilder();
            while (index < html.length()) {
                char c = html.charAt(index);
                if (c == '<') {
                    break;
                } else {
                    strBuilder.append(c);
                    index++;
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
        // check for a deal price first
        Matcher matcher = dealPricePattern.matcher(html);

        double price;

        boolean dealFound = false;

        StringBuilder strBuilder = new StringBuilder();

        if (matcher.find()) {
            dealFound = true;
            int index = matcher.end();
            while (index < html.length()) {
                char c = html.charAt(index);
                if (c == '<') {
                    break;
                } else {
                    strBuilder.append(c);
                    index++;
                }
            }
        }

        if (!dealFound) {
            matcher = regularPricePattern.matcher(html);
            if (matcher.find()) {
                int index = matcher.end();
                while (index < html.length()) {
                    char c = html.charAt(index);
                    if (c == '<') {
                        break;
                    } else {
                        strBuilder.append(c);
                        index++;
                    }
                }
            }
        }

        return Double.parseDouble(strBuilder.toString().trim().substring(1));
    }
}
