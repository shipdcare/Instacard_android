package in.instacard.instacard.models.callback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmObject;


public class User extends RealmObject{

    @SerializedName("id")
    @Expose
    private int  id;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("fb_auth")
    @Expose
    private String fbAuth;
    @SerializedName("promo_code")
    @Expose
    private String promoCode;
    @SerializedName("auth_token")
    @Expose
    private String authToken;
    @SerializedName("confirmed_user")
    @Expose
    private String confirmedUser;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("reset_password_token")
    @Expose
    private String resetPasswordToken;
    @SerializedName("point_count")
    @Expose
    private int  pointCount;
    @SerializedName("sign_in_count")
    @Expose
    private int signInCount;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("provider")
    @Expose
    private String provider;
    @SerializedName("api_token")
    @Expose
    private String apiToken;
    @SerializedName("qr")
    @Expose
    private String qr;
    @SerializedName("visit_count")
    @Expose
    private int  visitCount;
    @SerializedName("review_count")
    @Expose
    private int  reviewCount;
    @SerializedName("redeem_count")
    @Expose
    private int redeemCount;
    @SerializedName("birthday")
    @Expose
    private Date birthday;


    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFbAuth() {
        return fbAuth;
    }

    public void setFbAuth(String fbAuth) {
        this.fbAuth = fbAuth;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getConfirmedUser() {
        return confirmedUser;
    }

    public void setConfirmedUser(String confirmedUser) {
        this.confirmedUser = confirmedUser;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public int getSignInCount() {
        return signInCount;
    }

    public void setSignInCount(int signInCount) {
        this.signInCount = signInCount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getRedeemCount() {
        return redeemCount;
    }

    public void setRedeemCount(int redeemCount) {
        this.redeemCount = redeemCount;
    }

    public User(){
    }
}
