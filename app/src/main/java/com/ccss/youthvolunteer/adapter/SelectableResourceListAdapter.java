package com.ccss.youthvolunteer.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.activity.ManageResourcesFragment;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.util.Constants;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class SelectableResourceListAdapter extends RecyclerView.Adapter<SelectableResourceListAdapter.ResourceViewHolder> implements View.OnClickListener  {
    private static final String TAG = "ResourceListAdapter";
    private List<ResourceModel> mResources;
    private Map<String, Boolean> mItemSelectionStates = Maps.newHashMap();
    private ManageResourcesFragment.RecyclerItemClickListener mItemClickListener;
    private SparseBooleanArray selectedItems;
    boolean mSelectable;
    int mViewType;

    /**
     * Initialize the list of the Adapter.
     *
     * @param resources List containing the data to populate views to be used by RecyclerView.
     */
    public SelectableResourceListAdapter(List<ResourceModel> resources, boolean isSelectable, int viewType) {
        mResources = resources;
        selectedItems = new SparseBooleanArray();
        mSelectable = isSelectable;
        mViewType = viewType;
    }

    public SelectableResourceListAdapter(List<ResourceModel> resources) {
        mResources = resources;
        selectedItems = new SparseBooleanArray();
        mSelectable = true;
        mViewType = Constants.GENERAL_ITEM;
    }

    /**
     * Adds and item into the underlying data set
     * at the position passed into the method.
     *
     * @param newResourceData The item to add to the data set.
     * @param position The index of the item to add.
     */
    public void addDataItem(ResourceModel newResourceData, int position) {
        mResources.add(position, newResourceData);
        notifyItemInserted(position);
    }

    /**
     * Removes the item that currently is at the passed in position from the
     * underlying data set.
     *
     * @param position The index of the item to remove.
     */
    public void removeDataItem(int position) {
        mResources.remove(position);
        notifyItemRemoved(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ResourceModel model = mResources.remove(fromPosition);
        mResources.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void animateTo(List<ResourceModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ResourceModel> newModels) {
        for (int i = mResources.size() - 1; i >= 0; i--) {
            final ResourceModel model = mResources.get(i);
            if (!newModels.contains(model)) {
                removeDataItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ResourceModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ResourceModel model = newModels.get(i);
            if (!mResources.contains(model)) {
                addDataItem(model, i);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ResourceModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ResourceModel model = newModels.get(toPosition);
            final int fromPosition = mResources.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    @Override
    public void onClick(final View v) {
        // Give some time to the ripple to finish the effect
        //TODO: Identify the view
        if (mItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mItemClickListener.onItemClick(v,
                            ((TextView) v.findViewById(R.id.resource_object_type)).getText().toString(),
                            ((TextView) v.findViewById(R.id.resource_object_id)).getText().toString());
                }
            }, 0);
        }
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public ResourceModel getItem(int position) {
        return mResources.get(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ResourceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v;
        switch (viewType){
            case Constants.OPPORTUNITY_ITEM:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.opportunity_list_item, viewGroup, false);
                break;
            case Constants.POST_ITEM:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.manage_resource_item, viewGroup, false);
                break;
            case Constants.VOLUNTEER_ITEM:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.volunteer_user_item, viewGroup, false);
                break;
            case Constants.COMMENT_ITEM:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.volunteer_user_item, viewGroup, false);
                break;
            default:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.manage_resource_item, viewGroup, false);
        }

        return new ResourceViewHolder(v, viewType);
    }

    public List<String> getSelectedItems(){
        Map<String, Boolean> selectedItems = Maps.filterEntries(mItemSelectionStates, new Predicate<Map.Entry<String, Boolean>>() {
            @Override
            public boolean apply(Map.Entry<String, Boolean> input) {
                return input.getValue();
            }
        });
        return Lists.newArrayList(selectedItems.keySet());
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ResourceViewHolder viewHolder, final int position) {
        // Get element from list at this position and replace the contents of the view with that element

        final ResourceModel currentResource = mResources.get(position);
        String resourceType = currentResource.getResourceType();

        if(Constants.VOLUNTEER_USER_RESOURCE.equals(resourceType)) {

        } else if(Constants.USER_POST_RESOURCE.equals(resourceType)) {

        } else if(Constants.USER_COMMENT_RESOURCE.equals(resourceType)) {

        } else {
            viewHolder.getResourceType().setText(resourceType);

            viewHolder.getTitleTextView().setText(currentResource.getTitle());
            viewHolder.getDescriptionTextView().setText(currentResource.getDescription());

            if(!currentResource.getBorderColor().isEmpty()){
                viewHolder.getBorderColorTextView().setBackgroundColor(Color.parseColor(currentResource.getBorderColor()));
            }
            viewHolder.getExtraInfoBottomTextView().setText(currentResource.getExtraInformationBottom());
            viewHolder.getExtraInfoTopRightTextView().setText(currentResource.getExtraInformationTopRight());

            if(Constants.OPPORTUNITY_RESOURCE.startsWith(resourceType)){
                if( currentResource.isStarred()){
                    viewHolder.getStarredImageView().setImageResource(android.R.drawable.btn_star_big_on);
                }
            } else {
                viewHolder.getStarredImageView().setVisibility(View.GONE);
            }

            viewHolder.itemView.setActivated(selectedItems.get(position, false));
            if(!currentResource.isActive()){
                viewHolder.itemView.setAlpha(Constants.INACTIVE_ALPHA);
            }

            viewHolder.getObjectIdTextView().setText(currentResource.getObjectId());
            // provide support for selected state
            updateCheckedState(viewHolder, currentResource);

            if(mSelectable) {
                viewHolder.getResourceImageLayoutView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentResource.setSelected(!currentResource.isSelected());
                        updateCheckedState(viewHolder, currentResource);
                    }
                });
            }

            viewHolder.getStarredImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentResource.setStarred(!currentResource.isStarred());
                    updateStarredState(viewHolder, currentResource);
                }
            });
        }

//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mFragment.onItemClicked(currentResource.getObjectId());
//            }
//        });
//        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                mFragment.onItemLongClicked(position);
//                return true;
//            }
//        });
    }

    private void updateCheckedState(ResourceViewHolder holder, ResourceModel item) {
        String resourceImageUri = item.getImageUri();
        if(Strings.isNullOrEmpty(item.getTitle())) {
            return;
        }
        String firstChar = item.getTitle().substring(0, 1).toUpperCase();
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int alphabetColor = !item.getBorderColor().isEmpty()
                                    ? Color.parseColor(item.getBorderColor())
                                    : generator.getColor(firstChar);
        final TextDrawable drawable = TextDrawable.builder().buildRound(firstChar, alphabetColor);

        if(!Strings.isNullOrEmpty(resourceImageUri)){
            holder.getResourceImageView().setImageURI(Uri.parse(resourceImageUri));
        } else {
            holder.getResourceImageView().setImageDrawable(drawable);
        }

        mItemSelectionStates.put(item.getObjectId(), item.isSelected());

        if(item.isSelected()) {
            holder.getResourceImageView().setVisibility(View.GONE);
            holder.getSelectedImage().setVisibility(View.VISIBLE);
        } else {
            holder.getResourceImageView().setVisibility(View.VISIBLE);
            holder.getSelectedImage().setVisibility(View.GONE);
        }
    }

    private void updateStarredState(ResourceViewHolder holder, ResourceModel item) {

        if(item.isStarred()) {
            holder.getStarredImageView().setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.getResourceImageView().setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    // Return the size of your list (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mResources.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    public void setOnItemClickListener(ManageResourcesFragment.RecyclerItemClickListener clickListener) {
        mItemClickListener = clickListener;
    }

    /**
     * Provide a reference to the type of views that are being used per row
     */
    public static class ResourceViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mResourceItemContainer;
        private FrameLayout mResourceImageItem;
        private ImageView mResourceImage;
        private ImageView mCommentImage;
        private ImageView mLikeImage;
        private ImageView mSelectedImage;
        private TextView mResourceTitle;
        private TextView mDescription;
        private TextView mExtraInfoTopRight;
        private TextView mExtraInfoBelowDesc;
        private TextView mExtraInfoBottom;
        private TextView mColorBorder;
        private TextView mObjectId;
        private ImageView mStarred;
        private TextView mResourceType;
        private ManageResourcesFragment.RecyclerItemClickListener mItemClickListener;

        public ResourceViewHolder(View v, int viewType) {
            super(v);
//            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Element " + getLayoutPosition() + " clicked.");
//                }
//            });

            //v.setOnClickListener(this);

            switch (viewType){
                case Constants.OPPORTUNITY_ITEM:
                    mResourceItemContainer = (RelativeLayout) v.findViewById(R.id.opp_item_container);
                    mResourceImage = (ImageView) v.findViewById(R.id.opp_row_item_image);
                    mSelectedImage = (ImageView) v.findViewById(R.id.opp_selected_icon);
                    mResourceImageItem = (FrameLayout) v.findViewById(R.id.opp_item_image);
                    mResourceTitle = (TextView) v.findViewById(R.id.opp_title);
                    mDescription = (TextView) v.findViewById(R.id.opp_description);
                    mExtraInfoTopRight = (TextView) v.findViewById(R.id.opp_info_extra);
                    mExtraInfoBelowDesc = (TextView) v.findViewById(R.id.opp_below_desc);
                    mExtraInfoBottom = (TextView) v.findViewById(R.id.opp_bottom);
                    mColorBorder = (TextView) v.findViewById(R.id.opp_category_indicator);
                    mObjectId = (TextView) v.findViewById(R.id.opp_object_id);
                    mStarred = (ImageView) v.findViewById(R.id.opp_starred);
                    mResourceType = (TextView) v.findViewById(R.id.opp_object_type);
                    break;
                case Constants.POST_ITEM:
                    mResourceItemContainer = (RelativeLayout) v.findViewById(R.id.post_container);
                    mResourceImage = (ImageView) v.findViewById(R.id.post_person_photo);
                    mResourceTitle = (TextView) v.findViewById(R.id.post_description);
                    mDescription = (TextView) v.findViewById(R.id.post_content);
                    mExtraInfoBelowDesc = (TextView) v.findViewById(R.id.post_comment);
                    mCommentImage = (ImageView) v.findViewById(R.id.post_comment_action);
                    mExtraInfoBottom = (TextView) v.findViewById(R.id.post_like);
                    mLikeImage = (ImageView) v.findViewById(R.id.post_like_action);
                    break;
                case Constants.VOLUNTEER_ITEM:
                    break;
                default:
                    mResourceItemContainer = (RelativeLayout) v.findViewById(R.id.resource_item_container);
                    mResourceImage = (ImageView) v.findViewById(R.id.row_item_image);
                    mSelectedImage = (ImageView) v.findViewById(R.id.resource_selected_icon);
                    mResourceImageItem = (FrameLayout) v.findViewById(R.id.resource_item_image);
                    mResourceTitle = (TextView) v.findViewById(R.id.resource_title);
                    mDescription = (TextView) v.findViewById(R.id.resource_description);
                    mExtraInfoTopRight = (TextView) v.findViewById(R.id.resource_info_extra);
                    mExtraInfoBelowDesc = (TextView) v.findViewById(R.id.resource_below_desc);
                    mExtraInfoBottom = (TextView) v.findViewById(R.id.resource_bottom);
                    mColorBorder = (TextView) v.findViewById(R.id.category_indicator);
                    mObjectId = (TextView) v.findViewById(R.id.resource_object_id);
                    mStarred = (ImageView) v.findViewById(R.id.resc_starred);
                    mResourceType = (TextView) v.findViewById(R.id.resource_object_type);
                    break;

            }



            //mOppColorBorder = (TextView) v.findViewById(R.id.opp_category_indicator);

            final String[] resourceType = {""};

//            mEditResource.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Element with id " + getObjectIdTextView().getText() + " clicked for edit.");
//
////                    resourceType[0] = mResourceType.getText().toString();
////                    if(Constants.OPPORTUNITY_RESOURCE.equalsIgnoreCase(resourceType[0])){
////                        Intent intent = new Intent(v.getContext(), ManageVolunteerOpportunityActivity.class);
////                        intent.putExtra(Constants.MANAGE_ITEM_KEY, resourceType[0]);
////                        intent.putExtra(Constants.OBJECT_ID_KEY, getObjectIdTextView().getText());
////                        startActivity(intent);
//////                        startManageActivityWithIntent(, userOrganization);
////                    } else {
////                        startManageActivityWithIntent(ManageSingleResourceActivity.class, mResourceType);
////                    }
//                }
//            });

//            mResourceImageItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Log.d(TAG, "Element with id " + getObjectIdTextView().getText() + " checked.");
//                }
//            });
        }

        //listener passed to viewHolder
        public interface ResourceClickListener {
            void resourceItemOnClick(String resourceId);
        }

        public RelativeLayout getResourceItemContainer() {
            return mResourceItemContainer;
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

        public TextView getExtraInfoTopRightTextView() {
            return mExtraInfoTopRight;
        }

        public TextView getExtraInfoBelowDescTextView() {
            return mExtraInfoBelowDesc;
        }

        public TextView getExtraInfoBottomTextView() {
            return mExtraInfoBottom;
        }

        public TextView getBorderColorTextView() {
            return mColorBorder;
        }

        public TextView getObjectIdTextView() {
            return mObjectId;
        }

        public ImageView getStarredImageView() {
            return mStarred;
        }

        public TextView getResourceType() {
            return mResourceType;
        }

//        @Override
//        public void onClick(View v) {
//
//        }

    }
}