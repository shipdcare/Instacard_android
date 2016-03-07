package in.instacard.instacard.views;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.instacard.instacard.R;
import in.instacard.instacard.controllers.RestManager;
import in.instacard.instacard.controllers.Utilities;
import in.instacard.instacard.models.callback.*;
import in.instacard.instacard.models.callback.User;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_signup)
public class SignupActivity extends AppCompatActivity {

    private RestManager mManager;
    User user;

    @ViewById
    Toolbar toolbar;

    @ViewById
    EditText signup_firstname;

    @ViewById
    EditText signup_lastname;

    @ViewById
    EditText signup_email;

    @ViewById
    EditText signup_phone;

    @ViewById
    EditText signup_password;

    @ViewById
    Button signup;

    @Bean
    Utilities utilities;

    @ViewById
    ProgressBar progressBar;


    String first_name, last_name, email, phone, password;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @AfterViews
    void initialize() {
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar); 
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#e32a61"), PorterDuff.Mode.SRC_IN);
    }

    @Click
    void signup() {
        first_name = signup_firstname.getText().toString().trim();
        last_name = signup_lastname.getText().toString().trim();
        email = signup_email.getText().toString().toLowerCase().trim();
        phone = signup_phone.getText().toString().trim();
        password = signup_password.getText().toString().trim();

        if (first_name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            utilities.toaster(this, "Make sure all fields you fill all the fields");
        } else {

            if(utilities.isNetworkAvailable(this)) {

                signup.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                mManager = new RestManager();

                user = new User();
                user.setFirstName(first_name);
                user.setLastName(last_name);
                user.setFullName(first_name + " " + last_name);
                user.setEmail(email);
                //user.setPassword(password);
                user.setPhone(phone);
                //user.setPasswordConfirmation(password);

                Call<User> call = mManager.getUserService().createUser(user);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Response<User> response) {
                        if (!response.isSuccess()) {
                            try {
                                String json = response.errorBody().string();
                                JSONObject errorData = new JSONObject(json);
                                JSONArray errors = errorData.getJSONArray("errors");
                                JSONObject error = errors.getJSONObject(0);
                                Log.i("ERROR", error.getString("title"));

                               utilities.createDialog(SignupActivity.this, error.getString("id") + " invalid!", error.getString("title"));


                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                                utilities.toaster(SignupActivity.this, "Oops! Something went wrong!");
                            }
                            signup.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Realm realm = Realm.getInstance(SignupActivity.this);
                            RealmResults<User> results = realm.where(User.class).findAll();
                            realm.beginTransaction();
                            results.clear();
                            User user = realm.copyToRealm(response.body());
                            realm.commitTransaction();

                            MainActivity_.intent(SignupActivity.this).start();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("TAG", t.toString());
                        utilities.toaster(SignupActivity.this, "Oops! Something went wrong!");
                        signup.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
            else{
                utilities.createDialog(this, "Network error", "Please make sure that you are connected to the internet");
            }
        }
    }
}