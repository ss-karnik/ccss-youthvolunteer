package com.ccss.youthvolunteer.handler;

import com.ccss.youthvolunteer.adapter.GenericListAdapter;

public interface ItemActionHandler {
    void onSelectedItem(int position);
    void onDeleteItem(GenericListAdapter source, int position);
}