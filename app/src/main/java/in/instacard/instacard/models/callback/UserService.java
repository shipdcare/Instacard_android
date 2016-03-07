package in.instacard.instacard.models.callback;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @Headers("Content-Type: application/json")
    @GET("/api/current/")
    Call<User> getUser(@Header("Authorization") String authorization);

    @Headers("Content-Type: application/json")
    @POST("api/users")
    Call<User> createUser(@Body User user);

    @Headers("Content-Type: application/json")
    @POST("api/sessions/")
    Call<User> signInUser(@Body User user);

    @Headers("Content-Type: application/json")
    @GET("/api/stores/{page}")
    Call<List<Store>> getStores(@Header("Authorization") String authorization,
                                 @Path("page") int page);


    @Headers("Content-Type: application/json")
    @GET("/api/stores/{store_id}/rewards")
    Call<List<Reward>> getRewards(@Header("Authorization") String authorization,
                                  @Path("store_id") int store_id);


    @Headers("Content-Type: application/json")
    @GET("/api/stores/{store_id}/visits")
    Call<Visit> getVisit(@Header("Authorization") String authorization,
                                  @Path("store_id") int store_id);


    @Headers("Content-Type: application/json")
    @POST("api/redeems/")
    Call<Redeem> createRedeem(@Header("Authorization") String authorization, @Body Redeem redeem);

    @Headers("Content-Type: application/json")
    @GET("api/redeems")
    Call<List<Redeem>> getRedeems(@Header("Authorization") String authorization);

    @Headers("Content-Type: application/json")
    @GET("api/history")
    Call<List<Redeem>> getHistory(@Header("Authorization") String authorization);

    @Headers("Content-Type: application/json")
    @GET("api/insta")
    Call<List<Reward>> getInsta(@Header("Authorization") String authorization);

    @Headers("Content-Type: application/json")
    @PATCH("api/current")
    Call<User> updateUser(@Header("Authorization") String authorization, @Body User user);

    @Headers("Content-Type: application/json")
    @PATCH("api/bills/{bill_id}")
    Call<Bill> updateBill(@Header("Authorization") String authorization, @Path("bill_id") int bill_id, @Body Bill bill);

    @Headers("Content-Type: application/json")
    @GET("api/bills")
    Call<Bill> getBill(@Header("Authorization") String authorization);
}
