import edu.uci.ics.crawler4j.parser.HtmlParseData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonDataParser extends DataParser {

    private HtmlParseData data;

    static int numImages = 0;

    static final Pattern titlePattern = Pattern.compile("<span id=\"productTitle\".[^>]+>");
    static final Pattern dealPricePattern = Pattern.compile("<span id=\"priceblock_dealprice\"[^>]+>");
    static final Pattern regularPricePattern = Pattern.compile("<span id=\"priceblock_ourprice\"[^>]+>");
    static final Pattern mainImagePattern = Pattern.compile("<div id=\"imgTagWrapperId\"[^>]+>");
    static final Pattern imagePattern = Pattern.compile("src=\"");
    static final Pattern reviewPattern = Pattern.compile("data-hook=\"review\"[^>]+>");
    static final Pattern reviewNamePattern = Pattern.compile("<span class=\"a-profile-name\">");
    static final Pattern reviewTitlePattern = Pattern.compile("<a data-hook=\"review-title\"[^>]+>[^<]+<span>");
    static final Pattern reviewDatePattern = Pattern.compile("<span data-hook=\"review-date\"[^>]+>");
    static final Pattern reviewTextPattern = Pattern.compile("<div data-hook=\"review-collapsed\"[^>]+>[^<]+<span>");
    static final Pattern reviewRatingPattern = Pattern.compile("<i data-hook=\"review-star-rating\"[^>]+><span[^>]+>");
    static final Pattern altImagesHeaderPattern = Pattern.compile("<div id=\"altImages\"[^>]+>");
    static final Pattern altImageHeaderPattern = Pattern.compile("<li class=\"a-spacing-small item\">");



    String title;
    double price;
    List<Review> reviews;
    List<String> altImages; // local path to alternate images

    public AmazonDataParser(HtmlParseData data) {
        super(data);
        this.data = data;
        reviews = new ArrayList<Review>();
        altImages = new ArrayList<String>();
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

    public List<String> extractAlternateImages() {
        if (data.getHtml() == null) return null;
        Matcher matcher = altImagesHeaderPattern.matcher(data.getHtml()); // find the header for all alt images
        if (matcher.find()) {
            int idx = matcher.end();
            String altImagesHtml = data.getHtml().substring(idx);
            matcher = altImageHeaderPattern.matcher(altImagesHtml);
            while (matcher.find()) {
                idx = matcher.end();
                String altImageHtml = altImagesHtml.substring(matcher.end());
                altImages.add(extractImage(altImageHtml));
            }
        }
        return altImages;
    }

    public List<Review> extractReviews() {
        if (data.getHtml() == null) return null;
        Matcher matcher = reviewPattern.matcher(data.getHtml());
        while (matcher.find()) {
            Review review = new Review();
            int index = matcher.end();
            String temp = data.getHtml().substring(index);
            Matcher tempMatcher = reviewNamePattern.matcher(temp);
            if (tempMatcher.find()) { // should happen
                review.setName(getContent(reviewNamePattern, temp, '<'));
            }
            tempMatcher = reviewTitlePattern.matcher(temp);
            if (tempMatcher.find()) { // should happen
                review.setTitle(getContent(reviewTitlePattern, temp, '<'));
            }
            tempMatcher = reviewDatePattern.matcher(temp);
            if (tempMatcher.find()) { // should happen
                review.setDate(getContent(reviewDatePattern, temp, '<'));
            }
            tempMatcher = reviewRatingPattern.matcher(temp);
            if (tempMatcher.find()) { // should happen
                review.setRating(Double.parseDouble(getContent(reviewRatingPattern, temp, '<').substring(0, 3))); // should be something like 3.0 or 4.0
            }
            tempMatcher = reviewTextPattern.matcher(temp);
            if (tempMatcher.find()) { // should happen
                StringBuilder strBuilder = new StringBuilder();
                int idx = tempMatcher.end();
                while (idx < temp.length() && (strBuilder.toString().length() < 7 || !strBuilder.toString().substring(strBuilder.length() - 7, strBuilder.length()).equals("</span>"))) {
                    strBuilder.append(temp.charAt(idx));
                    idx++;
                }
                String text = strBuilder.toString();
                text.replaceAll("<br>", "\n");
                text = text.substring(0, text.length() - 7);
                review.setBody(text); // remove the </span> tag
            }
            reviews.add(review);
        }
        return reviews;
    }

    /**
     * Given an img element, this method extracts the URL of the image.
     * @param img The String containing the img element.
     * @return The URL of the image, or null if the URL does not exist.
     */
    private String extractImage(String img) {
        Matcher matcher = imagePattern.matcher(img);

        String imgLink = getContent(imagePattern, img, '\"');

        return imgLink;

        // Side note: weirdly enough, amazon no longer sends alternate images as base64??? So now I'm just printing out the links.

//        String path = null;
//        if (base64 == null) {
//            return null;
//        } else {
//            String[] strings = base64.split(",");
//            String extension;
//            switch (strings[0]) {//check image's extension
//                case "data:image/jpeg;base64":
//                    extension = "jpeg";
//                    break;
//                case "data:image/png;base64":
//                    extension = "png";
//                    break;
//                default://should write cases for more images types
//                    extension = "jpg";
//                    break;
//            }
//            //convert base64 string to binary data
//            byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
//            path = "C:\\Users\\hungw\\Desktop\\" + numImages + "." + extension;
//            File file = new File(path);
//            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
//                outputStream.write(data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        numImages++;
//        return path;
    }

    public String extractName() {
        if (title == null) {
            title = getContent(titlePattern, data.getHtml(), '<');
        }
        return title;
    }

    public String extractMainImage() {
        if (data.getHtml() == null) return null;
        Matcher matcher = mainImagePattern.matcher(data.getHtml());
        int index = -1;
        if (matcher.find()) {
            index = matcher.end();
        }
        try {
            return extractImage(data.getHtml().substring(index));
        } catch (StringIndexOutOfBoundsException | NullPointerException e) {
            // e.printStackTrace();
        }
        return null;
    }

    public double extractPrice() {
        if (price == 0.0) {
            String temp = getContent(dealPricePattern, data.getHtml(), '<');
            if (temp == null) {
                temp = getContent(regularPricePattern, data.getHtml(), '<');
            }
            try {
                price = Double.parseDouble(temp.substring(1));
            } catch (Exception e) {
                // no need to take action, price should stay at zero
                // e.printStackTrace();
            }
        }
        return price;
    }



}
