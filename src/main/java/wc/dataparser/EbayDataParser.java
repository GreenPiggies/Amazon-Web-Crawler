package wc.dataparser;

import wc.csv.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EbayDataParser extends DataParser {
    static final Pattern namePattern = Pattern.compile("id=\"itemTitle\"><span[^>]+>[^<]+</span>");
    static final Pattern bidPricePattern = Pattern.compile("id=\"prcIsum_bidPrice\" itemprop=\"price\" content=\"");
    static final Pattern buyPricePattern = Pattern.compile("id=\"prcIsum\" itemprop=\"price\" style=\"\" content=\"");
    static final Pattern mainImagePattern = Pattern.compile("itemprop=\"image\" src=\"");
    static final Pattern reviewHeaderPattern = Pattern.compile("<div class=\" ebay-review-section\"[^>]+>");
    static final Pattern reviewRatingPattern = Pattern.compile("<div role=\"img\" class=\"ebay-star-rating\" aria-label=\"");
    static final Pattern reviewNamePattern = Pattern.compile("class=\"review-item-author\" itemprop=\"author\" title=\"");
    static final Pattern reviewDatePattern = Pattern.compile("<span itemprop=\"datePublished\" content=\"");
    static final Pattern reviewTitlePattern = Pattern.compile("<p itemprop=\"name\" class=\"review-item-title[^>]+>");
    static final Pattern reviewBodyPattern = Pattern.compile("<p itemprop=\"reviewBody\" class=\"review-item-content[^>]+>");
    static final Pattern altImagePattern = Pattern.compile("<a id=\"vi_main_img_fs[^>]+>");
    static final Pattern imagePattern = Pattern.compile("src=\"");
    static final Pattern outgoingLinksPattern = Pattern.compile("href=\"|src=\"");
    static final String root = "https://www.ebay.com";


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
        if (getHtml() == null) return new ArrayList<String>();

        Matcher matcher = outgoingLinksPattern.matcher(getHtml());
        List<String> outgoingLinks = new ArrayList<String>();
        while (matcher.find()) {
            StringBuffer buff = new StringBuffer();
            int idx = matcher.end();
            while (idx < getHtml().length() && getHtml().charAt(idx) != '\"') {
                buff.append(getHtml().charAt(idx));
                idx++;
            }
            String link = buff.toString();
            if (link.startsWith("/")) link = root + link;
            outgoingLinks.add(link);
        }
        setOutgoingLinks(outgoingLinks);
        return outgoingLinks;
    }



}
