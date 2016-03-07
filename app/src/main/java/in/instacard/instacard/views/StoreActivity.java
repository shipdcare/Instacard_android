package in.instacard.instacard.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.instacard.instacard.R;
import in.instacard.instacard.adapters.RewardsAdapter2;
import in.instacard.instacard.controllers.RestManager;
import in.instacard.instacard.controllers.Utilities;
import in.instacard.instacard.helpers.RecyclerItemClickListener;
import in.instacard.instacard.models.callback.Redeem;
import in.instacard.instacard.models.callback.Reward;
import in.instacard.instacard.models.callback.User;
import in.instacard.instacard.models.callback.Visit;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

@EActivity(R.layout.activity_store)
public class StoreActivity extends AppCompatActivity {

    @ViewById
    CollapsingToolbarLayout collapsingToolbar;

    @ViewById
    RecyclerView rewardsList;

    @ViewById
    LinearLayout starLayout;

    @ViewById
    TextView empty, name, street, store_points;

    @Bean
    Utilities utilities;

    @ViewById
    AppBarLayout appbar;

    @ViewById
    ProgressBar progressBar;

    protected List<Reward> rewardList;

    RestManager restManager;

    String store_name, store_street, store_cover;
    int store_id;

    int points;

    Realm realm;
    User user;

    Redeem mRedeem;

    boolean free = true;

    @AfterViews
    void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

        realm = Realm.getInstance(this);
        user = realm.where(User.class).findFirst();

        Intent intent = getIntent();
        store_name = intent.getStringExtra("name");
        store_id = intent.getIntExtra("id", 0);
        store_street = intent.getStringExtra("street");
        store_cover = intent.getStringExtra("cover");

        name.setText(store_name);
        street.setText(store_street);
        setTitle("");

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(store_name);
                    setTitle(store_name);
                    starLayout.setVisibility(View.INVISIBLE);
                    isShow = true;
                } else if (isShow) {
                    starLayout.setVisibility(View.VISIBLE);
                    collapsingToolbar.setTitle("");
                    setTitle("");
                    isShow = false;
                }
            }
        });

        loadBackdrop();
        restManager = new RestManager();
        if(utilities.isNetworkAvailable(this)) {
            Realm realm = Realm.getInstance(this);
            User user = realm.where(User.class).findFirst();

            retrieveVisits(user);
            retrieveRewards();

        } else{
            utilities.createDialog(this, "Network error", "Please check your internet settings");
        }
    }

    private void retrieveRewards(){

        progressBar.setVisibility(View.VISIBLE);
        Call<List<Reward>> listCall = restManager.getUserService().getRewards(user.getAuthToken(), store_id);
        listCall.enqueue(new Callback<List<Reward>>() {
            @Override
            public void onResponse(Response<List<Reward>> response) {

                if (response.isSuccess()) {

                    progressBar.setVisibility(View.INVISIBLE);
                    rewardList = response.body();

                    if (rewardList == null) {
                        empty.setVisibility(View.VISIBLE);
                    } else {
                        RewardsAdapter2 rewardsAdapter2 = new RewardsAdapter2(StoreActivity.this, rewardList);
                        rewardsList.setAdapter(rewardsAdapter2);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(StoreActivity.this);
                        rewardsList.setLayoutManager(layoutManager);
                        rewardsList.setHasFixedSize(true);

                        rewardsList.addOnItemTouchListener(new RecyclerItemClickListener(StoreActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position) {

                                if(free) {
                                    free = false;
                                    final Reward reward = rewardList.get(position);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);
                                    builder.setMessage("Redeem the reward-" + reward.getTitle() + "?")
                                            .setTitle("Confirm action")
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    free = true;
                                                }
                                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (reward.getPoints() < points) {

                                                progressBar.setVisibility(View.VISIBLE);
                                                //utilities.toaster(StoreActivity.this, reward.getPoints() + "");
                                                Redeem redeem = new Redeem();
                                                redeem.setMerchant_id(reward.getMerchant_id());
                                                redeem.setUser_id(user.getId());
                                                redeem.setReward_id(reward.getId());
                                                redeem.setTitle(reward.getTitle());
                                                redeem.setStore_name(store_name);
                                                redeem.setStore_street(store_street);

                                                redeem(redeem);
                                                retrieveVisits(user);

                                                dialog.dismiss();
                                                free = true;
                                            } else {
                                                utilities.createDialog(StoreActivity.this, "Oops!", "You don't have enough points");
                                                free = true;
                                            }
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                    Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                    pbutton.setTextColor(Color.parseColor("#e32a61"));
                                    nbutton.setTextColor(Color.parseColor("#e32a61"));
                                }
                            }
                        }));
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    empty.setVisibility(View.VISIBLE);
                    int sc = response.code();
                    switch (sc) {
                        case 400:
                            Log.e("Error 400", "Bad Request");
                            break;
                        case 404:
                            Log.e("Error 404", "Not Found");
                            break;
                        case 204:
                            break;
                        default:
                            Log.e("Error", "Generic Error");
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(StoreActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    void retrieveVisits(User user){

        progressBar.setVisibility(View.VISIBLE);

        Call<Visit> visitCall = restManager.getUserService().getVisit(user.getAuthToken(), store_id);
        visitCall.enqueue(new Callback<Visit>() {
            @Override
            public void onResponse(Response<Visit> response) {

                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccess() && response.body() != null) {
                    store_points.setText(response.body().getCurrent_points() + "");
                    points = response.body().getCurrent_points();
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    int sc = response.code();
                    switch (sc) {
                        case 400:
                            Log.e("Error 400", "Bad Request");
                            break;
                        case 404:
                            Log.e("Error 404", "Not Found");
                            break;
                        case 204:
                            //empty.setVisibility(View.VISIBLE);
                            store_points.setText("0");
                            break;
                        default:
                            Log.e("Error", "Generic Error");
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(StoreActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    void redeem(final Redeem redeem){
        restManager = new RestManager();

        progressBar.setVisibility(View.VISIBLE);

        Call<Redeem> redeemCall = restManager.getUserService().createRedeem(user.getAuthToken(), redeem);
        redeemCall.enqueue(new Callback<Redeem>() {
            @Override
            public void onResponse(Response<Redeem> response) {
                if (response.isSuccess() && response.body() != null) {

                    progressBar.setVisibility(View.INVISIBLE);

                    realm.beginTransaction();
                    mRedeem = realm.copyToRealm(response.body());
                    realm.commitTransaction();

                    utilities.toaster(StoreActivity.this, "Reward successfully redeemed.");

                    String format = "dd MMMM yyyy";
                    Date date = response.body().getExpiry();
                    SimpleDateFormat form = new SimpleDateFormat(format, Locale.ENGLISH);
                    String expiry = form.format(date);


                    Intent intent = new Intent(StoreActivity.this, RedeemActivity_.class);
                    intent.putExtra("title", response.body().getTitle());
                    intent.putExtra("id", response.body().getId());
                    intent.putExtra("expiry", expiry);
                    intent.putExtra("store", response.body().getStore_name());
                    intent.putExtra("street", response.body().getStore_street());
                    intent.putExtra("state", response.body().getState());
                    intent.putExtra("uid", response.body().getUid());
                    startActivity(intent);

                } else {
                    try {
                        String json = response.errorBody().string();
                        JSONObject errorData = new JSONObject(json);

                        utilities.createDialog(StoreActivity.this, "Error", errorData.getString("error"));

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        utilities.toaster(StoreActivity.this, "Oops! Something went wrong!");
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                utilities.toaster(StoreActivity.this, t.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(store_cover).placeholder(R.color.black).into(imageView);
        imageView.setColorFilter(Color.rgb(163, 163, 163), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
