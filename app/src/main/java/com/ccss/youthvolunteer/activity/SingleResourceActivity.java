package com.ccss.youthvolunteer.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.util.Constants;

public class SingleResourceActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent manageIntent = getIntent();
        final String resourceValue = manageIntent.getStringExtra(Constants.MANAGE_ITEM_KEY);
        setTitle(getResources().getText(R.string.manage_title) + " " + resourceValue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource);

        //Set control visibility
        switch (resourceValue){
            case Constants.CATEGORY_RESOURCE:

                break;
            case Constants.INTEREST_RESOURCE:

                break;
            case Constants.SKILL_RESOURCE:

                break;
            case Constants.USER_RESOURCE:

                break;
            case Constants.SCHOOL_RESOURCE:

                break;
            case Constants.GROUP_RESOURCE:

                break;
            case Constants.ORGANIZATION_RESOURCE:

                break;
            case Constants.OPPORTUNITY_RESOURCE:

                break;
        }
    }

    @Override
    public void onClick(View v) {
        //Validate and Save the resource
    }
}
