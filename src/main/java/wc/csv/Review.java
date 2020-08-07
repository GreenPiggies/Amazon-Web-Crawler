package wc.csv;

public class Review {

    private String name;
    private String reviewText;
    private String reviewTitle;
    private double rating;
    private String productID;
    private String recordID;
    private String reviewURL;
    private String reviewDate;

    public Review() {
        name = "";
        reviewText = "";
        reviewTitle = "";
        rating = 0.0;
        reviewDate = "";
        productID = "";
        recordID = "";
        reviewURL = "";
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getProductID() { return productID; }

    public void setProductID(String id) { this.productID = id; }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String id) {
        this.recordID = id;
    }

    public String getReviewURL() { return reviewURL; }

    public void setReviewURL(String reviewURL) {
        this.reviewURL = reviewURL;
    }

    public String toString() {
        return "----------\n"
        + "id: " + productID + "\n"
        + "name: " + name + "\n"
        + "rating: " + rating + "\n"
        + "review date: " + reviewDate + "\n"
        + "review title: " + reviewTitle + "\n"
        + "review text: " + reviewText + "\n"
        + "review URL: " + reviewURL + "\n";
    }



}
