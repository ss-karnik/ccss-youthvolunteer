package com.ccss.youthvolunteer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.model.Category;
import com.ccss.youthvolunteer.model.UserAction;
import com.ccss.youthvolunteer.util.Constants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

import java.util.List;

public class VolunteerOpportunityActivity extends BaseActivity {

    private Spinner mCategoryList;
    private Button mBtnVolunteer;
    private List<String> mCategoryActions;
    private Category mSelectedCategory;
    private VolunteerOpportunity mSelectedVolunteerOpportunity;
    private ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_opportunity);

        mCurrentUser = ParseUser.getCurrentUser();

        mBtnVolunteer = (Button) findViewById(R.id.btnAdd);
        mBtnVolunteer.setEnabled(false);

        mCategoryList = (Spinner) findViewById(R.id.category_list);
        configureCategoryList();

        addListenerOnButton();
    }

    public void configureCategoryList() {
        final List<String> categories = Category.getAllCategoryNames();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategoryList.setAdapter(dataAdapter);

        mCategoryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //TODO: Select category
                //mSelectedCategory = categories.get(arg2);
                mCategoryActions = VolunteerOpportunity.getOpportunitiesForCategory(mSelectedCategory, true);
                populateCategoryActions();
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    public void populateCategoryActions() {
        Spinner mCategoryActionList = (Spinner) findViewById(R.id.category_list);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mCategoryActions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategoryActionList.setAdapter(dataAdapter);

        mCategoryActionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                try {
                    mSelectedVolunteerOpportunity = VolunteerOpportunity.getOpportunityByNameAndCategory(mCategoryActions.get(arg2), mSelectedCategory);
                } catch (ParseException e) {
                    mSelectedVolunteerOpportunity = new VolunteerOpportunity();
                }
                populateActionDetails();
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    public void populateActionDetails() {

        TextView actionDescription = (TextView)findViewById(R.id.txtDescription);
        actionDescription.setText(mSelectedVolunteerOpportunity.getDescription());

        TextView actionPoints = (TextView)findViewById(R.id.txtPoints);
        actionPoints.setText(mSelectedVolunteerOpportunity.getActionPoints());

        if(latestUserVolunteerActivityBeforeCoolingPeriod()){
            mBtnVolunteer.setEnabled(true);
        }
    }

    private boolean latestUserVolunteerActivityBeforeCoolingPeriod() {
        final LocalDateTime[] latestUserAction = new LocalDateTime[1];
        UserAction.findLatestUserActionInBackground(mCurrentUser, new GetCallback<UserAction>() {
            @Override
            public void done(UserAction object, ParseException e) {
                if (object != null) {
                    latestUserAction[0] = new LocalDateTime(object.getActionDate());
                } else {
                    latestUserAction[0] = new LocalDateTime().minusMinutes(Constants.COOLING_PERIOD_BETWEEN_ACTIONS_IN_MINS + 10);
                }
            }
        });

        return Minutes.minutesBetween(latestUserAction[0], LocalDateTime.now()).getMinutes()
                >= Constants.COOLING_PERIOD_BETWEEN_ACTIONS_IN_MINS;
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {
        mBtnVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertUserAction();

                Uri uri = Uri.parse(mSelectedVolunteerOpportunity.getActionLink()); // missing 'http://' will cause crashed
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(launchBrowser);
            }
        });
    }

    private void insertUserAction() {
        UserAction thisAction = new UserAction();
        thisAction.setAction(mSelectedVolunteerOpportunity.getObjectId());
        thisAction.setActionBy(mCurrentUser);
        thisAction.setActionDate(LocalDateTime.now().toDate());
        thisAction.setPointsAllocated(false);
        thisAction.saveInBackground();
    }
}