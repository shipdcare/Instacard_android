package in.instacard.instacard.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import in.instacard.instacard.R;


@EFragment(R.layout.fragment_blank)
public class DemoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    @ViewById
    TextView sliderText;

    @ViewById
    ImageView sliderImage;

    public DemoFragment() {
        // Required empty public constructor
    }

    public static  DemoFragment newInstance(int sectionNumber) {
        DemoFragment fragment = new  DemoFragment_();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @AfterViews
    void initialize() {
        int id = getArguments().getInt(ARG_SECTION_NUMBER);
    }

}
