package in.instacard.instacard.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SearchView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import in.instacard.instacard.R;
import in.instacard.instacard.controllers.Utilities;


@EActivity(R.layout.activity_search)
public class SearchActivity extends AppCompatActivity {

    @ViewById
    SearchView searchView;

    @Bean
    Utilities utilities;

    @AfterViews
    void search_int(){
    }
}
