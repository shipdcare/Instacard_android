package in.instacard.instacard.views;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import in.instacard.instacard.R;
import in.instacard.instacard.adapters.RedeemsAdapter;
import in.instacard.instacard.controllers.RestManager;
import in.instacard.instacard.controllers.Utilities;
import in.instacard.instacard.models.callback.Redeem;
import in.instacard.instacard.models.callback.User;
import io.realm.Realm;
import io.realm.RealmQuery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_history)
public class HistoryActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    ListView list;

    @ViewById
    TextView empty;

    Realm realm;
    RestManager restManager;
    List<Redeem> mRedeems;

    @Bean
    Utilities utilities;


    @AfterViews
    void history() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.parseColor("#e00007"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        toolbar.setTitleTextColor(Color.parseColor("#e00007"));


        realm = Realm.getInstance(this);
        User user = realm.where(User.class).findFirst();

        restManager = new RestManager();

        if(utilities.isNetworkAvailable(this)) {
            Call<List<Redeem>> call = restManager.getUserService().getHistory(user.getAuthToken());
            call.enqueue(new Callback<List<Redeem>>() {
                @Override
                public void onResponse(Response<List<Redeem>> response) {
                    if(response.isSuccess() && response.body() != null){
                        mRedeems= response.body();
                        RedeemsAdapter redeemsAdapter = new RedeemsAdapter(HistoryActivity.this, mRedeems);
                        list.setAdapter(redeemsAdapter);
                        RealmQuery<Redeem> query = realm.where(Redeem.class).equalTo("state", "redeemed");
                        realm.beginTransaction();
                        query.findAll().clear();
                        realm.copyToRealm(mRedeems);
                        realm.commitTransaction();
                    } else{
                        int sc = response.code();
                        switch (sc) {
                            case 400:
                                Log.e("Error 400", "Bad Request");
                                break;
                            case 404:
                                Log.e("Error 404", "Not Found");
                                break;
                            case 204:
                                empty.setVisibility(View.VISIBLE);
                                break;
                            default:
                                Log.e("Error", "Generic Error");
                        }
                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    empty.setVisibility(View.VISIBLE);
                    utilities.createDialog(HistoryActivity.this, "Oops!", "There was an error.");
                }
            });
        } else{
            RealmQuery<Redeem> query = realm.where(Redeem.class).equalTo("state", "redeemed");
            mRedeems = query.findAll();
            if (mRedeems.size() != 0) {
                RedeemsAdapter redeemsAdapter = new RedeemsAdapter(this, mRedeems);
                list.setAdapter(redeemsAdapter);
            } else {
                empty.setVisibility(View.VISIBLE);
            }
        }
    }
}
