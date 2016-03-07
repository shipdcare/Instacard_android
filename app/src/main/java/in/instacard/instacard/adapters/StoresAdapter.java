package in.instacard.instacard.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

import in.instacard.instacard.R;
import in.instacard.instacard.helpers.Constants;
import in.instacard.instacard.models.callback.Store;


public class StoresAdapter extends ArrayAdapter<Store> {

    protected Context mContext;
    protected List<Store> mStores;

    public StoresAdapter(Context context, List<Store> stores) {
        super(context, R.layout.item_store, stores);
        mContext = context;
        mStores = stores;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_store, null);
            holder = new ViewHolder();
            holder.cover = (ImageView)convertView.findViewById(R.id.store_cover);
            holder.nameLabel = (TextView)convertView.findViewById(R.id.store_name);
            holder.streetLabel= (TextView) convertView.findViewById(R.id.store_street);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        Store store = mStores.get(position);

        try {
            holder.nameLabel.setText(store.getName());
            holder.streetLabel.setText(store.getStreet());
            Glide.with(mContext).load(store.getCoverUrl()).placeholder(R.color.black).crossFade().into(holder.cover);
            holder.cover.setColorFilter(Color.rgb(183, 183, 183), android.graphics.PorterDuff.Mode.MULTIPLY);
        } catch (NullPointerException e){
            Log.i("NULL-EXCEPTION",  e.getMessage());
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView cover;
        TextView nameLabel;
        TextView streetLabel;
    }

    public void refill(List<Store> stores) {
        mStores.clear();
        mStores.addAll(stores);
        notifyDataSetChanged();
    }

}
