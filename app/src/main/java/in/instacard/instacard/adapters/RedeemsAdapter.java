package in.instacard.instacard.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.instacard.instacard.R;
import in.instacard.instacard.models.callback.Redeem;

public class RedeemsAdapter extends ArrayAdapter<Redeem> {

    protected Context mContext;
    protected List<Redeem> mRedeems;

    public RedeemsAdapter(Context context, List<Redeem> redeems) {
        super(context, R.layout.item_redeem, redeems);
        mContext = context;
        mRedeems = redeems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.redeem_ticket, null);
            holder = new ViewHolder();
            holder.reward = (TextView)convertView.findViewById(R.id.reward);
            holder.uid = (TextView)convertView.findViewById(R.id.redeem_uid);
            holder.qr= (ImageView)convertView.findViewById(R.id.redeem_qr);
            holder.store = (TextView) convertView.findViewById(R.id.redeem_store);
            holder.expiry = (TextView) convertView.findViewById(R.id.redeem_expiry);
            holder.street = (TextView) convertView.findViewById(R.id.redeem_street);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        Redeem redeem = mRedeems.get(position);
        holder.reward.setText(redeem.getTitle());
        holder.uid.setText(redeem.getUid());
        holder.qr.setImageBitmap(generate_qr(redeem.getUid()));
        holder.street.setText(redeem.getStore_street());
        holder.store.setText(redeem.getStore_name());

        String format = "dd MMMM yyyy";
        Date date = redeem.getExpiry();
        SimpleDateFormat form = new SimpleDateFormat(format, Locale.ENGLISH);
        String expiry = form.format(date);

        holder.expiry.setText("EXPIRES AT: " + expiry);

        return convertView;
    }


    private static class ViewHolder {
        TextView reward;
        TextView uid;
        ImageView qr;
        TextView expiry;
        TextView store;
        TextView street;
    }

    public void refill(List<Redeem> redeems) {
        mRedeems.clear();
        mRedeems.addAll(redeems);
        notifyDataSetChanged();
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
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? ContextCompat.getColor(mContext, R.color.light) : Color.WHITE);
            }
        }
        return bmp;
    }

}
