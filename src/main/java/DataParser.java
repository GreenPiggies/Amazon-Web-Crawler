import edu.uci.ics.crawler4j.url.WebURL;

public interface DataParser {
    String getTitle();
    WebURL getImage();
    double getPrice();
}
