package in.instacard.instacard.models.callback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohakgoyal on 3/1/16.
 */
public class Bill {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("vendor_id")
    @Expose
    private int vendor_id;

    @SerializedName("merchant_id")
    @Expose
    private int merchant_id;

    @SerializedName("store_id")
    @Expose
    private int store_id;

    @SerializedName("user_id")
    @Expose
    private int user_id;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("amount")
    @Expose
    private int amount;

    @SerializedName("points")
    @Expose
    private int points;

    @SerializedName("surge")
    @Expose
    private int surge;

    @SerializedName("rating")
    @Expose
    private int rating;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("store_name")
    @Expose
    private String store_name;

    @SerializedName("store_logo")
    @Expose
    private String store_logo;

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_logo() {
        return store_logo;
    }

    public void setStore_logo(String store_logo) {
        this.store_logo = store_logo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public int getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getSurge() {
        return surge;
    }

    public void setSurge(int surge) {
        this.surge = surge;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
