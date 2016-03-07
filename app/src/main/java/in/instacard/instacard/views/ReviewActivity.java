package in.instacard.instacard.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.instacard.instacard.R;
import in.instacard.instacard.controllers.RestManager;
import in.instacard.instacard.controllers.Utilities;
import in.instacard.instacard.models.callback.Bill;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

@EActivity(R.layout.activity_review)
public class ReviewActivity extends AppCompatActivity {

    @ViewById
    TextView store_name, bill_details, point_details;

    @ViewById
    ImageView store_logo;

    @ViewById
    EditText comment;

    @ViewById
    RatingBar ratingBar;

    @ViewById
    ProgressBar progressBar;

    @ViewById
    Button submit;

    @Bean
    Utilities utilities;

    @ViewById
    Toolbar toolbar;

    int bill_id, points, amount;
    int rating;
    String comm = "";
    String name, logo, auth_token;

    RestManager restManager;

    @Click
    void submitClicked(){

        submit.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        rating = (int) ratingBar.getRating();
        if(comment.getText() != null) {
            comm = comment.getText().toString().trim();
        }
        if(rating != 0){
            Bill bill = new Bill();
            bill.setRating(rating);
            bill.setComment(comm);
            bill.setStatus("submitted");

            restManager = new RestManager();
            Call<Bill> call = restManager.getUserService().updateBill(auth_token, bill_id, bill);
            call.enqueue(new Callback<Bill>() {
                @Override
                public void onResponse(Response<Bill> response) {
                    if(response.isSuccess()){
                        MainActivity_.intent(ReviewActivity.this).start();
                        progressBar.setVisibility(View.INVISIBLE);
                        submit.setVisibility(View.VISIBLE);
                    }else{
                        try {
                            String json = response.errorBody().string();
                            JSONObject errorData = new JSONObject(json);
                            JSONArray errors = errorData.getJSONArray("errors");
                            JSONObject error = errors.getJSONObject(0);
                            Log.i("ERROR", error.getString("title"));
                            utilities.createDialog(ReviewActivity.this, error.getString("id") + " invalid!", error.getString("title"));
                            progressBar.setVisibility(View.INVISIBLE);
                            submit.setVisibility(View.VISIBLE);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    utilities.createDialog(ReviewActivity.this, "Error", "Make sure that you have an internet connection");
                    submit.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }else{
            submit.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            utilities.createDialog(this, "Error", "Please select a rating from 1-5");
        }
    }

    @AfterViews
    void init(){

        setSupportActionBar(toolbar);

        ratingBar.setStepSize(1);

        Intent intent = getIntent();
        bill_id = intent.getIntExtra("id", 0);
        name = intent.getStringExtra("name");
        logo = intent.getStringExtra("logo");
        points = intent.getIntExtra("points", 0);
        amount = intent.getIntExtra("amount", 0);
        auth_token = intent.getStringExtra("auth");

        store_name.setText(name);
        Glide.with(this).load(logo).placeholder(R.drawable.logo).into(store_logo);
        bill_details.setText(getResources().getString(R.string.Rs) + " " + amount + "");
        point_details.setText(points + " stars were added to your account");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
