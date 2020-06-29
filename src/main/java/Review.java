public class Review {

    private String name;
    private String reviewText;
    private String reviewTitle;
    private double rating;



    private String reviewDate;

    public Review() {
        name = "";
        reviewText = "";
        reviewTitle = "";
        rating = 0.0;
        reviewDate = "";
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
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

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String toString() {
        return "----------\n"
        + "name: " + name + "\n"
        + "rating: " + rating + "\n"
        + "review date: " + reviewDate + "\n"
        + "review title: " + reviewTitle + "\n"
        + "review text: " + reviewText + "\n";
    }



}
