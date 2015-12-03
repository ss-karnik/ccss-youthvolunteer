package com.ccss.youthvolunteer.util;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.ccss.youthvolunteer.R;
import com.parse.ParseImageView;

public class ExpandableActivityChildViewHolder extends ChildViewHolder {

    public TextView mCategoryIndicator;
    public ParseImageView mActionImage;
    public TextView mTitleText;
    public TextView mOrganizationText;
    public TextView mLocationText;
    public TextView mDateText;
    public ImageButton mLikeButton;
    public ImageButton mDetailsButton;

    public ExpandableActivityChildViewHolder(View itemView) {
        super(itemView);

        mCategoryIndicator = (TextView) itemView.findViewById(R.id.category_indicator);
        mActionImage = (ParseImageView) itemView.findViewById(R.id.action_image);
        mTitleText = (TextView) itemView.findViewById(R.id.action_title);
        mOrganizationText = (TextView) itemView.findViewById(R.id.action_organization);
        mLocationText = (TextView) itemView.findViewById(R.id.action_location);
        mDateText = (TextView) itemView.findViewById(R.id.action_date);
        mLikeButton = (ImageButton) itemView.findViewById(R.id.btn_like);
        mDetailsButton = (ImageButton) itemView.findViewById(R.id.btn_view_details);
    }
}