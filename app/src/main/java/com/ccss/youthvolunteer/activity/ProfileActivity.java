package com.ccss.youthvolunteer.activity;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.InterestsExpandableAdapter;
import com.ccss.youthvolunteer.adapter.ProfileSectionsPagerAdapter;
import com.ccss.youthvolunteer.adapter.SkillsExpandableAdapter;
import com.ccss.youthvolunteer.model.Interests;
import com.ccss.youthvolunteer.model.Skills;
import com.ccss.youthvolunteer.model.SpecialUser;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.google.common.collect.Lists;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class ProfileActivity extends BaseActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ProfileSectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static final String USER_FIRST_NAME_FIELD = "firstName";
    private static final String USER_LAST_NAME_FIELD = "lastName";
    private static final String USER_SCHOOL_NAME_FIELD = "schoolName";
    private static final String USER_DOB_FIELD = "dateOfBirth";
    private static final int SELECT_PICTURE = 200;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 20;
    private static final int ON_TOUCH = 1;
    private static final String PROFILE_IMAGE = "profileImage";
    private static final DateTimeFormatter DOB_FORMAT = DateTimeFormat.forPattern("dd/MM/yyyy");

    private EditText mFirstNameField;
    private EditText mLastNameField;
    //private EditText schoolNameField;
    private Spinner mSchoolNameSpinner;
    public static EditText dateOfBirthField;
    private VolunteerUser mVolunteerUser;
    private View mProfileDetailsForm;
    private View mProgressBar;
    private RadioGroup mRadioGenderGroup;
    private ParseImageView mProfileImageButton;
    private CheckBox mFbPublishPermission;
    private CropImageView mCropImageView;
    private RecyclerView mSkillsView;
    private RecyclerView mInterestsView;
    private TextView mOrganizationField;
    private TextView mMobileNumberField;
    private SkillsExpandableAdapter mSkillsAdapter;
    private InterestsExpandableAdapter mInterestsAdapter;
    private List<Interests> mInterests = Lists.newArrayList();
    private List<Skills> mSkills = Lists.newArrayList();
    private List<SpecialUser> mSpecialUsers = Lists.newArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.profile_data_container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.profile_tabs);
        tabLayout.setupWithViewPager(mViewPager);


        mProgressBar = findViewById(R.id.profile_details_progress);
        mProfileDetailsForm = findViewById(R.id.profile_details_form);
        mSchoolNameSpinner = (Spinner) findViewById(R.id.school_name);
        mProfileImageButton = (ParseImageView) findViewById(R.id.profile_image);
        mFirstNameField = (EditText) findViewById(R.id.signup_firstname_input);
        mLastNameField = (EditText) findViewById(R.id.signup_lastname_input);
        mMobileNumberField = (EditText) findViewById(R.id.signup_mobile_input);
        dateOfBirthField = (EditText) findViewById(R.id.signup_dateofbirth_input);
        mFbPublishPermission = (CheckBox) findViewById(R.id.checkbox_fbPublish);
        mRadioGenderGroup = (RadioGroup) findViewById(R.id.radioGroupGender);
        mCropImageView = (CropImageView) findViewById(R.id.cropImageView);
        mSkillsView = (RecyclerView) findViewById(R.id.skills_view);
        mInterestsView = (RecyclerView) findViewById(R.id.interests_view);
        mOrganizationField = (TextView) findViewById(R.id.user_organization);

        mVolunteerUser = VolunteerUser.getCurrentUserInformation(ParseUser.getCurrentUser());
        mProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), SELECT_PICTURE);
            }
        });

        loadStaticData();

        setupSchoolSpinner();
        setupSkillsView();
        setupInterestsView();

        ImageButton btnChangeDate = (ImageButton) findViewById(R.id.cal_button);
        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new ProfileActivitySimple.DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        TextView userNameField = (TextView) findViewById(R.id.signup_user_email);
        userNameField.setText(mVolunteerUser.getEmail());

        if(mVolunteerUser.isProfileComplete()){
            loadPredefinedUserValues();
        }
    }

    private void loadPredefinedUserValues() {

    }

    private void setupInterestsView() {

    }

    private void setupSchoolSpinner() {

    }

    private void setupSkillsView() {

    }

    private void loadStaticData() {

    }

    private void setupViewPager(ViewPager viewPager) {
        ProfileSectionsPagerAdapter adapter = new ProfileSectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new InterestsFragment(), getString(R.string.interests_title).toUpperCase());
        adapter.addFragment(new SkillsFragment(), getString(R.string.skills_title).toUpperCase());
        adapter.addFragment(new DetailsFragment(), getString(R.string.personal_details_title).toUpperCase());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * Interests fragment containing a recycler view.
     */
    public static class InterestsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static InterestsFragment newInstance(int sectionNumber) {
            InterestsFragment fragment = new InterestsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public InterestsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.profile_interests_fragment, container, false);

            return rootView;
        }
    }

    /**
     * Skills fragment containing a recycler view.
     */
    public static class SkillsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SkillsFragment newInstance(int sectionNumber) {
            SkillsFragment fragment = new SkillsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public SkillsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.profile_skills_fragment, container, false);

            return rootView;
        }
    }

    /**
     * Details fragment containing a simple view.
     */
    public static class DetailsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DetailsFragment newInstance(int sectionNumber) {
            DetailsFragment fragment = new DetailsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public DetailsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.profile_details_fragment, container, false);

            return rootView;
        }
    }
}
