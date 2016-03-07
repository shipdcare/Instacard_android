package in.instacard.instacard.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.instacard.instacard.R;
import in.instacard.instacard.controllers.RestManager;
import in.instacard.instacard.controllers.Utilities;
import in.instacard.instacard.models.callback.User;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

@EActivity(R.layout.activity_phone)
public class PhoneActivity extends AppCompatActivity {

    Realm realm;
    RestManager restManager;

    @ViewById
    EditText promo, phone;

    @ViewById
    ProgressBar progressBar;

    String mPhone, mPromo;

    @ViewById
    TextView next;

    @ViewById
    ImageView back;

    @Click
     void seePlacesClicked(){

    }

    @Bean
    Utilities utilities;

    @Click
    void nextClicked(){

        next.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        mPhone = phone.getText().toString().trim();
        mPromo = promo.getText().toString().trim();

        realm = Realm.getInstance(this);
        final User u = realm.where(User.class).findFirst();

        User user = new User();
        if(mPhone.length() == 10){
            user.setPhone(mPhone);
            if(mPromo != null) {
                user.setPromoCode(mPromo);
            }
            restManager = new RestManager();
            Call<User> call = restManager.getUserService().updateUser(u.getAuthToken(), user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Response<User> response) {
                    if(response.isSuccess() && response.body() != null){
                        realm.beginTransaction();
                        u.setPhone(response.body().getPhone());
                        if(response.body().getPromoCode() != null) {
                            u.setPromoCode(response.body().getPromoCode());
                        }
                        u.setPointCount(response.body().getPointCount());
                        realm.commitTransaction();

                        MainActivity_.intent(PhoneActivity.this).start();
                    }
                    else{
                        try {
                            String json = response.errorBody().string();
                            JSONObject errorData = new JSONObject(json);
                            JSONArray errors = errorData.getJSONArray("errors");
                            JSONObject error = errors.getJSONObject(0);
                            Log.i("ERROR", error.getString("title"));

                            utilities.createDialog(PhoneActivity.this, error.getString("id") + " invalid!", error.getString("title"));
                            progressBar.setVisibility(View.GONE);


                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            utilities.toaster(PhoneActivity.this, "Oops!");
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    utilities.toaster(PhoneActivity.this, "Oops! Something went wrong!");
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else{
            utilities.createDialog(this, "Error", "Enter a valid 10 digit number");
        }


        next.setVisibility(View.VISIBLE);
    }


    @AfterViews
    void init(){
        back.setColorFilter(Color.rgb(183, 183, 183), android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
