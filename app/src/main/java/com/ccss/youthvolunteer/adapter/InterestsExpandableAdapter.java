package com.ccss.youthvolunteer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.model.Interests;
import com.ccss.youthvolunteer.model.ProfileVerticalParent;
import com.ccss.youthvolunteer.util.ExpandableParentViewHolder;
import com.ccss.youthvolunteer.util.ExpandableProfileChildViewHolder;

import java.util.List;

public class InterestsExpandableAdapter extends ExpandableRecyclerAdapter<ExpandableParentViewHolder, ExpandableProfileChildViewHolder> {

    private LayoutInflater mInflater;
    private List<String> mExistingSelections;
    private ExpandableAdapter.OnItemClickListener mOnItemClickListener;

    public InterestsExpandableAdapter(Context context, List<? extends ParentListItem> itemList, List<String> existingSelections, ExpandableAdapter.OnItemClickListener itemClickListener) {
        super(itemList);
        mInflater = LayoutInflater.from(context);
        mExistingSelections = existingSelections;
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public ExpandableParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view = mInflater.inflate(R.layout.expandable_header, parentViewGroup, false);
        return new ExpandableParentViewHolder(view);
    }

    @Override
    public ExpandableProfileChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = mInflater.inflate(R.layout.profile_selection_row_layout, childViewGroup, false);
        return new ExpandableProfileChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(ExpandableParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        ProfileVerticalParent verticalParent = (ProfileVerticalParent) parentListItem;
        parentViewHolder.bind(verticalParent.getParentText());
    }

    @Override
    public void onBindChildViewHolder(ExpandableProfileChildViewHolder childViewHolder, int position, Object childListItem) {
        Interests interests = (Interests) childListItem;

        childViewHolder.bind(interests.getInterestTitle(), interests.getDescription(), mExistingSelections.contains(interests.getInterestTitle()), mOnItemClickListener);
    }

    public void setOnItemClickListener(ExpandableAdapter.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
