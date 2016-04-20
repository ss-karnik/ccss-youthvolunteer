package com.ccss.youthvolunteer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.model.Announcement;
import com.ccss.youthvolunteer.model.Category;
import com.ccss.youthvolunteer.model.UserCategoryPoints;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.CheckNetworkConnection;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DateUtils;
import com.ccss.youthvolunteer.util.CommonUtils;
import com.google.common.base.Joiner;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.joda.time.LocalDate;

import java.util.List;

public class IntroActivity extends BaseActivity {
    int mUserPoints, mUserHours, mUserRank, mTotalUsers;
    String mTotalUserHours;
    String mCurrentAnnouncements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        loadPageContent();
    }

    private void loadPageContent() {

        if (!CheckNetworkConnection.isInternetAvailable(this)) {
            showToastLong(R.string.internet_connection_unavailable);

            VolunteerUser userFromDataStore = VolunteerUser.getCurrentUserInformationFromLocalStore();
            if (userFromDataStore == null) {
                Intent errorIntent = new Intent(this, ErrorMessageActivity.class);
                errorIntent.putExtra(Constants.ERROR_ITEM_KEY, getResources().getString(R.string.internet_connection_unavailable));
                startActivity(errorIntent);
                return;
            }
            //User available from local datastore
            if (!userFromDataStore.isEmailVerified()) {
                displayError(userFromDataStore.getEmail());

                return;
            }

            if (!userFromDataStore.isProfileComplete()) {
                Intent errorIntent = new Intent(this, ErrorMessageActivity.class);
                String errorMessage = getResources().getString(R.string.incomplete_profile_no_internet);
                errorIntent.putExtra(Constants.ERROR_ITEM_KEY, errorMessage);
                startActivity(errorIntent);

                return;
            }

            //Start Main Activity
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.putExtra(Constants.INTENT_SENDER, "intro");
            mainIntent.putExtra(Constants.INTERNET_AVAILABLE, false);
            startActivity(mainIntent);
        }

        //currentUser
        final ParseUser currentParseUser = ParseUser.getCurrentUser();
        if (currentParseUser == null) {
            startActivity(DispatchActivity.class);
            return;
        }

        final VolunteerUser currentUser = VolunteerUser.getCurrentUserInformation(currentParseUser);
        currentUser.pinInBackground();

        if (!currentUser.isEmailVerified()) {
            displayError(currentUser.getEmail());

            return;
        }

        if (!currentUser.isProfileComplete()) {
            showToastLong(R.string.help_us_know_you);
            startActivity(ProfileActivitySimple.class);
        } else {

            try {
                UserCategoryPoints.unpinAll(Constants.CURRENT_USER_POINTS);
                Category.unpinAll(Constants.CATEGORY_RESOURCE);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Category.pinAllInBackground(Constants.CATEGORY_RESOURCE, Category.getAllCategories(true));

            mUserPoints = 0;
            mUserHours = 0;
            mUserRank = 5;

            mCurrentAnnouncements = Joiner.on("|").join(Announcement.getAllAnnouncementText());
            mTotalUsers = VolunteerUser.getTotalUserCount();
            mTotalUserHours = DateUtils.minutesToHoursRepresentation(CommonUtils.sum(UserCategoryPoints.findMinutesForAllUsers(), 0));

            try {
                List<VolunteerUser> rankedUsers = VolunteerUser.findUsersRanked();
                mUserRank = rankedUsers.indexOf(currentUser);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            UserCategoryPoints.findCurrentUserPointsInBackground(currentParseUser, 0, new FindCallback<UserCategoryPoints>() {
                @Override
                public void done(List<UserCategoryPoints> list, ParseException e) {
                    if (e == null) {
                        UserCategoryPoints.pinAllInBackground(Constants.CURRENT_USER_POINTS, list);
                        for (UserCategoryPoints item: list) {
                            mUserPoints += item.getCategoryPoints();
                            mUserHours += item.getCategoryMinutes();
                        }
                    } else {
                        e.getMessage();
                    }

                    startMainActivity();
                }
            });
        }
    }

    private void startMainActivity() {
        VolunteerOpportunity upcomingActivity = VolunteerOpportunity.getUpcomingOpportunity();
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.ANNOUNCEMENTS_KEY, mCurrentAnnouncements);
        //Overall with %s points and %s hours you are ranked %s out of %s volunteers!
        editor.putString(Constants.POINTS_RANK_KEY, String.format(getString(R.string.main_points_rank),
                mUserPoints, mUserHours, mUserRank, mTotalUsers));
        //Our %s volunteers have put in %s hours!
        editor.putString(Constants.SG_STATS_KEY, String.format(getString(R.string.total_sg_users_hours), mTotalUsers, mTotalUserHours));
        editor.putString(Constants.UPCOMING_KEY, String.format(getString(R.string.upcoming_event), upcomingActivity.getTitle()));
        editor.putString(Constants.STATS_LAST_UPDATE_DATE, DateUtils.formattedDateString(new LocalDate().toDate()).toString());
        editor.apply();

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra(Constants.INTENT_SENDER, "intro");
        mainIntent.putExtra(Constants.INTERNET_AVAILABLE, true);
        startActivity(mainIntent);
    }

    private void displayError(String userEmail) {
        Intent errorIntent = new Intent(this, ErrorMessageActivity.class);
        String errorMessage = getResources().getText(R.string.verify_email_error_title) + "\n" + "\n"
                + getResources().getText(R.string.verify_email_error_message) + "\n"
                + getResources().getText(R.string.verify_email_error_message2) + " " + userEmail + ".\n"
                + getResources().getText(R.string.verify_email_error_message3) + "\n" + "\n"
                + getResources().getText(R.string.verify_email_error_message4);
        errorIntent.putExtra(Constants.ERROR_ITEM_KEY, errorMessage);
        startActivity(errorIntent);
    }
}
