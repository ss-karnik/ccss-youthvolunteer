package com.ccss.youthvolunteer.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.util.Constants;

public class ManageVolunteerOpportunityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent manageIntent = getIntent();
        final String organizationName = manageIntent.getStringExtra(Constants.MANAGE_ITEM_KEY);
        setTitle(getResources().getText(R.string.manage_title) + " " + getResources().getText(R.string.title_opportunity_detail));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_volunteer_opportunity);
    }
}
