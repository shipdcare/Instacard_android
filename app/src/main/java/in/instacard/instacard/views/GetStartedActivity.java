package in.instacard.instacard.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.instacard.instacard.R;
import in.instacard.instacard.controllers.RestManager;
import in.instacard.instacard.controllers.Utilities;
import in.instacard.instacard.helpers.ImageHelper;
import in.instacard.instacard.models.callback.User;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


@EActivity(R.layout.activity_get_started)
public class GetStartedActivity extends AppCompatActivity {

    private static final String TAG= GetStartedActivity.class.getSimpleName();

    //////////////////////// INITIALIZATIONS /////////////////////////////////////
    private CallbackManager callbackManager;

    @ViewById
    LinearLayout getStartedLayout;

    @ViewById
    ProgressBar progressBar;

    @ViewById
    ImageView back;

    @ViewById
    ViewPager viewpager;

    @ViewById
    TextView subhead;

    @ViewById
    CirclePageIndicator indicator;

    List<String> permissionNeeds= Arrays.asList("public_profile","email","user_birthday");

    private RestManager mManager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Bean
    Utilities utilities;

    int refresh_count =0;

    ////////////////////////////// SHOW/ HIDE VIEWS ////////////////////////////////////////////


    /////////////////////// ON CREATE ///////////////////////////////////////

    @AfterViews
    void initialize(){

        fbLoginInitialise();
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.INVISIBLE);

        Glide.with(this).load(R.drawable.back).into(back);
        back.setColorFilter(Color.rgb(183, 183, 183), android.graphics.PorterDuff.Mode.MULTIPLY);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(mSectionsPagerAdapter);
        indicator.setViewPager(viewpager);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position){
                    case 0:
                        subhead.setText("Your loyalty wallet");
                        break;
                    case 1:
                        subhead.setText("Get more by shopping while your favorite stores");
                        break;
                    case 2:
                        subhead.setText("See rewards available and choose the rewards you want to avail");
                        break;
                    case 3:
                        subhead.setText("Redeem rewards anytime you like");
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    ////////////////////// FACEBOOK FUNCTIONALITY  ///////////////////////////

    @Click
    void fbLoginButtonClicked(){
        if(utilities.isNetworkAvailable(this)) {
            getStartedLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            LoginManager.getInstance().logInWithReadPermissions(this, permissionNeeds);
        }else{
            utilities.createDialog(this, "Network error", "Make sure you have active internet connection");
        }
    }


    /////////////////////// FB FUNCTIONALITY //////////////////////////////////

    private void fbLoginInitialise(){

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                mManager = new RestManager();

                                try {
                                    User user = new User();
                                    user.setUid(object.optString("id"));
                                    user.setEmail(object.optString("email"));
                                    user.setFirstName(object.optString("first_name"));
                                    user.setLastName(object.optString("last_name"));
                                    user.setFbAuth(loginResult.getAccessToken().getToken());
                                    user.setFullName(object.optString("name"));

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                    Date test = dateFormat.parse(object.optString("birthday"));

                                    user.setBirthday(test);

                                    createUser(user);

                                } catch (StackOverflowError e) {
                                    utilities.toaster(GetStartedActivity.this, "Stack error");
                                    getStartedLayout.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,name,email,birthday,link");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                utilities.toaster(GetStartedActivity.this, "User cancelled Facebook Login");
                getStartedLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
                utilities.toaster(GetStartedActivity.this, error.getMessage());
                getStartedLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    public void createUser(final User user){
        Call<User> userCall = mManager.getUserService().createUser(user);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if (!response.isSuccess()) {
                    try {
                        String json = response.errorBody().string();
                        JSONObject errorData = new JSONObject(json);
                        JSONArray errors = errorData.getJSONArray("errors");
                        JSONObject error = errors.getJSONObject(0);
                        Log.i("ERROR", error.getString("title"));

                        utilities.createDialog(GetStartedActivity.this, error.getString("id") + " invalid!", error.getString("title"));
                        getStartedLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);


                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        utilities.toaster(GetStartedActivity.this, "Oops! Something went wrong!");
                    }
                } else {
                    Realm realm = Realm.getInstance(GetStartedActivity.this);
                    RealmResults<User> results = realm.where(User.class).findAll();
                    realm.beginTransaction();
                    results.clear();
                    User user = realm.copyToRealm(response.body());
                    realm.commitTransaction();

                    if (user.getPhone() == null) {
                        Intent intent = new Intent(GetStartedActivity.this, PhoneActivity_.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(GetStartedActivity.this, MainActivity_.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

                while(refresh_count <2){
                    createUser(user);
                    refresh_count++;
                }
                Log.i("TAG", t.toString());
                utilities.toaster(GetStartedActivity.this, "Network error. Please try again");
                getStartedLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return GettingStartedFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
