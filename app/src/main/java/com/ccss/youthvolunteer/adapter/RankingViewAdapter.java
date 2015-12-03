package com.ccss.youthvolunteer.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.model.RankingModel;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RankingViewAdapter extends RecyclerView.Adapter<RankingViewAdapter.RankingViewHolder> {
    final List<RankingModel> rankedVolunteers;
    final Context context;

    public RankingViewAdapter(Context context, List<RankingModel> rankedVolunteers){
        this.rankedVolunteers = rankedVolunteers;
        this.context = context;
    }

    @Override
    public RankingViewAdapter.RankingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.action_list_row_layout, viewGroup, false);
        return new RankingViewHolder(context, v);
    }

    @Override
    public void onBindViewHolder(RankingViewAdapter.RankingViewHolder holder, int position) {
        RankingModel volunteerUser = rankedVolunteers.get(position);
        holder.personName.setText(volunteerUser.getName());
        holder.personPointsAndRank.setText(volunteerUser.getPointsAndRank());
        holder.personTitle.setText(volunteerUser.getUserLevel());
        holder.personPhoto.setImageURI(Uri.parse(volunteerUser.getProfilePhotoUri()));
    }

    @Override
    public int getItemCount() {
        return rankedVolunteers.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        CardView personCardView;
        TextView personName;
        TextView personPointsAndRank;
        TextView personTitle;
        ImageView personPhoto;

        RankingViewHolder(Context context, View itemView) {
            super(itemView);
            personCardView = (CardView) itemView.findViewById(R.id.person_view);
            personName = (TextView) itemView.findViewById(R.id.person_name);
            personPointsAndRank = (TextView) itemView.findViewById(R.id.person_points_and_rank);
            personTitle = (TextView) itemView.findViewById(R.id.person_title);
            personPhoto = (ImageView) itemView.findViewById(R.id.person_photo);

            Picasso.with(context)
                    .load(R.drawable.default_avatar)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.default_avatar)
                    .fit()
                    .into(personPhoto);
        }
    }
}
