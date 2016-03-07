package in.instacard.instacard.models.callback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohakgoyal on 2/25/16.
 */
public class Visit {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("user_id")
    @Expose
    private int user_id;

    @SerializedName("merchant_id")
    @Expose
    private int merchant_id;

    @SerializedName("visit_count")
    @Expose
    private int visit_count;

    @SerializedName("current_points")
    @Expose
    private int current_points;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }

    public int getVisit_count() {
        return visit_count;
    }

    public void setVisit_count(int visit_count) {
        this.visit_count = visit_count;
    }

    public int getCurrent_points() {
        return current_points;
    }

    public void setCurrent_points(int current_points) {
        this.current_points = current_points;
    }
}
