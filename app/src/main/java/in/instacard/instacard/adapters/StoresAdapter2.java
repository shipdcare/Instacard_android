package in.instacard.instacard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.instacard.instacard.R;
import in.instacard.instacard.models.callback.Reward;
import in.instacard.instacard.models.callback.Store;


public class StoresAdapter2 extends RecyclerView.Adapter<StoresAdapter2.StoreViewHolder> {

    private List<Store> mStores;
    private Context mContext;

    public StoresAdapter2(Context context, List<Store> stores) {
        mContext = context;
        mStores = stores;
    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_map, parent, false);
        StoreViewHolder viewHolder = new StoreViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StoreViewHolder holder, int position) {
        holder.bindStore(mStores.get(position));
    }

    @Override
    public int getItemCount() {
        return mStores.size();
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder {

        public ImageView cover;
        public TextView name;

        public StoreViewHolder(View itemView) {
            super(itemView);
            cover= (ImageView) itemView.findViewById(R.id.map_cover);
            name = (TextView) itemView.findViewById(R.id.map_name);
        }

        public void bindStore(Store store) {
            Glide.with(mContext).load(store.getCoverUrl()).placeholder(R.color.black).crossFade().into(cover);
            name.setText(store.getName());
        }
    }
}
