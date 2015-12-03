package com.ccss.youthvolunteer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RecyclerHeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    String[] data;

    public RecyclerHeaderAdapter(String[] data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new ViewHolderItem(null);
        } else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            return new ViewHolderHeader(null);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderItem) {
            String dataItem = getItem(position);
            //cast holder to ViewHolderItem and set data
        } else if (holder instanceof ViewHolderHeader) {
            //cast holder to ViewHolderHeader and set data for header.
        }
    }

    @Override
    public int getItemCount() {
        return data.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private String getItem(int position) {
        return data[position - 1];
    }



    class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolderItem(View itemView) {
            super(itemView);
        }
    }


    class ViewHolderHeader extends RecyclerView.ViewHolder {
        Button button;

        public ViewHolderHeader(View itemView) {
            super(itemView);
        }
    }
}