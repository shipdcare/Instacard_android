package in.instacard.instacard.controllers;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.stetho.Stetho;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EApplication;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import in.instacard.instacard.R;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

@EApplication
public class Instacard extends Application {

    public void onCreate() {
        super.onCreate();

        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setShowErrorDetails(false);

        FacebookSdk.sdkInitialize(getApplicationContext());
        Stetho.initializeWithDefaults(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Graphik-Regular.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        Fabric fabric = new Fabric.Builder(this).debuggable(true).kits(new Crashlytics()).build();
        Fabric.with(fabric);
    }
}
