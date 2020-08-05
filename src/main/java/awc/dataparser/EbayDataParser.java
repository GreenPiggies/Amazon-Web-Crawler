package awc.dataparser;

import awc.csv.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EbayDataParser extends DataParser {
    private Pattern namePattern = Pattern.compile("id=\"itemTitle\"><span[^>]+>[^<]+</span>");
    private Pattern bidPricePattern = Pattern.compile("id=\"prcIsum_bidPrice\" itemprop=\"price\" content=\"");
    private Pattern buyPricePattern = Pattern.compile("id=\"prcIsum\" itemprop=\"price\" style=\"\" content=\"");
    private Pattern mainImagePattern = Pattern.compile("itemprop=\"image\" src=\"");
    private Pattern reviewHeaderPattern = Pattern.compile("<div class=\" ebay-review-section\"[^>]+>");
    private Pattern reviewRatingPattern = Pattern.compile("<div role=\"img\" class=\"ebay-star-rating\" aria-label=\"");
    private Pattern reviewNamePattern = Pattern.compile("class=\"review-item-author\" itemprop=\"author\" title=\"");
    private Pattern reviewDatePattern = Pattern.compile("<span itemprop=\"datePublished\" content=\"");
    private Pattern reviewTitlePattern = Pattern.compile("<p itemprop=\"name\" class=\"review-item-title[^>]+>");
    private Pattern reviewBodyPattern = Pattern.compile("<p itemprop=\"reviewBody\" class=\"review-item-content[^>]+>");
    private Pattern altImagePattern = Pattern.compile("<a id=\"vi_main_img_fs[^>]+>");
    private Pattern imagePattern = Pattern.compile("src=\"");

    double bidPrice;

    public EbayDataParser(String html) {
        super(html);
        bidPrice = 0.0;
    }

    public String extractName() {
        String name = getContent(namePattern, getHtml(), '<');
        return (name == null) ? name : name.trim();
    }

    public double extractPrice() {
        String price = getContent(buyPricePattern, getHtml(), '\"');
        return (price == null ? 0.0 : Double.parseDouble(price.trim()));
    }

    public double extractBidPrice() {
        return Double.parseDouble(getContent(bidPricePattern, getHtml(), '\"').trim());
    }

    public double getBidPrice() {
        if (bidPrice == 0.0) bidPrice = extractBidPrice();
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String extractMainImage() {
        return getContent(mainImagePattern, getHtml(), '\"');
    }

    public List<Review> extractReviews() {
        if (getHtml() == null) return null;
        Matcher reviewMatcher = reviewHeaderPattern.matcher(getHtml());
        List<Review> reviews = new ArrayList<Review>();
        while (reviewMatcher.find()) {
            System.out.println("match found");
            Review review = new Review();
            String temp = getHtml().substring(reviewMatcher.end());
            // review rating
            review.setRating(Double.parseDouble(getContent(reviewRatingPattern, temp, ' ')));
            review.setName(getContent(reviewNamePattern, temp, '\"'));
            review.setDate(getContent(reviewDatePattern, temp, '\"'));
            review.setTitle(getContent(reviewTitlePattern, temp, '<'));
            review.setReviewText(getContent(reviewBodyPattern, temp, '<'));
            reviews.add(review);
        }
        return reviews;
    }

    public List<String> extractAlternateImages() {
        if (getHtml() == null) return null;
        Matcher altImageMatcher = altImagePattern.matcher(getHtml());
        List<String> altImages = new ArrayList<String>();
        while (altImageMatcher.find()) {
            String temp = getHtml().substring(altImageMatcher.end());
            altImages.add(getContent(imagePattern, temp, '\"').trim());
        }
        return altImages;
    }

    public List<String> extractOutgoingLinks() {
        return null;
    }



}
