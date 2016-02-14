package com.ccss.youthvolunteer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.model.Announcement;
import com.ccss.youthvolunteer.model.Category;
import com.ccss.youthvolunteer.model.UserCategoryPoints;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.CheckNetworkConnection;
import com.ccss.youthvolunteer.util.Constants;
import com.google.common.base.Joiner;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class IntroActivity extends BaseActivity {
    int mUserPoints, mUserHours, mUserRank, mTotalUsers, mTotalUserHours;
    String mCurrentAnnouncements;

    public static int sum(List<Integer> list, int start) {
        if (start == list.size()) return 0;
        return list.get(start) + sum(list, start + 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

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
            mainIntent.putExtra(Constants.INTERNET_AVAILABLE, "false");
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
            Category.pinAllInBackground(Constants.CATEGORY_RESOURCE, Category.getAllCategories(true));

            //TODO: Load UserCategoryPoints/Hours
            mUserPoints = 10;
            mUserHours = 2;
            mUserRank = 5; //Map<User, Points>?

            mCurrentAnnouncements = Joiner.on("|").join(Announcement.getAllAnnouncementText());
            mTotalUsers = VolunteerUser.getTotalUserCount();
            mTotalUserHours = sum(UserCategoryPoints.findHoursForAllUsers(), 0);

//            final String[] userMonthStats = new String[0];
            UserCategoryPoints.findCurrentUserPointsInBackground(currentParseUser, 0, new FindCallback<UserCategoryPoints>() {
                @Override
                public void done(List<UserCategoryPoints> list, ParseException e) {
                    if (e == null) {
                        UserCategoryPoints.pinAllInBackground("CurrentUserPoints", list);
//                        if (!list.isEmpty()) {
//                            UserCategoryPoints.pinAllInBackground("CurrentUserPoints", list);
////                            userMonthStats[0] = Joiner.on(";").join(Collections2.transform(Collections2.filter(list, new Predicate<UserCategoryPoints>() {
////                                @Override
////                                public boolean apply(UserCategoryPoints input) {
////                                    return input.getActionMonthYear().equals(DateTime.now().toString("MM-yyyy"));
////                                }
////                            }), new Function<UserCategoryPoints, String>() {
////                                @Override
////                                public String apply(UserCategoryPoints userCategoryPoints) {
////                                    return userCategoryPoints.getActionCategory().getCategoryName() + "|"
////                                            + userCategoryPoints.getCategoryHours() + "|"
////                                            + userCategoryPoints.getCategoryPoints();
////                                }
////                            }));
//                        }
                    }
                    startMainActivity();
                }
            });

//                        UserCategoryPoints.findCurrentUsersPointsForMonthYearInBackground(currentParseUser, null, true, new FindCallback<UserCategoryPoints>() {
//                            @Override
//                            public void done(List<UserCategoryPoints> list, ParseException e) {
//                                if (e == null) {
//                                    userMonthStats = Joiner.on(";").join(Lists.transform(list;
//                        userStats[0] = Joiner.on(";").join(Lists.transform(list, new Function<UserCategoryPoints, String>() {
//                            @Override
//                            public String apply(UserCategoryPoints userCategoryPoints) {
//                                return userCategoryPoints.getActionCategory().getCategoryName() + "|"
//                                        + userCategoryPoints.getCategoryHours() + "|"
//                                        + userCategoryPoints.getCategoryPoints() + "|"
//                                        + userCategoryPoints.getActionMonthYear();
//                            }
//                        }));
//                    }
//                    mQueryCount++;
//                }
//            });
//
//            //After all queries complete
//            while (true){
//                if(mQueryCompleted){
//                    break;
//                }
//            }


        }
    }

    private void startMainActivity() {
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.ANNOUNCEMENTS_KEY, mCurrentAnnouncements);
        //Overall with %s points and %s hours you are ranked %s out of %s volunteers!
        editor.putString(Constants.POINTS_RANK_KEY, String.format(getString(R.string.main_points_rank),
                mUserPoints, mUserHours, mUserRank, mTotalUsers));
        //Our %s volunteers have put in %s hours!
        editor.putString(Constants.SG_STATS_KEY, String.format(getString(R.string.total_sg_users_hours), mTotalUsers, mTotalUserHours));
        editor.putString(Constants.UPCOMING_KEY, getString(R.string.upcoming_event));

        editor.apply();

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra(Constants.INTENT_SENDER, "intro");
        mainIntent.putExtra(Constants.INTERNET_AVAILABLE, "true");
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
