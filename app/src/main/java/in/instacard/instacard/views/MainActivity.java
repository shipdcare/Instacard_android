package in.instacard.instacard.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import in.instacard.instacard.R;
import in.instacard.instacard.controllers.Utilities;
import in.instacard.instacard.models.callback.User;
import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bean
    Utilities utilities;

    private int[] tabIcons = {
            R.drawable.toolbar_home,
            R.drawable.toolbar_bookmark
    };


    private SectionsPagerAdapter mSectionsPagerAdapter;

    @ViewById
    ViewPager container;

    @ViewById
    TabLayout tabs;

    @ViewById
    Toolbar toolbar;

    @ViewById
    DrawerLayout drawer_layout;

    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utilities.refreshUser(this);

    }

    @AfterViews
    void main_initialize(){

        fragmentManager = getSupportFragmentManager();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        container.setAdapter(mSectionsPagerAdapter);
        tabs.setupWithViewPager(container);
        setupTabIcons();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        navigationView.setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        utilities.checkBill(this);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return HomeFragment.newInstance();
                case 1:
                    return EnrolledFragment.newInstance();
            }
            return  null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return tabIcons.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    private void setupTabIcons() {
        tabs.getTabAt(0).setIcon(tabIcons[0]);
        tabs.getTabAt(1).setIcon(tabIcons[1]);
    }

    @Click
    void show_nav(){
        drawer_layout.openDrawer(Gravity.RIGHT);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        int id = menuItem.getItemId();

                        if (id == R.id.nav_home) {
                            MainActivity_.intent(MainActivity.this).start();
                        } else if (id == R.id.nav_history) {
                            HistoryActivity_.intent(MainActivity.this).start();
                        } else if (id == R.id.nav_share) {

                        } else if (id == R.id.nav_logout) {

                            Realm realm = Realm.getInstance(MainActivity.this);
                            User r = realm.where(User.class).findFirst();
                            realm.beginTransaction();
                            r.removeFromRealm();
                            realm.commitTransaction();
                            GetStartedActivity_.intent(MainActivity.this).start();
                        }

                        drawer_layout.closeDrawer(GravityCompat.END);
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
