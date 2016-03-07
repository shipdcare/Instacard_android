package in.instacard.instacard.views;


import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.internal.LikeButton;
import com.facebook.share.widget.LikeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import in.instacard.instacard.R;
import in.instacard.instacard.adapters.RedeemsAdapter;
import in.instacard.instacard.adapters.RewardsAdapter2;
import in.instacard.instacard.controllers.RestManager;
import in.instacard.instacard.controllers.Utilities;
import in.instacard.instacard.helpers.RecyclerItemClickListener;
import in.instacard.instacard.models.callback.Redeem;
import in.instacard.instacard.models.callback.Reward;
import in.instacard.instacard.models.callback.User;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@EFragment(R.layout.fragment_instacard)
public class InstacardFragment extends Fragment {

    @ViewById
    TextView profile_name, profile_code, profile_crowns, empty, profile_points, profile_redeems, profile_visits;

    @ViewById
    RecyclerView profile_rewards;


    List<Reward> mRewards;

    @Bean
    Utilities utilities;

    public InstacardFragment() {
        // Required empty public constructor
    }

    public static InstacardFragment newInstance(){
        return new InstacardFragment_();
    }

    RestManager restManager;

    Realm realm;
    User user;

    int points;

    Redeem mRedeem;

    @AfterViews
    void insta_init(){

        realm = Realm.getInstance(getActivity());
        user = realm.where(User.class).findFirst();
        if(user != null) {

            profile_name.setText(user.getFullName());
            profile_code.setText(user.getQr());
            profile_crowns.setText("");
            profile_points.setText(user.getPointCount() + "");
            profile_redeems.setText(user.getRedeemCount() + "");
            profile_visits.setText(user.getVisitCount() + "");

            points = user.getPointCount();

            getRewards();
        }
    }

    void getRewards(){

        restManager = new RestManager();

        if(utilities.isNetworkAvailable(getActivity())) {
            Call<List<Reward>> listCall = restManager.getUserService().getInsta(user.getAuthToken());
            listCall.enqueue(new Callback<List<Reward>>() {
                @Override
                public void onResponse(Response<List<Reward>> response) {
                    if (response.isSuccess() && response.body() != null) {
                        mRewards = response.body();

                        if (profile_rewards == null) {
                            empty.setVisibility(View.VISIBLE);
                        } else {
                            RewardsAdapter2 rewardsAdapter2 = new RewardsAdapter2(getActivity(), mRewards);
                            profile_rewards.setAdapter(rewardsAdapter2);

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            profile_rewards.setLayoutManager(layoutManager);
                            profile_rewards.setHasFixedSize(true);

                            profile_rewards.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Reward reward = mRewards.get(position);
                                    if(reward.getPoints() < points){
                                        utilities.toaster(getActivity(), reward.getPoints() + "");
                                        Redeem redeem = new Redeem();
                                        redeem.setMerchant_id(reward.getMerchant_id());
                                        redeem.setUser_id(user.getId());
                                        redeem.setReward_id(reward.getId());
                                        redeem.setTitle(reward.getTitle());

                                        redeem(redeem);
                                    } else{
                                        utilities.createDialog(getActivity(), "Oops!", "You don't have enough points");
                                    }
                                }
                            }));

                        }
                    } else {
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
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            utilities.createDialog(getActivity(),"Network error", "Please check your internet settings");
        }
    }

    void redeem(Redeem redeem){
        restManager = new RestManager();

        Call<Redeem> redeemCall = restManager.getUserService().createRedeem(user.getAuthToken(), redeem);
        redeemCall.enqueue(new Callback<Redeem>() {
            @Override
            public void onResponse(Response<Redeem> response) {
                if (response.isSuccess() && response.body() != null){

                    realm.beginTransaction();
                    mRedeem = realm.copyToRealm(response.body());
                    realm.commitTransaction();

                }else{
                        try {
                            String json = response.errorBody().string();
                            JSONObject errorData = new JSONObject(json);

                            utilities.createDialog(getActivity(), "Error" , errorData.getString("error"));

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            utilities.toaster(getActivity(), "Oops! Something went wrong!");
                        }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                utilities.toaster(getActivity() , "Redeem failed");
            }
        });
    }

}
