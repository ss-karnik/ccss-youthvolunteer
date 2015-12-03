package com.ccss.youthvolunteer.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.handler.ItemActionHandler;
import com.ccss.youthvolunteer.util.ListItem;

import java.util.List;

public class GenericListAdapter extends ArrayAdapter<ListItem> {

    private List<ListItem> items;
    private int layoutResourceId;
    private Context context;
    private boolean editMode;

    public GenericListAdapter(Context context, int layoutResourceId, List<ListItem> items, boolean editMode) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        this.editMode = editMode;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        if (row == null)
            row = inflater.inflate(layoutResourceId, parent, false);
        ListItem listItem = items.get(position);
        TextView text1 = (TextView) row.findViewById(R.id.list_item_text1);
        text1.setText(listItem.getText1());
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ItemActionHandler) context).onSelectedItem(position);
            }
        };
        text1.setOnClickListener(listener);
        TextView text2 = (TextView) row.findViewById(R.id.list_item_text2);
        text2.setText(listItem.getText2());
        text2.setOnClickListener(listener);
        return row;
    }

}