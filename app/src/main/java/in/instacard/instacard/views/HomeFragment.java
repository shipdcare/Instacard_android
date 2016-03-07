package in.instacard.instacard.views;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import in.instacard.instacard.R;
import in.instacard.instacard.adapters.StoresAdapter;
import in.instacard.instacard.controllers.RestManager;
import in.instacard.instacard.controllers.Utilities;
import in.instacard.instacard.models.callback.Store;
import in.instacard.instacard.models.callback.User;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@EFragment(R.layout.fragment_home)
public class HomeFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener {

    @Bean
    Utilities utilities;

    @ViewById
    ListView list;

    @ViewById
    SwipeRefreshLayout storesList;

    @ViewById
    TextView empty;

    @ViewById
    ProgressBar progressBar;

    @Click
    void search_fab(){
        SearchActivity_.intent(this).start();
    }

    RestManager restManager;

    protected List<Store> storeList;

    int refresh_count = 0;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(){
        HomeFragment fragment = new HomeFragment_();
        return fragment;
    }

    @AfterViews
    void home_init(){
        storesList.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.swipeRefresh1),
                ContextCompat.getColor(getActivity(), R.color.swipeRefresh2),
                ContextCompat.getColor(getActivity(), R.color.swipeRefresh4));
        storesList.setOnRefreshListener(this);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(utilities.isNetworkAvailable(getActivity())) {
            retrieveStores();
        }
        else{
            refresh_count++;
            while (refresh_count<3) {
                if(utilities.isNetworkAvailable(getActivity())) {
                    retrieveStores();
                }
            }
            utilities.createDialog(getActivity(), "Network error", "Make sure you are connected to the internet");
        }
    }

    private void retrieveStores() {

        progressBar.setVisibility(View.VISIBLE);

        restManager = new RestManager();

        Realm realm = Realm.getInstance(getActivity());
        User user = realm.where(User.class).findFirst();

        if(utilities.isNetworkAvailable(getActivity()) && user != null && user.getPhone() != null) {
            Call<List<Store>> listCall = restManager.getUserService().getStores(user.getAuthToken(), 1);
            listCall.enqueue(new Callback<List<Store>>() {
                @Override
                public void onResponse(Response<List<Store>> response) {

                    progressBar.setVisibility(View.INVISIBLE);

                    if (response.isSuccess() && response.body() != null) {
                        storeList = response.body();
                        Log.i("LIST", storeList.toString());

                        if (storeList.isEmpty()) {
                            empty.setVisibility(View.VISIBLE);
                        }


                        StoresAdapter adapter = new StoresAdapter(
                                getActivity(),
                                storeList);
                        list.setAdapter(adapter);

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Store store = storeList.get(position);

                                Intent intent = new Intent(getActivity(), StoreActivity_.class);
                                intent.putExtra("id", store.getId());
                                intent.putExtra("name", store.getName());
                                intent.putExtra("street", store.getStreet());
                                intent.putExtra("cover", store.getCoverUrl());
                                startActivity(intent);
                            }
                        });

                    } else {
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
                                Log.e("Error 204", "Error");
                                break;
                            default:
                                Log.e("Error", "Generic Error");
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                    while (refresh_count < 2) {
                        retrieveStores();
                        refresh_count++;
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onRefresh() {
        retrieveStores();
        storesList.setRefreshing(false);
    }
}
