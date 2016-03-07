package in.instacard.instacard.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import in.instacard.instacard.R;
import in.instacard.instacard.controllers.Utilities;

@EFragment(R.layout.fragment_getting_started)
public class GettingStartedFragment extends Fragment {

    @ViewById
    ImageView sliderImage;

    @Bean
    Utilities utilities;

    public GettingStartedFragment() {
    }

    static String page = "";

    public static  GettingStartedFragment newInstance(int sectionNumber) {
        GettingStartedFragment fragment = new  GettingStartedFragment_();
        Bundle args = new Bundle();
        args.putInt(page, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @AfterViews
       void initialize(){
        switch (getArguments().getInt(page)){
            case 1:
                Glide.with(getActivity()).load(R.drawable.pot1).into(sliderImage);
                break;
            case 2:
                Glide.with(getActivity()).load(R.drawable.pot2).into(sliderImage);
                break;
            case 3:
                Glide.with(getActivity()).load(R.drawable.pot4).into(sliderImage);
                break;
            case 4:
                Glide.with(getActivity()).load(R.drawable.pot3).into(sliderImage);
                break;
            default:
                Glide.with(getActivity()).load(R.drawable.pot1).into(sliderImage);
                break;
        }
    }
}
