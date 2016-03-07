package in.instacard.instacard.views;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
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

@EFragment(R.layout.fragment_enrolled)
public class EnrolledFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @ViewById
    ListView redeemsList;

    @ViewById
    LinearLayout empty;

    @Bean
    Utilities utilities;

    @ViewById
    ProgressBar progressBar;

    @ViewById
    SwipeRefreshLayout refreshRedeems;

    List<Redeem> mRedeems;

    RestManager restManager;
    Realm realm;

    int refresh_count= 0;

    public EnrolledFragment() {
    }

    public static EnrolledFragment newInstance(){
        EnrolledFragment fragment = new EnrolledFragment_();
        return fragment;
    }

    @AfterViews
    void enrolled_init() {
        refreshRedeems.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.swipeRefresh1),
                ContextCompat.getColor(getActivity(), R.color.swipeRefresh2),
                ContextCompat.getColor(getActivity(), R.color.swipeRefresh4));
        refreshRedeems.setOnRefreshListener(this);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    void refreshList(){

        realm = Realm.getInstance(getActivity());
        User user = realm.where(User.class).findFirst();

        restManager = new RestManager();

        progressBar.setVisibility(View.VISIBLE);
        if(utilities.isNetworkAvailable(getActivity()) && user !=null && user.getPhone() !=null) {
            Call<List<Redeem>> call = restManager.getUserService().getRedeems(user.getAuthToken());
            call.enqueue(new Callback<List<Redeem>>() {
                @Override
                public void onResponse(Response<List<Redeem>> response) {
                    if(response.isSuccess() && response.body() != null){
                        progressBar.setVisibility(View.INVISIBLE);
                        mRedeems= response.body();
                        RedeemsAdapter redeemsAdapter = new RedeemsAdapter(getActivity(), mRedeems);
                        redeemsList.setAdapter(redeemsAdapter);
                        RealmQuery<Redeem> query = realm.where(Redeem.class).equalTo("state","pending");
                        realm.beginTransaction();
                        query.findAll().clear();
                        realm.copyToRealm(mRedeems);
                        realm.commitTransaction();
                    } else{
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
                                empty.setVisibility(View.VISIBLE);
                                break;
                            default:
                                Log.e("Error", "Generic Error");
                        }
                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    refresh_count ++;
                    if (refresh_count < 2){
                        refreshList();
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        empty.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else{
            RealmQuery<Redeem> query = realm.where(Redeem.class).equalTo("state", "pending");
            mRedeems = query.findAll();
            if (mRedeems.size() != 0) {
                progressBar.setVisibility(View.INVISIBLE);
                RedeemsAdapter redeemsAdapter = new RedeemsAdapter(getActivity(), mRedeems);
                redeemsList.setAdapter(redeemsAdapter);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                empty.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh() {
        refreshList();
        refreshRedeems.setRefreshing(false);
    }
}
