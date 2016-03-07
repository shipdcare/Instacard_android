package in.instacard.instacard.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import in.instacard.instacard.helpers.Constants;
import in.instacard.instacard.models.callback.UserService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by mohakgoyal on 2/3/16.
 */
public class RestManager {

    private UserService mUserService;

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    public UserService getUserService(){

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        if(mUserService ==null){

            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            //httpClient.addInterceptor(logging);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.HTTP.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();

            mUserService = retrofit.create(UserService.class);
        }
        return mUserService;
    }


}
