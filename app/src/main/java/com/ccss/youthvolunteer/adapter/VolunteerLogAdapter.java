package com.ccss.youthvolunteer.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.model.UserAction;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class VolunteerLogAdapter extends RecyclerView.Adapter<VolunteerLogAdapter.VolunteerLogViewHolder> {
    private static final String TAG = "VolunteerLogAdapter";
    private List<UserAction> mUserActions;

    /**
     * Initialize the list of the Adapter.
     *
     * @param userActions List containing the data to populate views to be used by RecyclerView.
     */
    public VolunteerLogAdapter(List<UserAction> userActions) {
        mUserActions = userActions;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public VolunteerLogViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.volunteer_log_action_row, viewGroup, false);

        return new VolunteerLogViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(VolunteerLogViewHolder viewHolder, final int position) {
        // Get element from list at this position and replace the contents of the view with that element

        UserAction currentAction = mUserActions.get(position);

       // viewHolder.getCategoryIndicatorTextView().setBackgroundColor(Color.parseColor(currentAction.getAction().getActionCategory().getCategoryColor()));
        viewHolder.getTitleTextView().setText(currentAction.getAction().getTitle());
        viewHolder.getOrganizationTextView().setText(currentAction.getAction().getOrganizationName());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        viewHolder.getDateTextView().setText(dateFormatter.format(currentAction.getActionDate()));
        viewHolder.getLocationTextView().setText(currentAction.getAction().getLocationName());
        viewHolder.getUserCommentTextView().setText(currentAction.getComments());
        String points = currentAction.isVerified() ? String.valueOf(currentAction.getAction().getActionPoints()) : "-";
        viewHolder.getActionPointsTextView().setText(points);
    }

    // Return the size of your list (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUserActions.size();
    }




    /**
     * Provide a reference to the type of views that are being used per row
     */
    public static class VolunteerLogViewHolder extends RecyclerView.ViewHolder {
        private final TextView mCategoryIndicator;
        private final TextView mActionTitle;
        private final TextView mOrganization;
        private final TextView mActionDate;
        private final TextView mActionLocation;
        private final TextView mUserComment;
        private final TextView mActionPoints;

        public VolunteerLogViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getLayoutPosition() + " clicked.");
                }
            });

            mCategoryIndicator = (TextView) v.findViewById(R.id.log_category_indicator);
            mActionTitle = (TextView) v.findViewById(R.id.log_action_title);
            mOrganization = (TextView) v.findViewById(R.id.log_action_organization);
            mActionDate = (TextView) v.findViewById(R.id.log_action_date);
            mActionLocation  = (TextView) v.findViewById(R.id.log_action_location);
            mUserComment = (TextView) v.findViewById(R.id.log_action_comment);
            mActionPoints = (TextView) v.findViewById(R.id.log_action_points);
        }

        public TextView getCategoryIndicatorTextView() {
            return mCategoryIndicator;
        }

        public TextView getTitleTextView() {
            return mActionTitle;
        }

        public TextView getOrganizationTextView() {
            return mOrganization;
        }

        public TextView getDateTextView() {
            return mActionDate;
        }

        public TextView getLocationTextView() {
            return mActionLocation;
        }

        public TextView getUserCommentTextView() {
            return mUserComment;
        }

        public TextView getActionPointsTextView() {
            return mActionPoints;
        }

    }
}
