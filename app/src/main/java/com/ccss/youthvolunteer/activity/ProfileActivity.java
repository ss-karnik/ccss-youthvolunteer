package com.ccss.youthvolunteer.activity;

import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.ProfileSectionsPagerAdapter;
import com.ccss.youthvolunteer.model.Interest;
import com.ccss.youthvolunteer.model.Skill;
import com.ccss.youthvolunteer.model.SpecialUser;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Stack;

public class ProfileActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
   // private TabLayout mTabLayout;
    private View mProfileDetailsForm;
    private View mProgressBar;

    public static String POSITION = "POSITION";
    private static VolunteerUser mVolunteerUser;

    private Stack<Integer> mPageHistory;
    private boolean mSaveToHistory;

    private List<Interest> mSelectedInterests = Lists.newArrayList();
    private List<Skill> mSelectedSkills = Lists.newArrayList();
    private List<SpecialUser> mSpecialUsers = Lists.newArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);
        mSelectedSkills = Lists.newArrayList();
        mSelectedInterests = Lists.newArrayList();
        mProgressBar = findViewById(R.id.profile_progress_bar);
        mProfileDetailsForm = findViewById(R.id.profile_main_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(mSpecialUsers.isEmpty()) {
            SpecialUser.findInBackground(new FindCallback<SpecialUser>() {
                @Override
                public void done(List<SpecialUser> objects, ParseException e) {
                    if (e == null) {
                        mSpecialUsers = objects;
                    }
                }
            });
        }

        mVolunteerUser = VolunteerUser.getCurrentUserInformation(ParseUser.getCurrentUser());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.profile_data_container);
        setupViewPager(mViewPager);

        mViewPager.setOffscreenPageLimit(2);
//        mViewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
        mPageHistory = new Stack<>();

//        mTabLayout = (TabLayout) findViewById(R.id.profile_tabs);
//        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        /*
          The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections.
          We use a {@link FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory.
          If this becomes too memory intensive, switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
        */
        ProfileSectionsPagerAdapter mSectionsPagerAdapter = new ProfileSectionsPagerAdapter(getSupportFragmentManager(), this);
        mSectionsPagerAdapter.addFragment(ProfileInterestsFragment.newInstance(0, mVolunteerUser), getString(R.string.interests_title));
        mSectionsPagerAdapter.addFragment(ProfileSkillsFragment.newInstance(1, mVolunteerUser), getString(R.string.skills_title));
        mSectionsPagerAdapter.addFragment(ProfileDetailsFragment.newInstance(2, mVolunteerUser), getString(R.string.personal_details_title));
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setCurrentItem(0);
    }

    public void onNavigateToInterestsClick(List<Skill> userSelectedSkills){
        if(userSelectedSkills != null) {
            mSelectedSkills = userSelectedSkills;
        }

        mViewPager.setCurrentItem(0);
    }

    public void onNavigateToSkillsClick(List<Interest> userSelectedInterests){
        if(userSelectedInterests != null) {
            mSelectedInterests = userSelectedInterests;
        }

        mViewPager.setCurrentItem(1);
    }

    public void onNavigateToDetailsClick(List<Skill> userSelectedSkills){
        if(userSelectedSkills != null) {
            mSelectedSkills.addAll(userSelectedSkills);
        }

        mViewPager.setCurrentItem(2);
    }

    public void onSubmitDetailsClick(VolunteerUser volunteer){
        final SpecialUser specialUser = Iterables.tryFind(mSpecialUsers, isSpecialUser(volunteer.getEmail())).orNull()  ;
        if (specialUser != null) {
            assignRole(specialUser, volunteer);
            volunteer.setSpecialRole(specialUser.getRole());
            volunteer.setOrganizationName(specialUser.getOrganizationName());
        }
        volunteer.setUserInterests(!mSelectedInterests.isEmpty() ? mSelectedInterests : mVolunteerUser.getUserInterests());
        volunteer.setUserSkills(!mSelectedSkills.isEmpty() ? mSelectedSkills : mVolunteerUser.getUserSkills());

        showProgress(true, mProfileDetailsForm, mProgressBar);
        volunteer.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    showProgress(false, mProfileDetailsForm, mProgressBar);
                    signUpCompleteSuccess();
                } else {
                    showToast(com.parse.ui.R.string.com_parse_ui_signup_failed_unknown_toast);
                }
            }
        });
    }

    public static Predicate<SpecialUser> isSpecialUser(final String emailId) {
        return new Predicate<SpecialUser>() {
            @Override
            public boolean apply(SpecialUser input) {
                return input.getEmailId().equalsIgnoreCase(emailId);
            }
        };
    }

    private void assignRole(final SpecialUser specialUser, VolunteerUser volunteer) {
        final String roleToSaveAs = specialUser.getRole();
        ParseQuery<ParseRole> query = ParseRole.getQuery();
        query.whereEqualTo("name", roleToSaveAs);
        try {
            ParseRole role = query.getFirst();
            role.getUsers().add(volunteer);
            role.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        showToast(getResources().getText(com.parse.ui.R.string.com_parse_ui_admin_made) + roleToSaveAs );
                    }
                }
            });
        } catch (ParseException e) {
            showToast(getResources().getText(com.parse.ui.R.string.com_parse_ui_admin_failed) + roleToSaveAs );
        }
    }

    private void signUpCompleteSuccess() {
        Snackbar snackbar = Snackbar.make(mProfileDetailsForm, R.string.msg_user_update_success, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    //region ViewPager overloads
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(mSaveToHistory)
            mPageHistory.push(position);
       // actionBar.setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //endregion

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt(POSITION, mTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mViewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }

    @Override
    public void onBackPressed() {
        if(mPageHistory.empty())
            super.onBackPressed();
        else {
            mSaveToHistory = false;
            mViewPager.setCurrentItem(mPageHistory.pop());
            mSaveToHistory = true;
        }
    };


    public interface ValidateFragment {
        public boolean validate();
    }

    public interface OnProfileDataPass {
        public void onProfileDataPass(List<String> data);
    }

    public interface OnProfileFragmentSelectionListener {
        public void onFragmentSelection(Uri uri);
        public void onFragmentSelection(String id);
        public void onFragmentSelection(int actionId);
    }
}
