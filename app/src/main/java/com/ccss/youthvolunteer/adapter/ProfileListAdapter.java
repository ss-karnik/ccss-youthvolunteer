package com.ccss.youthvolunteer.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.model.ResourceModel;

import java.util.List;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ViewHolder> {
    private static final String TAG = "ProfileListAdapter";

    private List<ResourceModel> mDataSet;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet containing the data to populate views to be used by RecyclerView.
     */
    public ProfileListAdapter(List<ResourceModel> dataSet) {
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.profile_selection_row_layout, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTitleTextView().setText(mDataSet.get(position).getTitle());
        viewHolder.getDescriptionTextView().setText(mDataSet.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            titleTextView = (TextView) v.findViewById(R.id.item_title);
            descriptionTextView = (TextView) v.findViewById(R.id.item_description);
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }
        public TextView getDescriptionTextView() {
            return descriptionTextView;
        }
    }
}
