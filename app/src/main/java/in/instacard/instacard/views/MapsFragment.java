package in.instacard.instacard.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import in.instacard.instacard.R;
import in.instacard.instacard.adapters.RewardsAdapter2;
import in.instacard.instacard.adapters.StoresAdapter;
import in.instacard.instacard.adapters.StoresAdapter2;
import in.instacard.instacard.controllers.RestManager;
import in.instacard.instacard.controllers.Utilities;
import in.instacard.instacard.helpers.GPSTracker;
import in.instacard.instacard.helpers.RecyclerItemClickListener;
import in.instacard.instacard.models.callback.Store;
import in.instacard.instacard.models.callback.User;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

@EFragment(R.layout.fragment_maps)
public class MapsFragment extends Fragment {

    private static GoogleMap mMap;
    private static Double latitude, longitude;


    @ViewById
    RecyclerView mapsList;

    @ViewById
    TextView empty;

    List<Store> mStores;

    public MapsFragment() {
    }

    @Bean
    Utilities utilities;

    RestManager restManager;

    public static MapsFragment newInstance() {
        return new MapsFragment_();
    }

    @AfterViews
    void init() {

        getUserLocation();

        final SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                        longitude), 12.5f));

                if(utilities.isNetworkAvailable(getActivity())) {
                    retrieveStores();
                }

            }
        });
    }

    private void getUserLocation() {
        GPSTracker gps = new GPSTracker(getActivity());
        latitude= gps.getLatitude();
        longitude =gps.getLongitude();
    }


    private void retrieveStores() {

        restManager = new RestManager();

        Realm realm = Realm.getInstance(getActivity());
        User user = realm.where(User.class).findFirst();

        if(utilities.isNetworkAvailable(getActivity()) && user != null && user.getPhone() != null) {
            Call<List<Store>> listCall = restManager.getUserService().getStores(user.getAuthToken(), 1);
            listCall.enqueue(new Callback<List<Store>>() {
                @Override
                public void onResponse(Response<List<Store>> response) {
                    if (response.isSuccess() && response.body() != null) {

                        mStores = response.body();

                        StoresAdapter2 storesAdapter2 = new StoresAdapter2(getActivity(), mStores);
                        mapsList.setAdapter(storesAdapter2);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
                        mapsList.setLayoutManager(layoutManager);
                        mapsList.setHasFixedSize(true);

                        for(Store store : mStores){
                            LatLng latLng = new LatLng(store.getLatitude(), store.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng));
                        }

                        mapsList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Store store = mStores.get(position);

                                Intent intent = new Intent(getActivity(), StoreActivity_.class);
                                intent.putExtra("id", store.getId());
                                intent.putExtra("name", store.getName());
                                intent.putExtra("street", store.getStreet());
                                //intent.putExtra("cover", store.getCoverUrl());
                                startActivity(intent);
                            }
                        }));

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
                                Log.e("Error 204", "Error");
                                break;
                            default:
                                Log.e("Error", "Generic Error");
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    empty.setVisibility(View.VISIBLE);
                }
            });
        }
    }

}
