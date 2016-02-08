package com.ccss.youthvolunteer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.model.Category;
import com.ccss.youthvolunteer.model.UserCategoryPoints;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.CheckNetworkConnection;
import com.ccss.youthvolunteer.util.Constants;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class IntroActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if(!CheckNetworkConnection.isInternetAvailable(this)){
            showToastLong(R.string.internet_connection_unavailable);

            if(VolunteerUser.getCurrentUserInformationFromLocalStore() == null)
            {
                Intent errorIntent = new Intent(this, ErrorMessageActivity.class);
                errorIntent.putExtra(Constants.ERROR_ITEM_KEY, getResources().getString(R.string.internet_connection_unavailable));
                startActivity(errorIntent);
                return;
            }
        }

        //currentUser
        ParseUser currentParseUser = ParseUser.getCurrentUser();
        if(currentParseUser == null){
            startActivity(DispatchActivity.class);
            return;
        }

        final VolunteerUser currentUser = VolunteerUser.getCurrentUserInformation(currentParseUser);
        currentUser.pinInBackground();

        if(!currentUser.isEmailVerified()){
            Intent errorIntent = new Intent(this, ErrorMessageActivity.class);
            String errorMessage = getResources().getText(R.string.verify_email_error_title) + "\n" + "\n"
                    + getResources().getText(R.string.verify_email_error_message) + "\n"
                    + getResources().getText(R.string.verify_email_error_message2) + " " + currentUser.getEmail() + ".\n"
                    + getResources().getText(R.string.verify_email_error_message3) + "\n" + "\n"
                    + getResources().getText(R.string.verify_email_error_message4);
            errorIntent.putExtra(Constants.ERROR_ITEM_KEY, errorMessage);
            startActivity(errorIntent);

            return;
        }

        if (!currentUser.isProfileComplete()) {
            showToastLong(R.string.help_us_know_you);
            startActivity(ProfileActivitySimple.class);
        } else {
            Category.findInBackground(new FindCallback<Category>() {
                @Override
                public void done(List<Category> objects, ParseException e) {
                    if (e == null) {
                        try {
                            Category.pinAll(Constants.CATEGORY_RESOURCE, objects);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });

            //TODO: Load UserCategoryPoints/Hours
            int totalUsers = VolunteerUser.getTotalUserCount();
//            UserCategoryPoints.findAllUsersPointsInBackground();
//            UserCategoryPoints.findAllUsersPointsForMonthYearInBackground()

            //TODO: Load the following items and add to SharedPreferences
            SharedPreferences sharedPref = this.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.ENCOURAGE_KEY, getString(R.string.main_message));
            editor.putString(Constants.GOODNESS_KEY, getString(R.string.main_goodness));
            editor.putString(Constants.POINTSANDRANK_KEY, getString(R.string.main_points_rank));
            editor.putString(Constants.SGSTATS_KEY, getString(R.string.total_sg_users_hours));
            editor.putString(Constants.UPCOMING_KEY, getString(R.string.upcoming_event));
//            editor.putString(getString(R.string.total_sg_users), total_sg_users);
//            editor.putString(getString(R.string.user_points), user_points);
//            editor.putString(getString(R.string.user_hours), user_hours);
//            editor.putString(getString(R.string.user_rank), user_rank);

            editor.apply();

            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.putExtra(Constants.INTENT_SENDER, "intro");
            startActivity(mainIntent);
        }
    }
}
