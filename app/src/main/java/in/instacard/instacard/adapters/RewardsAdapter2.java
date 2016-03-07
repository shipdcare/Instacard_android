package in.instacard.instacard.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import in.instacard.instacard.R;
import in.instacard.instacard.models.callback.Reward;


public class RewardsAdapter2 extends RecyclerView.Adapter<RewardsAdapter2.RewardViewHolder> {

    private List<Reward> mRewards;
    private Context mContext;
    int mPoints;

    public RewardsAdapter2(Context context, List<Reward> rewards) {
        mContext = context;
        mRewards = rewards;
    }


    @Override
    public RewardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reward, parent, false);
        RewardViewHolder viewHolder = new RewardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RewardViewHolder holder, int position) {
        holder.bindReward(mRewards.get(position));
    }

    @Override
    public int getItemCount() {
        return mRewards.size();
    }

    public class RewardViewHolder extends RecyclerView.ViewHolder {

        public TextView mPointsLabel;
        public TextView mTitleLabel;
        View divider;
        LinearLayout background;

        public RewardViewHolder(View itemView) {
            super(itemView);
            mPointsLabel = (TextView) itemView.findViewById(R.id.reward_points);
            mTitleLabel = (TextView) itemView.findViewById(R.id.reward_title);
            divider = itemView.findViewById(R.id.divider);
            background = (LinearLayout) itemView.findViewById(R.id.reward_background);
        }

        public void bindReward(Reward reward) {
            mPointsLabel.setText(String.valueOf(reward.getPoints()));
            mTitleLabel.setText(reward.getTitle());

            if(reward.getPoints() == 0){
                background.setBackgroundResource(R.drawable.green);
                //background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.green));
                background.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
                mPointsLabel.setVisibility(View.GONE);
                //mTitleLabel.setTextColor(Color.parseColor("#ffffff"));
                divider.setVisibility(View.GONE);
            }
        }
    }
}

