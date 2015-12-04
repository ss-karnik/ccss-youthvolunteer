package com.ccss.youthvolunteer.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.activity.ManageVolunteerOpportunityActivity;
import com.ccss.youthvolunteer.activity.SingleResourceActivity;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.util.Constants;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ResourceListAdapter extends RecyclerView.Adapter<ResourceListAdapter.ResourceViewHolder> {
    private static final String TAG = "ResourceListAdapter";
    private List<ResourceModel> mResources;
    private Map<String, Boolean> mSelectedPositions = Maps.newHashMap();

    /**
     * Initialize the list of the Adapter.
     *
     * @param resources List containing the data to populate views to be used by RecyclerView.
     */
    public ResourceListAdapter(List<ResourceModel> resources) {
        mResources = resources;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ResourceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.manage_resource_row_layout, viewGroup, false);

        return new ResourceViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ResourceViewHolder viewHolder, final int position) {
        // Get element from list at this position and replace the contents of the view with that element
        final ResourceModel currentResource = mResources.get(position);

        viewHolder.getTitleTextView().setText(currentResource.getTitle());
        viewHolder.getDescriptionTextView().setText(currentResource.getDescription());
        viewHolder.getExtraInfoTextView().setText(currentResource.getExtraInformation());
        viewHolder.getObjectIdTextView().setText(currentResource.getObjectId());
        // provide support for selected state
        updateCheckedState(viewHolder, currentResource);

        viewHolder.getResourceImageLayoutView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentResource.setSelected(!currentResource.isSelected());
                updateCheckedState(viewHolder, currentResource);
            }
        });
    }

    private void updateCheckedState(ResourceViewHolder holder, ResourceModel item) {
        String resourceImageUri = item.getImageUri();
        if(Strings.isNullOrEmpty(item.getTitle())) {
            return;
        }
        String firstChar = item.getTitle().substring(0, 1).toUpperCase();
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int alphabetColor = generator.getColor(firstChar);
        final TextDrawable drawable = TextDrawable.builder().buildRound(firstChar, alphabetColor);

        if(!Strings.isNullOrEmpty(resourceImageUri)){
            holder.getResourceImageView().setImageURI(Uri.parse(resourceImageUri));
        } else {
            holder.getResourceImageView().setImageDrawable(drawable);
        }

        mSelectedPositions.put(item.getObjectId(), item.isSelected());

        if(item.isSelected()) {
            holder.getResourceImageView().setVisibility(View.GONE);
            holder.getSelectedImage().setVisibility(View.VISIBLE);
        } else {
            holder.getResourceImageView().setVisibility(View.VISIBLE);
            holder.getSelectedImage().setVisibility(View.GONE);
        }
    }

    // Return the size of your list (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mResources.size();
    }


    /**
     * Provide a reference to the type of views that are being used per row
     */
    public static class ResourceViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout mResourceImageItem;
        private final ImageView mResourceImage;
        private final ImageView mSelectedImage;
        private final TextView mResourceTitle;
        private final TextView mDescription;
        private final TextView mExtraInfo;
        private final TextView mObjectId;
        private final ImageView mEditResource;


        public ResourceViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getLayoutPosition() + " clicked.");
                }
            });

            mResourceImage = (ImageView) v.findViewById(R.id.row_item_image);
            mSelectedImage = (ImageView) v.findViewById(R.id.resource_selected_icon);
            mResourceImageItem = (FrameLayout) v.findViewById(R.id.resource_item_image);
            mResourceTitle = (TextView) v.findViewById(R.id.resource_title);
            mDescription = (TextView) v.findViewById(R.id.resource_description);
            mExtraInfo = (TextView) v.findViewById(R.id.resource_info_extra);
            mObjectId = (TextView) v.findViewById(R.id.resource_object_id);
            mEditResource = (ImageView) v.findViewById(R.id.btn_edit_resource);

            mEditResource.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element with id " + getObjectIdTextView().getText() + " clicked for edit.");
//                    if(Constants.OPPORTUNITY_RESOURCE.equalsIgnoreCase(resourceValue)){
//                        startManageActivityWithIntent(ManageVolunteerOpportunityActivity.class, userOrganization);
//                    } else {
//                        startManageActivityWithIntent(SingleResourceActivity.class, resourceValue);
//                    }
                }
            });

            mResourceImageItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element with id " + getObjectIdTextView().getText() + " checked.");
                }
            });
        }

        public FrameLayout getResourceImageLayoutView() {
            return mResourceImageItem;
        }

        public ImageView getResourceImageView() {
            return mResourceImage;
        }

        public ImageView getSelectedImage() {
            return mSelectedImage;
        }

        public TextView getTitleTextView() {
            return mResourceTitle;
        }

        public TextView getDescriptionTextView() {
            return mDescription;
        }

        public TextView getExtraInfoTextView() {
            return mExtraInfo;
        }

        public TextView getObjectIdTextView() {
            return mObjectId;
        }

        public ImageView getEditImageView() {
            return mEditResource;
        }

    }
}