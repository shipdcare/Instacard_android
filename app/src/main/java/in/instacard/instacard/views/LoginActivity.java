package in.instacard.instacard.views;

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
import in.instacard.instacard.models.callback.User;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Button login;

    @ViewById
    EditText login_phone;

    @ViewById
    EditText login_password;

    @Bean
    Utilities utilities;

    @ViewById
    ProgressBar progressBar;

    String phone, password;

    private RestManager mManager;

    @AfterViews
    void initialize() {
        toolbar.setTitle("Log in");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.yellow));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#e32a61"), PorterDuff.Mode.SRC_IN);
    }

    @Click
    void login(){

        phone = login_phone.getText().toString().trim();
        password = login_password.getText().toString().trim();

        if(phone.isEmpty() || password.isEmpty()){
            utilities.toaster(this, "Make sure all fields are filled");
        }
        else{

            if(utilities.isNetworkAvailable(this)) {

                login.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                mManager = new RestManager();

                User user = new User();
                user.setPhone(phone);
                //user.setPassword(password);

                Call<User> call = mManager.getUserService().signInUser(user);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Response<User> response) {
                        if (response.isSuccess()) {

                            Realm realm = Realm.getInstance(LoginActivity.this);
                            RealmResults<User> results = realm.where(User.class).findAll();
                            realm.beginTransaction();
                            results.clear();
                            User user = realm.copyToRealm(response.body());
                            realm.commitTransaction();

                            utilities.toaster(LoginActivity.this, "User signed in successfully.");

                            MainActivity_.intent(LoginActivity.this).start();

                        } else {
                            try {

                                String json = response.errorBody().string();
                                JSONObject error = new JSONObject(json);
                                Log.i("ERROR", error.optString("errors"));

                                utilities.createDialog(LoginActivity.this, "Error" + " invalid!", error.optString("errors"));

                            } catch (IOException | JSONException e) {
                                utilities.toaster(LoginActivity.this, "Oops! Something went wrong!");
                                e.printStackTrace();
                            }
                            progressBar.setVisibility(View.GONE);
                            login.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("FAILURE_TAG", "throwable-error:" + t.getMessage());
                        utilities.toaster(LoginActivity.this, "Oops! Something went wrong!");
                        login.setVisibility(View.VISIBLE);
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
