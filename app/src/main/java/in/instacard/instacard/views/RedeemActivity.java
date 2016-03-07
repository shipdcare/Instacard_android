package in.instacard.instacard.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Date;

import in.instacard.instacard.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


@EActivity(R.layout.activity_redeem)
public class RedeemActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView reward, redeem_expiry, redeem_store, redeem_street, redeem_uid;

    @ViewById
    ImageView redeem_qr;

    String uid, title, store, street, expiry;

    @Click
    void nextClicked(){
        MainActivity_.intent(this).start();
    }

    @AfterViews
    void init(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.parseColor("#e00007"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        toolbar.setTitle("ALL DONE!");

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        store = intent.getStringExtra("store");
        street = intent.getStringExtra("street");
        uid = intent.getStringExtra("uid");
        expiry = intent.getStringExtra("expiry");

        reward.setText(title);
        redeem_store.setText(store);
        redeem_street.setText(street);
        redeem_uid.setText(uid);
        redeem_expiry.setText("EXPIRES AT: " + expiry);
        redeem_qr.setImageBitmap(generate_qr(uid));
    }

    private Bitmap generate_qr(String qr){

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = qrCodeWriter.encode(qr, BarcodeFormat.QR_CODE, 512, 512);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? ContextCompat.getColor(this, R.color.light) : Color.WHITE);
            }
        }
        return bmp;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
