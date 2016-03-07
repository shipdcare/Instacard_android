package in.instacard.instacard.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import in.instacard.instacard.R;
import in.instacard.instacard.models.callback.Reward;

public class RewardsAdapter extends ArrayAdapter<Reward> {


    protected Context mContext;
    protected List<Reward> mRewards;

    public RewardsAdapter(Context context, List<Reward> rewards) {
        super(context, R.layout.item_reward, rewards);
        mContext = context;
        mRewards = rewards;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_reward, null);
            holder = new ViewHolder();
            holder.reward_points = (TextView)convertView.findViewById(R.id.reward_points);
            holder.reward_title = (TextView)convertView.findViewById(R.id.reward_title);
            holder.divider = (View) convertView.findViewById(R.id.divider);
            holder.background = (LinearLayout) convertView.findViewById(R.id.reward_background);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        Reward reward = mRewards.get(position);
        holder.reward_points.setText(reward.getPoints() + "");
        holder.reward_title.setText(reward.getTitle());

        if(reward.getPoints() == 0){
            holder.reward_points.setVisibility(View.GONE);
            holder.reward_title.setTextColor(Color.parseColor("#ffffff"));
            holder.divider.setVisibility(View.GONE);
            holder.background.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
        } else{
            holder.background.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView reward_points;
        TextView reward_title;
        View divider;
        LinearLayout background;
    }

    public void refill(List<Reward> rewards) {
        mRewards.clear();
        mRewards.addAll(rewards);
        notifyDataSetChanged();
    }
}
