package com.ccss.youthvolunteer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.util.Constants;

public class ManageResourceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent manageIntent = getIntent();
        final String resourceValue = manageIntent.getStringExtra(Constants.MANAGE_ITEM_KEY);
        final String userOrganization = manageIntent.getStringExtra(Constants.USER_ORGANIZATION_KEY);

        setTitle(getResources().getText(R.string.manage_title) + " " + resourceValue);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_resources_list_view);

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.resource_fragment_container, ResourcesFragment.newInstance(resourceValue))
            .commit();

//        switch (resourceValue){
//            case Constants.CATEGORY_RESOURCE:
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.resource_fragment_container, ResourcesFragment.newInstance(resourceValue))
//                        .commit();
//                break;
//            case Constants.INTEREST_RESOURCE:
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.resource_fragment_container, ResourcesFragment.newInstance(resourceValue))
//                        .commit();
//                break;
//            case Constants.SKILL_RESOURCE:
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.resource_fragment_container, ResourcesFragment.newInstance(resourceValue))
//                        .commit();
//                break;
//            case Constants.USER_RESOURCE:
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.resource_fragment_container, ResourcesFragment.newInstance(resourceValue))
//                        .commit();
//                break;
//            case Constants.SCHOOL_RESOURCE:
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.resource_fragment_container, ResourcesFragment.newInstance(resourceValue))
//                        .commit();
//                break;
//            case Constants.GROUP_RESOURCE:
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.resource_fragment_container, ResourcesFragment.newInstance(resourceValue))
//                        .commit();
//                break;
//            case Constants.ORGANIZATION_RESOURCE:
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.resource_fragment_container, ResourcesFragment.newInstance(resourceValue))
//                        .commit();
//                break;
//            case Constants.OPPORTUNITY_RESOURCE:
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.resource_fragment_container, ResourcesFragment.newInstance(resourceValue, userOrganization))
//                        .commit();
//                break;
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_res_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.manage_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constants.OPPORTUNITY_RESOURCE.equalsIgnoreCase(resourceValue)){
                    startManageActivityWithIdentifiers(ManageVolunteerOpportunityActivity.class, userOrganization, "");
                } else {
                    startManageActivityWithIdentifiers(ManageSingleResourceActivity.class, resourceValue, "");
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
