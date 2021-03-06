package com.ccss.youthvolunteer.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.ProfileSectionsPagerAdapter;
import com.ccss.youthvolunteer.model.Interest;
import com.ccss.youthvolunteer.model.Post;
import com.ccss.youthvolunteer.model.Skill;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.util.Constants;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Stack;

/**
 * An activity representing a single Opportunity detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link OpportunityListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link OpportunityDetailFragment}.
 */
public class OpportunityDetailActivity extends BaseActivity {

    public static String POSITION = "POSITION";

    private ViewPager mViewPager;
    private View mProgressBar;
    private View mOpportunityDetailsForm;
    private FloatingActionButton mFabExpressInterest;

    private String mUserOrganization;
    private String mResourceObjectId;
    private List<Interest> mSelectedInterests = Lists.newArrayList();
    private List<Skill> mSelectedSkills = Lists.newArrayList();
    private VolunteerOpportunity mOpportunity;
    private Stack<Integer> mPageHistory;
    private boolean mIsInterestedUser;
    private boolean mSaveToHistory;
    private boolean mReadonly;
    private boolean mIsCloneable;

    private OpportunityDetailsFragment mDetailFragment;

    /*
    Intent should have
      OBJECT_ID_KEY  (for determining add/edit)
      USER_ORGANIZATION_KEY (blank for Admin)
      ACCESS_MODE_KEY (read / write)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent manageIntent = getIntent();
        setTitle(getResources().getText(R.string.manage_title) + " Opportunity");

        mResourceObjectId = manageIntent.getStringExtra(Constants.OBJECT_ID_KEY);
        final boolean isNewAddition = Strings.isNullOrEmpty(mResourceObjectId);

        mUserOrganization = manageIntent.getStringExtra(Constants.USER_ORGANIZATION_KEY);
        mReadonly = Constants.READ_MODE.equals(manageIntent.getStringExtra(Constants.ACCESS_MODE_KEY));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.opportunity_detail_toolbar);
        setSupportActionBar(toolbar);

        mOpportunityDetailsForm = findViewById(R.id.opportunity_main_content);
        mSelectedSkills = Lists.newArrayList();
        mSelectedInterests = Lists.newArrayList();
        mProgressBar = findViewById(R.id.opportunity_progress_bar);
        mFabExpressInterest = (FloatingActionButton) findViewById(R.id.fab_express_interest);
        mFabExpressInterest.setVisibility(View.INVISIBLE);
        mFabExpressInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUserInterest();
            }
        });

        // Show the Up button in the action bar.
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(!isNewAddition){
            mProgressBar.setVisibility(View.VISIBLE);
            mOpportunity = VolunteerOpportunity.getOpportunityDetails(mResourceObjectId);
            loadVolunteersForOpportunity();
            if(mReadonly && mOpportunity.isPastActivity()){
                mFabExpressInterest.setVisibility(View.VISIBLE);
                mIsCloneable = true;
                invalidateOptionsMenu();
                mIsInterestedUser = mOpportunity.getInterestedUsers().contains(ParseUser.getCurrentUser());
                if(mIsInterestedUser){
                    mFabExpressInterest.setImageResource(android.R.drawable.star_big_on);
                } else {
                    mFabExpressInterest.setImageResource(android.R.drawable.star_big_off);
                }
            }
        } else {
            mOpportunity = new VolunteerOpportunity();
        }

        mProgressBar.setVisibility(View.INVISIBLE);

        mViewPager = (ViewPager) findViewById(R.id.opportunity_data_container);
        setupViewPager(mViewPager, isNewAddition);

        mViewPager.setOffscreenPageLimit(4);
        mPageHistory = new Stack<>();

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(OpportunityDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(OpportunityDetailFragment.ARG_ITEM_ID));
            OpportunityDetailFragment fragment = new OpportunityDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.opportunity_data_container, fragment)
                    .commit();
        }
    }

    private void setUserInterest() {
        List<ParseUser> interestedUsers = mOpportunity.getInterestedUsers();
        final boolean added;
        if(mIsInterestedUser){
            interestedUsers.remove(ParseUser.getCurrentUser());
            added = false;
        } else {
            interestedUsers.add(ParseUser.getCurrentUser());
            added = true;
        }

        mOpportunity.setInterestedUsers(interestedUsers);
        mOpportunity.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(added){
                    mIsInterestedUser = true;
                    mFabExpressInterest.setImageResource(android.R.drawable.star_big_on);
                } else {
                    mIsInterestedUser = false;
                    mFabExpressInterest.setImageResource(android.R.drawable.star_big_off);
                }
            }
        });
    }

    public void onSubmitDetailsClick(VolunteerOpportunity opportunity){

        opportunity.setRequiredInterests(!mSelectedInterests.isEmpty() ? mSelectedInterests : mOpportunity.getRequiredInterests());
        opportunity.setRequiredSkills(!mSelectedSkills.isEmpty() ? mSelectedSkills : mOpportunity.getRequiredSkills());
        mOpportunity = opportunity;
        showProgress(true, mOpportunityDetailsForm, mProgressBar);
        opportunity.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    showProgress(false, mOpportunityDetailsForm, mProgressBar);
                    mDetailFragment.enableSubmitButton();
                    saveCompleteSuccess();
                } else {
                    showToast(com.parse.ui.R.string.com_parse_ui_signup_failed_unknown_toast);
                }
            }
        });
    }

    private void saveCompleteSuccess() {
        Snackbar snackbar = Snackbar.make(mOpportunityDetailsForm, R.string.msg_data_success, Snackbar.LENGTH_LONG);
        snackbar.show();
        if(Strings.isNullOrEmpty(mResourceObjectId)){
            Post opportunityAddedPost = new Post();
            opportunityAddedPost.setPostText(String.format(getResources().getString(R.string.opportunity_added_post),
                    mOpportunity.getTitle(), mOpportunity.getOrganizationName()));
            opportunityAddedPost.setPostBy(ParseUser.getCurrentUser());
            opportunityAddedPost.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        showToast(R.string.activity_post_success);
                    }
                }
            });
        }
    }

    private void loadVolunteersForOpportunity() {
//        UserAction.findUsersForAction(mOpportunity, new FindCallback<UserAction>() {
//            @Override
//            public void done(List<UserAction> objects, ParseException e) {
//                if(e == null)
//
//            }
//        });
    }

    private void setupViewPager(ViewPager viewPager, boolean isNew) {
        /*
          The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections.
          We use a {@link FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory.
          If this becomes too memory intensive, switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
        */
        ProfileSectionsPagerAdapter pagerAdapter = new ProfileSectionsPagerAdapter(getSupportFragmentManager(), this);
        pagerAdapter.addFragment(OpportunityInterestsFragment.newInstance(0, mOpportunity.getRequiredInterests(), mReadonly), getString(R.string.interests_title));
        pagerAdapter.addFragment(OpportunitySkillsFragment.newInstance(1, mOpportunity.getRequiredSkills(), mReadonly), getString(R.string.skills_title));
        mDetailFragment = OpportunityDetailsFragment.newInstance(2, mOpportunity, mReadonly, mUserOrganization, isNew);
        pagerAdapter.addFragment(mDetailFragment, getString(R.string.personal_details_title));
        if(!isNew) {
            pagerAdapter.addFragment(OpportunityInterestedUsersFragment.newInstance(3, mOpportunity.getInterestedUsers(), mReadonly), getString(R.string.personal_details_title));
            pagerAdapter.addFragment(OpportunityVolunteeredUsersFragment.newInstance(4, mOpportunity.getObjectId()), getString(R.string.personal_details_title));
        }
        viewPager.setAdapter(pagerAdapter);
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

    public void onNavigateToInterestedUsersClick(){
        mViewPager.setCurrentItem(3);
    }

    public void onNavigateToVolunteerUsersClick(){
        mViewPager.setCurrentItem(4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_manage_resource, menu);

        menu.findItem(R.id.clone_opportunity).setVisible(mIsCloneable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clone_opportunity) {
            String cloneObjectId = VolunteerOpportunity.cloneOpportunity(mOpportunity);
            Intent intent = new Intent(this, OpportunityDetailActivity.class);
            intent.putExtra(Constants.OBJECT_ID_KEY, cloneObjectId);
            intent.putExtra(Constants.USER_ORGANIZATION_KEY, mUserOrganization);
            intent.putExtra(Constants.ACCESS_MODE_KEY, Constants.WRITE_MODE);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void copySelectedEmailIds(List<String> emailIds) {
        if(emailIds == null || emailIds.isEmpty()){
            showError(R.string.no_email_selected);
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Email Ids", Joiner.on(";").join(emailIds));
        clipboard.setPrimaryClip(clip);
        showToast(String.format(getString(R.string.emailIds_copied_message), emailIds.size()));
    }

    public void createEmailIntent(final List<String> addresses)
    {
        if(addresses == null || addresses.isEmpty()){
            showError(getString(R.string.no_email_selected));
            return;
        }

        Intent sendTo = new Intent(Intent.ACTION_SENDTO);
        sendTo.setData(Uri.parse("mailto:")); // only email apps should handle this
        sendTo.putExtra(Intent.EXTRA_EMAIL, addresses.toArray(new String[0]));
        sendTo.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.volunteer_interest_subject));
        sendTo.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.volunteer_interest_message), mOpportunity.getTitle()));

        if (sendTo.resolveActivity(getPackageManager()) != null) {
            startActivity(sendTo);
        }
    }
}
