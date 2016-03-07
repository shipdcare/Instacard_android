package in.instacard.instacard.models.callback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reward {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("points")
    @Expose
    private int points;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("merchant_id")
    @Expose
    private int merchant_id;

    public int getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
