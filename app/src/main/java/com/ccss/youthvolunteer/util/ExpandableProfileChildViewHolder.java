package com.ccss.youthvolunteer.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.ExpandableAdapter;
import com.google.common.base.Strings;

public class ExpandableProfileChildViewHolder extends ChildViewHolder  {

    public ImageView mImageView;
    public TextView mTitleText;
    public TextView mDescriptionText;
    private boolean mSelectedState;
    private String mTitleFirstChar;
    private ExpandableAdapter.OnItemClickListener mOnItemClickListener;

    public ExpandableProfileChildViewHolder(View itemView) {
        super(itemView);

        mImageView = (ImageView) itemView.findViewById(R.id.row_title_image);
        mTitleText = (TextView) itemView.findViewById(R.id.item_title);
        mDescriptionText = (TextView) itemView.findViewById(R.id.item_description);
    }

    private void updateCheckedState() {
        if(Strings.isNullOrEmpty(mTitleFirstChar)) {
            return;
        }

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int alphabetColor = generator.getColor(mTitleFirstChar);
        final TextDrawable drawable = TextDrawable.builder().buildRound(mTitleFirstChar, alphabetColor);

        if(mSelectedState){
            mImageView.setImageResource(R.drawable.check_sm1);
        } else {
            mImageView.setImageDrawable(drawable);
        }
    }

    public void bind(String title, String description, boolean isSelected, ExpandableAdapter.OnItemClickListener itemClickListener) {
        mTitleText.setText(title);
        mDescriptionText.setText(description);
        mSelectedState = isSelected;
        mTitleFirstChar = title.substring(0, 1).toUpperCase();
        mOnItemClickListener = itemClickListener;
        updateCheckedState();

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mSelectedState = !mSelectedState;
                    updateCheckedState();
                    mOnItemClickListener.onItemClick(mTitleText.getText().toString());
                }
            }
        });
    }

    @Override
    public String toString() {
        return mTitleText.getText().toString();
    }
}