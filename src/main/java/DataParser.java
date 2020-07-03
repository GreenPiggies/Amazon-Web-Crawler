import edu.uci.ics.crawler4j.parser.HtmlParseData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public abstract class DataParser {
    private HtmlParseData data; // so far this is just being used to get the html string, might have future use?
    private String html;



    private String name;
    private String image;
    private double price;
    private List<Review> reviews;
    private List<String> alternateImages;

    public DataParser(HtmlParseData data) {
        this.data = data;
        html = data.getHtml();
        name = image = "";
        price = 0.0;
        reviews = null;
        alternateImages = null;
    }

    abstract String extractName();
    abstract String extractImage();
    abstract double extractPrice();
    abstract List<Review> extractReviews();
    abstract List<String> extractAlternateImages();


    /**
     * This method extracts the content (a string) from the first matched pattern index until the specified character.
     * (Good for extracting stuff from inside <b>span tags</b> and such).
     * Precondition: Pattern p is present in String s.
     * @param p A regex pattern.
     * @param s The string to search.
     * @param c End character.
     * @return The aforementioned string, starting from the first instance of the specified pattern to the first occurrence of the specified character.
     */
    public String getContent(Pattern p, String s, char end) {
        Matcher matcher = p.matcher(s);
        if (matcher.find()) { // only need to find one
            int index = matcher.end();
            StringBuilder strBuilder = new StringBuilder();
            while (index < s.length()) {
                char c = s.charAt(index);
                if (c == end) {
                    break;
                } else {
                    strBuilder.append(c);
                    index++;
                }
            }
            return strBuilder.toString().trim();
        }
        return null;
    }

    public String getName() {
        if (name.equals("")) name = extractName();
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        if (image.equals("")) image = extractImage();
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        if (price == 0.0) price = extractPrice();
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Review> getReviews() {
        if (reviews == null) reviews = extractReviews();
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<String> getAlternateImages() {
        if (alternateImages == null) alternateImages = extractAlternateImages();
        return alternateImages;
    }

    public void setAlternateImages(List<String> alternateImages) {
        this.alternateImages = alternateImages;
    }

    public String getHtml() {
        return html;
    }

}
