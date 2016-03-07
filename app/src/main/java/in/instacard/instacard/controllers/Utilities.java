package in.instacard.instacard.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.EBean;

import in.instacard.instacard.R;
import in.instacard.instacard.models.callback.Bill;
import in.instacard.instacard.models.callback.Redeem;
import in.instacard.instacard.models.callback.User;
import in.instacard.instacard.views.GetStartedActivity_;
import in.instacard.instacard.views.PhoneActivity_;
import in.instacard.instacard.views.ReviewActivity_;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EBean
public class Utilities {

    RestManager restManager;


    public void setHeadingFont(Context context, TextView tv){
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Multicolore.otf");
        tv.setTypeface(face);
    }

    public void toaster(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        Log.i("Logger", message);
    }

    public Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void createDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#e32a61"));
    }

    public AlertDialog createConfirmDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#e32a61"));
        return dialog;
    }

    public void checkBill(final Context context){
        restManager = new RestManager();
        final Realm realm = Realm.getInstance(context);
        final User u = realm.where(User.class).findFirst();
        if(u != null){
            Call<Bill> call = restManager.getUserService().getBill(u.getAuthToken());
            call.enqueue(new Callback<Bill>() {
                @Override
                public void onResponse(Response<Bill> response) {
                    if(response.isSuccess() && response.body() != null){
                        Bill bill = response.body();

                        Intent intent = new Intent(context, ReviewActivity_.class);
                        intent.putExtra("id", bill.getId());
                        intent.putExtra("name", bill.getStore_name());
                        intent.putExtra("logo", bill.getStore_logo());
                        intent.putExtra("amount", bill.getAmount());
                        intent.putExtra("points", bill.getPoints());
                        intent.putExtra("auth", u.getAuthToken());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.i("TAG", t.getMessage());
                }
            });

        }
    }

    public void refreshUser(Context context){
        restManager = new RestManager();

        final Realm realm = Realm.getInstance(context);
        final User u = realm.where(User.class).findFirst();
        if(u == null){
            Intent intent = new Intent(context, GetStartedActivity_.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } else if(u.getPhone() == null){
            Intent intent = new Intent(context, PhoneActivity_.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } else {

            Call<User> userCall = restManager.getUserService().getUser(u.getAuthToken());
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Response<User> response) {
                    if (response.isSuccess()) {
                        realm.beginTransaction();
                        u.setRedeemCount(response.body().getRedeemCount());
                        u.setPointCount(response.body().getPointCount());
                        realm.commitTransaction();
                    } else {
                        int sc = response.code();
                        switch (sc) {
                            case 400:
                                Log.e("Error 400", "Bad Request");
                                break;
                            case 404:
                                Log.e("Error 404", "Not Found");
                                break;
                            default:
                                Log.e("Error", "Generic Error");
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("USER LOAD ERROR", t.getMessage());
                }
            });
        }
    }
}
