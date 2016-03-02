package com.ccss.youthvolunteer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectableResourceListAdapter;
import com.ccss.youthvolunteer.adapter.SelectorHintAdapter;
import com.ccss.youthvolunteer.model.Announcement;
import com.ccss.youthvolunteer.model.Category;
import com.ccss.youthvolunteer.model.Interest;
import com.ccss.youthvolunteer.model.InterestGroup;
import com.ccss.youthvolunteer.model.Organization;
import com.ccss.youthvolunteer.model.Post;
import com.ccss.youthvolunteer.model.Recognition;
import com.ccss.youthvolunteer.model.RecognitionType;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.model.School;
import com.ccss.youthvolunteer.model.Skill;
import com.ccss.youthvolunteer.model.SpecialUser;
import com.ccss.youthvolunteer.model.UserRecognition;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DividerItemDecoration;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ManageSingleResourceActivity extends BaseActivity {

    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 20;
    private static final int ON_TOUCH = 1;
    private static final String PROFILE_IMAGE = "profileLogo";

    private ParseObject mResourceObject;
    private View mSaveResourceForm;
    private ProgressBar mProgressBar;
    private EditText mTitleText;
    private EditText mDescriptionText;
    private CheckBox mActiveStatus;
    private String mResourceType;
    private String mUserOrganization;
    private TextView mColorHexRep;
    private EditText mColorHexText;
    private CropImageView mCropImageView;
    private ParseImageView mLogoImageButton;
    private Spinner mRecognitionActivitySpinner;

    private RelativeLayout mUserMgmtContainer;
    private Spinner mUserOrgSpinner;
    private Spinner mUserRoleSpinner;
    private RelativeLayout mRecognitionMgmtContainer;
    private Spinner mRecognitionTypeSpinner;
    private CheckBox mRecognitionUnit;
    private EditText mRecogReqPtsText;
    private EditText mRecogMaxUnitsText;
    private RecyclerView mUsersRecyclerView;
    private Button mSubmitButton;
    private TextView mEmptyUserList;
    private ImageView mExpandCollapseUsers;
    private RelativeLayout mUserListContainer;
    private TextView mUserListHeaderText;

    private List<ResourceModel> mResourceList = Lists.newArrayList();
    private SelectableResourceListAdapter mUserResourceAdapter;

    private String mResourceObjectId;

//    private Category mCategory;
//    private Interest mInterest;
//    private Skill mSkill;
//    private School mSchool;
//    private SpecialUser mSplUser;
//    private Organization mOrg;
//    private InterestGroup mGroup;
//    private VolunteerOpportunity mOpportunity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent manageIntent = getIntent();
        mResourceType = manageIntent.getStringExtra(Constants.MANAGE_ITEM_KEY);
        mResourceObjectId = manageIntent.getStringExtra(Constants.OBJECT_ID_KEY);
        mUserOrganization = manageIntent.getStringExtra(Constants.USER_ORGANIZATION_KEY);

        if(mResourceType.equals(Constants.OPPORTUNITY_RESOURCE)){
            Intent intent = new Intent(this, OpportunityDetailActivity.class);
            intent.putExtra(Constants.OBJECT_ID_KEY, mResourceObjectId);
            intent.putExtra(Constants.USER_ORGANIZATION_KEY, mUserOrganization);
            intent.putExtra(Constants.ACCESS_MODE_KEY, Constants.WRITE_MODE);
            startActivityForResult(intent, Constants.ADD_RESOURCE_REQUEST_CODE);
            //return;
        }

        setTitle(getResources().getText(R.string.manage_title) + " " + mResourceType);
        final boolean isNewAddition = Strings.isNullOrEmpty(mResourceObjectId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_single_resource_activity);

        ImageButton colorPicker = (ImageButton) findViewById(R.id.manage_category_color_picker_button);
        RelativeLayout categoryContainer = (RelativeLayout) findViewById(R.id.manage_color_picker);

        initalizeControls();

        //Set control visibility based on the selection.
        mProgressBar.setVisibility(View.VISIBLE);

        //region ResourceType switch-case
        switch (mResourceType){
            case Constants.ANNOUNCEMENT_RESOURCE:

                mTitleText.setVisibility(View.INVISIBLE);
                ParseQuery<Announcement> announcementQuery = ParseQuery.getQuery(Announcement.class);
                announcementQuery.whereEqualTo(Constants.OBJECT_ID_KEY, mResourceObjectId);
                announcementQuery.getFirstInBackground(new GetCallback<Announcement>() {
                    @Override
                    public void done(Announcement object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Announcement();
                        }

                        if(!isNewAddition) {
                            mDescriptionText.setText(((Announcement) mResourceObject).getAnnouncementText());
                            mActiveStatus.setChecked(((Announcement) mResourceObject).isActive());
                        }

                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                break;

            case Constants.CATEGORY_RESOURCE:
                categoryContainer.setVisibility(View.VISIBLE);
                final int[] currentColor = new int[1];

                ParseQuery<Category> categoryQuery = ParseQuery.getQuery(Category.class);
                categoryQuery.whereEqualTo(Constants.OBJECT_ID_KEY, mResourceObjectId);
                categoryQuery.getFirstInBackground(new GetCallback<Category>() {
                    @Override
                    public void done(Category object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Category();
                        }

                        if(!isNewAddition){
                            mTitleText.setText(((Category) mResourceObject).getCategoryName());
                            mDescriptionText.setText(((Category) mResourceObject).getDescription());
                            mActiveStatus.setChecked(((Category) mResourceObject).isActive());

                            String categoryColorHex = ((Category) mResourceObject).getCategoryColor();
                            mColorHexText.setText(categoryColorHex);
                            mColorHexRep.setBackgroundColor(Color.parseColor(categoryColorHex));
                            currentColor[0] = Color.parseColor(categoryColorHex);
                        }

                        mProgressBar.setVisibility(View.GONE);
                    }
                });

                final AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentColor[0], new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.
                        mColorHexText.setText(String.format("#%06X", (0xFFFFFF & color)));
                        mColorHexRep.setBackgroundColor(color);
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                colorPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.show();
                    }
                });
                break;

            case Constants.RECOGNITION_RESOURCE:
                mLogoImageButton.setVisibility(View.VISIBLE);
                mCropImageView.setVisibility(View.VISIBLE);
                mRecognitionMgmtContainer.setVisibility(View.VISIBLE);
                initializeRecognitionTypeSpinner();
                initializeRecognitionOpportunitySpinner();

                ParseQuery<Recognition> recognitionQuery = ParseQuery.getQuery(Recognition.class);
                recognitionQuery.whereEqualTo(Constants.OBJECT_ID_KEY, mResourceObjectId);
                recognitionQuery.getFirstInBackground(new GetCallback<Recognition>() {
                    @Override
                    public void done(Recognition object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Recognition();
                        }

                        if (!isNewAddition) {
                            mTitleText.setText(((Recognition) mResourceObject).getTitle());
                            mDescriptionText.setText(((Recognition) mResourceObject).getDescription());
                            mRecogReqPtsText.setText(((Recognition) mResourceObject).getPointsRequired());
                            mRecogMaxUnitsText.setText(((Recognition) mResourceObject).getMaxUnits());
                            mRecognitionUnit.setChecked(((Recognition) mResourceObject).isHoursBased());
                            mUserListContainer.setVisibility(View.VISIBLE);
                            loadRecognizedUsers();

                            for(int i = 1; i <= mRecognitionTypeSpinner.getCount(); i++){
                                if(mRecognitionTypeSpinner.getItemAtPosition(i).toString().equals(((Recognition) mResourceObject).getRecognitionType())){
                                    mRecognitionTypeSpinner.setSelection(i);
                                    break;
                                }
                            }

                            mActiveStatus.setChecked(((Recognition) mResourceObject).isActive());
                            setLogoImage(((Recognition) mResourceObject).getImage());
                        }

                        mProgressBar.setVisibility(View.GONE);
                    }
                });

                break;

            case Constants.INTEREST_RESOURCE:
                mLogoImageButton.setVisibility(View.VISIBLE);
                mCropImageView.setVisibility(View.VISIBLE);

                ParseQuery<Interest> interestQuery = ParseQuery.getQuery(Interest.class);
                interestQuery.whereEqualTo(Constants.OBJECT_ID_KEY, mResourceObjectId);
                interestQuery.getFirstInBackground(new GetCallback<Interest>() {
                    @Override
                    public void done(Interest object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Interest();
                        }
                        if(!isNewAddition) {
                            mTitleText.setText(((Interest) mResourceObject).getInterestTitle());
                            mDescriptionText.setText(((Interest) mResourceObject).getDescription());
                            mActiveStatus.setChecked(((Interest) mResourceObject).isActive());
                            mUserListContainer.setVisibility(View.VISIBLE);
                            loadUsersWithInterest();
                            setLogoImage(((Interest) mResourceObject).getLogo());
                        }

                        mProgressBar.setVisibility(View.GONE);
                    }
                });

                break;

            case Constants.SKILL_RESOURCE:
                mLogoImageButton.setVisibility(View.VISIBLE);
                mCropImageView.setVisibility(View.VISIBLE);

                ParseQuery<Skill> skillsQuery = ParseQuery.getQuery(Skill.class);
                skillsQuery.whereEqualTo(Constants.OBJECT_ID_KEY, mResourceObjectId);
                skillsQuery.getFirstInBackground(new GetCallback<Skill>() {
                    @Override
                    public void done(Skill object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Skill();
                        }

                        if(!isNewAddition) {
                            mTitleText.setText(((Skill) mResourceObject).getSkillTitle());
                            mDescriptionText.setText(((Skill) mResourceObject).getDescription());
                            mActiveStatus.setChecked(((Skill) mResourceObject).isActive());
                            mUserListContainer.setVisibility(View.VISIBLE);
                            loadUsersWithSkills();
                            setLogoImage(((Skill) mResourceObject).getLogo());
                        }

                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                break;

            case Constants.USER_RESOURCE:
                mUserMgmtContainer.setVisibility(View.VISIBLE);
                initializeUserTypeSpinner();
                initializeOrganizationSpinner(mUserOrgSpinner);

                ParseQuery<SpecialUser> splUserQuery = ParseQuery.getQuery(SpecialUser.class);
                splUserQuery.whereEqualTo(Constants.OBJECT_ID_KEY, mResourceObjectId);
                splUserQuery.getFirstInBackground(new GetCallback<SpecialUser>() {
                    @Override
                    public void done(SpecialUser object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new SpecialUser();
                        }

                        if (!isNewAddition) {
                            mTitleText.setText(((SpecialUser) mResourceObject).getEmailId());
                            mDescriptionText.setText(((SpecialUser) mResourceObject).getDescription());
                            mActiveStatus.setChecked(((SpecialUser) mResourceObject).isActive());

                            for(int i = 1; i <= mUserOrgSpinner.getCount(); i++){
                                if(mUserOrgSpinner.getItemAtPosition(i).toString().equals(((SpecialUser) mResourceObject).getOrganizationName())){
                                    mUserOrgSpinner.setSelection(i);
                                    break;
                                }
                            }

                            for(int i = 1; i <= mUserRoleSpinner.getCount(); i++){
                                if(mUserRoleSpinner.getItemAtPosition(i).toString().equals(((SpecialUser) mResourceObject).getRole())){
                                    mUserRoleSpinner.setSelection(i);
                                    break;
                                }
                            }
                        }

                        mProgressBar.setVisibility(View.GONE);
                    }
                });

                mProgressBar.setVisibility(View.GONE);
                break;

            case Constants.SCHOOL_RESOURCE:
                mLogoImageButton.setVisibility(View.VISIBLE);
                mCropImageView.setVisibility(View.VISIBLE);

                ParseQuery<School> schoolQuery = ParseQuery.getQuery(School.class);
                schoolQuery.whereEqualTo(Constants.OBJECT_ID_KEY, mResourceObjectId);
                schoolQuery.getFirstInBackground(new GetCallback<School>() {
                    @Override
                    public void done(School object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new School();
                        }

                        if(!isNewAddition) {
                            String schoolName = ((School) mResourceObject).getSchoolName();
                            mTitleText.setText(schoolName);
                            mDescriptionText.setText(((School) mResourceObject).getDescription());
                            mActiveStatus.setChecked(((School) mResourceObject).isActive());
                            mUserListContainer.setVisibility(View.VISIBLE);
                            loadSchoolUsers(schoolName);
                            setLogoImage(((School) mResourceObject).getLogo());
                        }

                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                break;

            case Constants.GROUP_RESOURCE:

                mProgressBar.setVisibility(View.GONE);
                break;

            case Constants.ORGANIZATION_RESOURCE:
                mLogoImageButton.setVisibility(View.VISIBLE);
                mCropImageView.setVisibility(View.VISIBLE);

                ParseQuery<Organization> orgQuery = ParseQuery.getQuery(Organization.class);
                orgQuery.whereEqualTo(Constants.OBJECT_ID_KEY, mResourceObjectId);
                orgQuery.getFirstInBackground(new GetCallback<Organization>() {
                    @Override
                    public void done(Organization object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Organization();
                        }

                        if (!isNewAddition) {
                            String orgName = ((Organization) mResourceObject).getOrganizationName();
                            mTitleText.setText(orgName);
                            mDescriptionText.setText(((Organization) mResourceObject).getDescription());
                            mActiveStatus.setChecked(((Organization) mResourceObject).isActive());
                            mUserListContainer.setVisibility(View.VISIBLE);
                            //loadOrganizationUsers(orgName);
                            loadOrganizationEvents(orgName);
                            setLogoImage(((Organization) mResourceObject).getLogo());
                        }

                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                break;
//
//            case Constants.OPPORTUNITY_RESOURCE:
//                mActivityMgmtContainer.setVisibility(View.VISIBLE);
//                initializeOrganizationSpinner(mActivityOrgSpinner);
//                initializeCategorySpinner();
//
//                mResourceObject = VolunteerOpportunity.getOpportunityDetails(mResourceObjectId);
//
//
//
//                mProgressBar.setVisibility(View.GONE);
//
//                break;
        }
        //endregion
    }

    private void initalizeControls() {

        mProgressBar = (ProgressBar) findViewById(R.id.manage_res_progress);
        mSaveResourceForm = findViewById(R.id.save_resource_form);
        mTitleText = (EditText) findViewById(R.id.manage_res_title);
        mDescriptionText = (EditText) findViewById(R.id.manage_res_description);
        mActiveStatus = (CheckBox) findViewById(R.id.manage_active);
        mSubmitButton = (Button) findViewById(R.id.manage_res_submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick(v);
            }
        });

        //Category controls
        mColorHexText = (EditText) findViewById(R.id.manage_category_indicator);
        mColorHexRep = (TextView) findViewById(R.id.manage_category_color_indicator);
        //Interest, School, Org logo
        mCropImageView = (CropImageView) findViewById(R.id.res_cropImageView);
        mLogoImageButton = (ParseImageView) findViewById(R.id.resource_image);
        mLogoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), Constants.IMAGE_PICKER_REQUEST_CODE);
            }
        });

        //User management controls
        mUserMgmtContainer = (RelativeLayout) findViewById(R.id.user_mgmt_section);
        mUserOrgSpinner = (Spinner) findViewById(R.id.manage_user_org);
        mUserRoleSpinner = (Spinner) findViewById(R.id.manage_user_role);

        mRecognitionMgmtContainer = (RelativeLayout) findViewById(R.id.manage_recognition_container);
        mRecognitionTypeSpinner = (Spinner) findViewById(R.id.manage_recog_type);
        mRecognitionUnit = (CheckBox) findViewById(R.id.manage_recog_pts_category);
        mRecogReqPtsText = (EditText) findViewById(R.id.manage_recog_pts_req);
        mRecogMaxUnitsText = (EditText) findViewById(R.id.manage_recog_max_units);
        mRecognitionActivitySpinner = (Spinner) findViewById(R.id.manage_recog_activity);

        mExpandCollapseUsers = (ImageView) findViewById(R.id.expand_users);
        mUserListContainer = (RelativeLayout) findViewById(R.id.user_list_container);
        mUsersRecyclerView = (RecyclerView) findViewById(R.id.user_list);
        mEmptyUserList = (TextView) findViewById(R.id.empty_user_list);
        mUserListHeaderText = (TextView) findViewById(R.id.user_list_item_header);
    }

    private void setLogoImage(ParseFile logoImage) {
        if(logoImage != null){
            mLogoImageButton.setParseFile(logoImage);
            mLogoImageButton.loadInBackground();
        } else {
            String title = mTitleText.getText().toString();
            String firstChar = title.isEmpty() ? "N" : title.substring(0, 1).toUpperCase();
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int alphabetColor = generator.getColor(firstChar);
            final TextDrawable drawable = TextDrawable.builder().buildRound(firstChar, alphabetColor);
            mLogoImageButton.setImageDrawable(drawable);
        }
    }

    private void loadOrganizationEvents(String orgName) {
        initializeUserRecyclerView();
        VolunteerOpportunity.getOpportunitiesForOrganization(orgName, false, new FindCallback<VolunteerOpportunity>() {
            @Override
            public void done(List<VolunteerOpportunity> objects, ParseException e) {
                populateEventList(objects);
            }
        });
    }

    private void populateEventList(List<VolunteerOpportunity> objects){
        if(objects.isEmpty()){
            mEmptyUserList.setText(R.string.no_opportunities_available);
            mEmptyUserList.setVisibility(View.VISIBLE);
            mUsersRecyclerView.setVisibility(View.GONE);
        } else {
            mUserListHeaderText.setText(R.string.nav_opportunity);
            for (VolunteerOpportunity opportunity : objects) {
                mResourceList.add(opportunity.convertToResourceModel());
            }
        }
        mUserResourceAdapter.notifyDataSetChanged();
    }

    private void initializeUserRecyclerView() {
        mUserResourceAdapter = new SelectableResourceListAdapter(mResourceList);

        mUserListContainer.setVisibility(View.VISIBLE);
        mExpandCollapseUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUsersRecyclerView.getVisibility() == View.VISIBLE){
                    mUsersRecyclerView.setVisibility(View.GONE);
                    mExpandCollapseUsers.setImageResource(R.drawable.nav_next);
                } else {
                    mUsersRecyclerView.setVisibility(View.VISIBLE);
                    mExpandCollapseUsers.setImageResource(R.drawable.arrow_down);
                }
            }
        });

        mUsersRecyclerView.setVisibility(View.VISIBLE);
        mUsersRecyclerView.setHasFixedSize(true);
        mUsersRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mUsersRecyclerView.setAdapter(mUserResourceAdapter);
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void populateUserList(List<VolunteerUser> objects){
        if(objects.isEmpty()){
            showEmptyMessage();
        } else {
            for (VolunteerUser user : objects) {
                mResourceList.add(user.convertToResourceModel());
            }
        }
        mUserResourceAdapter.notifyDataSetChanged();
    }

    private void showEmptyMessage() {
        mEmptyUserList.setVisibility(View.VISIBLE);
        mUsersRecyclerView.setVisibility(View.GONE);
    }

    private void loadUsersWithInterest() {
        initializeUserRecyclerView();
        VolunteerUser.findUsersWithInterest(ParseObject.createWithoutData(Interest.class, mResourceObjectId), new FindCallback<VolunteerUser>() {
            @Override
            public void done(List<VolunteerUser> objects, ParseException e) {
                populateUserList(objects);
            }
        });
    }

    private void loadUsersWithSkills() {
        initializeUserRecyclerView();
        VolunteerUser.findUsersWithSkill(ParseObject.createWithoutData(Skill.class, mResourceObjectId), new FindCallback<VolunteerUser>() {
            @Override
            public void done(List<VolunteerUser> objects, ParseException e) {
                populateUserList(objects);
            }
        });
    }

    private void loadSchoolUsers(String schoolName) {
        initializeUserRecyclerView();
        VolunteerUser.findUsersFromSchool(schoolName, new FindCallback<VolunteerUser>() {
            @Override
            public void done(List<VolunteerUser> objects, ParseException e) {
                populateUserList(objects);
            }
        });
    }

    private void loadOrganizationUsers(String orgName) {
        initializeUserRecyclerView();
        VolunteerUser.findUsersFromOrganization(orgName, new FindCallback<VolunteerUser>() {
            @Override
            public void done(List<VolunteerUser> objects, ParseException e) {
                populateUserList(objects);
            }
        });
    }

    private void loadUsersForOpportunity(List<VolunteerUser> interestedVolunteers) {
        initializeUserRecyclerView();
        if(interestedVolunteers.isEmpty()){
            mEmptyUserList.setVisibility(View.VISIBLE);
        }else {
            for (VolunteerUser user : interestedVolunteers) {
                mResourceList.add(user.convertToResourceModel());
            }
        }
        mUserResourceAdapter.notifyDataSetChanged();
    }

//    private void LoadUsersInterestedInOpportunity() {
//        initializeUserRecyclerView();
//        VolunteerOpportunity.getInterestedVolunteers(mResourceObjectId);
//    }

    private void loadRecognizedUsers() {
        initializeUserRecyclerView();
        UserRecognition.findUsersWithRecognition(ParseObject.createWithoutData(Recognition.class, mResourceObjectId), new FindCallback<UserRecognition>() {
            @Override
            public void done(List<UserRecognition> objects, ParseException e) {
                if (objects.isEmpty()) {
                    showEmptyMessage();
                } else {
                    for (UserRecognition recognizedUsers : objects) {
                        mResourceList.add(VolunteerUser.convertToResourceModel(recognizedUsers.getAchievedBy()));
                    }
                }
                mUserResourceAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initializeOrganizationSpinner(Spinner control) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Strings.isNullOrEmpty(mUserOrganization)
                        ? Organization.getAllActiveOrganizationNames()
                        : Lists.newArrayList(mUserOrganization));

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        control.setPromptId(R.string.org_input_hint);
        control.setAdapter(new SelectorHintAdapter(dataAdapter, R.layout.org_selector_hint, this));
    }

    private void initializeUserTypeSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Lists.newArrayList("Admin", "Moderator", "Organizer"));

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUserRoleSpinner.setPromptId(R.string.user_role_hint);
        mUserRoleSpinner.setAdapter(
                new SelectorHintAdapter(dataAdapter, R.layout.userrole_selector_hint, this));
    }

    private void initializeRecognitionTypeSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, RecognitionType.toArrayList());

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRecognitionTypeSpinner.setPromptId(R.string.recog_type_hint);
        mRecognitionTypeSpinner.setAdapter(
                new SelectorHintAdapter(dataAdapter, R.layout.recog_selector_hint, this));
    }

    private void initializeRecognitionOpportunitySpinner() {
        final List<VolunteerOpportunity> availableOpportunities = Lists.newArrayList();
        final ArrayAdapter<VolunteerOpportunity> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, availableOpportunities);
        VolunteerOpportunity.getActiveOpportunities(new FindCallback<VolunteerOpportunity>() {
            @Override
            public void done(List<VolunteerOpportunity> objects, ParseException e) {
                if(e == null){
                    for (VolunteerOpportunity opportunity : objects) {
                        availableOpportunities.add(opportunity);
                    }
                }
                dataAdapter.notifyDataSetChanged();
            }
        });

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRecognitionActivitySpinner.setPromptId(R.string.recog_activity_hint);
        mRecognitionActivitySpinner.setAdapter(
                new SelectorHintAdapter(dataAdapter, R.layout.action_selector_hint_row, this));
    }

    //region Logo Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            //Logo selection
            if (requestCode == Constants.IMAGE_PICKER_REQUEST_CODE) {
                Uri imageUri = getPickImageResultUri(data);

                final RelativeLayout cropSection = (RelativeLayout) findViewById(R.id.crop_section);
                cropSection.setVisibility(View.VISIBLE);

                mCropImageView.setFixedAspectRatio(true);
                mCropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
                mCropImageView.setGuidelines(ON_TOUCH);
                mCropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
                mCropImageView.setImageUri(imageUri);

                findViewById(R.id.button_rotate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCropImageView.rotateImage(ROTATE_NINETY_DEGREES);
                    }
                });

                findViewById(R.id.button_crop_done).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveScaledPhoto(mCropImageView.getCroppedImage());
                        mLogoImageButton.setImageBitmap(mCropImageView.getCroppedImage());
                        cropSection.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    /*
	 * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
	 * they are saved. Since we never need a full-size image in our app, we'll
	 * save a scaled one right away.
	 */
    private void saveScaledPhoto(Bitmap data) {

        Bitmap profileImageScaled = Bitmap.createScaledBitmap(data, Constants.LIST_IMAGE_SIZE, Constants.LIST_IMAGE_SIZE, false);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        profileImageScaled.compress(Bitmap.CompressFormat.PNG, 100, bos);

        byte[] scaledData = bos.toByteArray();

        // Save the scaled image to Parse
        String fileName = mTitleText.getText().length() > 0 ? mTitleText.getText() + "logo.png": mResourceType + "logo.png";
        ParseFile logoImageFile = new ParseFile(fileName, scaledData);
        switch (mResourceType) {
            case Constants.ORGANIZATION_RESOURCE:
                ((Organization)mResourceObject).setLogo(logoImageFile);
                break;
            case Constants.SCHOOL_RESOURCE:
                ((School)mResourceObject).setLogo(logoImageFile);
                break;
            case Constants.INTEREST_RESOURCE:
                ((Interest)mResourceObject).setLogo(logoImageFile);
                break;
            case Constants.SKILL_RESOURCE:
                ((Skill)mResourceObject).setLogo(logoImageFile);
                break;
        }
    }
//endregion

    public void onSubmitClick(View v) {
        //Validate and Save the resource
        mProgressBar.setVisibility(View.VISIBLE);
        String errorMessage = validateResource();

        if(!Strings.isNullOrEmpty(errorMessage)){
            showError(errorMessage);
            return;
        }

        switch (mResourceType) {
            case Constants.ANNOUNCEMENT_RESOURCE:
                mResourceObject = Strings.isNullOrEmpty(mResourceObjectId)
                        ? new Announcement()
                        : ParseObject.createWithoutData(Announcement.class, mResourceObjectId);
                ((Announcement)mResourceObject).setAnnouncementText(mDescriptionText.getText().toString());
                ((Announcement)mResourceObject).setIsActive(mActiveStatus.isChecked());
                Announcement.saveAnnouncement((Announcement) mResourceObject, onResourceSave());
                break;

            case Constants.CATEGORY_RESOURCE:
                mResourceObject = Strings.isNullOrEmpty(mResourceObjectId)
                        ? new Category()
                        : ParseObject.createWithoutData(Category.class, mResourceObjectId);
                ((Category) mResourceObject).setCategoryName(mTitleText.getText().toString());
                ((Category) mResourceObject).setDescription(mDescriptionText.getText().toString());
                ((Category) mResourceObject).setCategoryColor(mColorHexText.getText().toString());
                ((Category) mResourceObject).setIsActive(mActiveStatus.isChecked());

                Category.saveCategory((Category) mResourceObject, onResourceSave());
                break;

            case Constants.RECOGNITION_RESOURCE:
                mResourceObject = Strings.isNullOrEmpty(mResourceObjectId)
                        ? new Recognition()
                        : ParseObject.createWithoutData(Recognition.class, mResourceObjectId);
                ((Recognition)mResourceObject).setTitle(mTitleText.getText().toString());
                ((Recognition)mResourceObject).setDescription(mDescriptionText.getText().toString());
                ((Recognition)mResourceObject).setPointsRequired(Integer.parseInt(mRecogReqPtsText.getText().toString()));
                ((Recognition)mResourceObject).setMaxUnits(Integer.parseInt(mRecogMaxUnitsText.getText().toString()));
                ((Recognition)mResourceObject).setIsHoursBased(mRecognitionUnit.isChecked());
                ((Recognition)mResourceObject).setRecognitionType(String.valueOf(mRecognitionTypeSpinner.getSelectedItem()));
                //TODO: Set logo
                //////((Recognition)mResourceObject).getLogo();
                ((Recognition)mResourceObject).setIsActive(mActiveStatus.isChecked());

                Recognition.saveRecognition((Recognition) mResourceObject, onResourceSave());
                break;

            case Constants.INTEREST_RESOURCE:
                mResourceObject = Strings.isNullOrEmpty(mResourceObjectId)
                        ? new Interest()
                        : ParseObject.createWithoutData(Interest.class, mResourceObjectId);
                ((Interest)mResourceObject).setInterestTitle(mTitleText.getText().toString());
                ((Interest)mResourceObject).setDescription(mDescriptionText.getText().toString());
                //TODO: Set logo
                //////((Interest)mResourceObject).getLogo();
                ((Interest)mResourceObject).setIsActive(mActiveStatus.isChecked());

                Interest.saveInterest((Interest) mResourceObject, onResourceSave());
                break;

            case Constants.SKILL_RESOURCE:
                mResourceObject = Strings.isNullOrEmpty(mResourceObjectId)
                        ? new Skill()
                        : ParseObject.createWithoutData(Skill.class, mResourceObjectId);
                ((Skill)mResourceObject).setSkillTitle(mTitleText.getText().toString());
                ((Skill)mResourceObject).setDescription(mDescriptionText.getText().toString());
                //TODO: Set logo
                //((Skill)mResourceObject).setLogo();
                ((Skill)mResourceObject).setIsActive(mActiveStatus.isChecked());

                Skill.saveSkill((Skill) mResourceObject, onResourceSave());
                break;

            case Constants.USER_RESOURCE:
                mResourceObject = Strings.isNullOrEmpty(mResourceObjectId)
                        ? new SpecialUser()
                        : ParseObject.createWithoutData(SpecialUser.class, mResourceObjectId);
                ((SpecialUser)mResourceObject).setEmailId(mTitleText.getText().toString());
                ((SpecialUser) mResourceObject).setRole(String.valueOf(mUserRoleSpinner.getSelectedItem()));
                ((SpecialUser)mResourceObject).setOrganizationName(String.valueOf(mUserOrgSpinner.getSelectedItem()));
                ((SpecialUser)mResourceObject).setIsActive(mActiveStatus.isChecked());
                SpecialUser.saveSpecialUser((SpecialUser) mResourceObject, onResourceSave());
                break;

            case Constants.SCHOOL_RESOURCE:
                mResourceObject = Strings.isNullOrEmpty(mResourceObjectId)
                        ? new School()
                        : ParseObject.createWithoutData(School.class, mResourceObjectId);
                ((School)mResourceObject).setSchoolName(mTitleText.getText().toString());
                ((School)mResourceObject).setDescription(mDescriptionText.getText().toString());
                //TODO: Set logo
                //((School)mResourceObject).setLogo();
                ((School)mResourceObject).setIsActive(mActiveStatus.isChecked());
                School.saveSchool((School) mResourceObject, onResourceSave());
                break;

            case Constants.GROUP_RESOURCE:
                mResourceObject = Strings.isNullOrEmpty(mResourceObjectId)
                        ? new InterestGroup()
                        : ParseObject.createWithoutData(InterestGroup.class, mResourceObjectId);
                ((InterestGroup)mResourceObject).setGroupName(mTitleText.getText().toString());
                ((InterestGroup)mResourceObject).setGroupDescription(mDescriptionText.getText().toString());
                //TODO: Set logo
                //((InterestGroup)mResourceObject).setLogo();
                ((InterestGroup)mResourceObject).setIsActive(mActiveStatus.isChecked());
                InterestGroup.saveGroup((InterestGroup) mResourceObject, onResourceSave());
                //TODO: Add members
                break;

            case Constants.ORGANIZATION_RESOURCE:
                mResourceObject = Strings.isNullOrEmpty(mResourceObjectId)
                        ? new Organization()
                        : ParseObject.createWithoutData(Organization.class, mResourceObjectId);
                ((Organization)mResourceObject).setOrganizationName(mTitleText.getText().toString());
                ((Organization)mResourceObject).setDescription(mDescriptionText.getText().toString());
                //TODO: Set logo
                //((Organization)mResourceObject).setLogo();
                ((Organization)mResourceObject).setIsActive(mActiveStatus.isChecked());

                Organization.saveOrganization((Organization) mResourceObject, onResourceSave());
                break;

            default:
                mResourceObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Snackbar snackbar = Snackbar.make(mSaveResourceForm, R.string.msg_data_success, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
        }
    }

    private String validateResource() {
        StringBuilder errorMessages = new StringBuilder();

        switch (mResourceType) {
            case Constants.ANNOUNCEMENT_RESOURCE:
                //Validate Announcement
                if(mDescriptionText.getText().toString().isEmpty()){
                    errorMessages.append(getResources().getString(R.string.no_description));
                }
                break;

//            case Constants.CATEGORY_RESOURCE:
//                //Validate Category
//                validateTitleAndDescription(errorMessages);
//                break;

            case Constants.RECOGNITION_RESOURCE:
                //Validate Recognition
                validateTitleAndDescription(errorMessages);
                errorMessages.append("\n");
                if(mRecogReqPtsText.getText().toString().isEmpty()){
                    errorMessages.append(getResources().getString(R.string.no_points_hours));
                }

                break;

//            case Constants.INTEREST_RESOURCE:
//                //Validate Interest
//                validateTitleAndDescription(errorMessages);
//                break;

//            case Constants.SKILL_RESOURCE:
//                //Validate Skill
//                validateTitleAndDescription(errorMessages);
//
//                break;

            case Constants.USER_RESOURCE:
                //Validate SpecialUser
                if(mTitleText.getText().toString().isEmpty()){
                    errorMessages.append(getResources().getString(R.string.no_email));
                    errorMessages.append("\n");
                }
                //Check for role?

                break;

//            case Constants.SCHOOL_RESOURCE:
//                //Validate School
//                validateTitleAndDescription(errorMessages);
//
//                break;

//            case Constants.GROUP_RESOURCE:
//                //Validate Group
//                validateTitleAndDescription(errorMessages);
//
//                break;

//            case Constants.ORGANIZATION_RESOURCE:
//                //Validate Organization
//                validateTitleAndDescription(errorMessages);
//
//                break;

            case Constants.OPPORTUNITY_RESOURCE:
                //Validate Opportunity
                validateTitleAndDescription(errorMessages);

                break;

            default:
                validateTitleAndDescription(errorMessages);

                break;
        }

        return errorMessages.toString();
    }

    private void validateTitleAndDescription(StringBuilder errorMessages) {
        if(mTitleText.getText().toString().isEmpty()){
            errorMessages.append(getResources().getString(R.string.no_title));
            errorMessages.append("\n");
        }
        if(mDescriptionText.getText().toString().isEmpty()){
            errorMessages.append(getResources().getString(R.string.no_description));
        }
    }

    @NonNull
    private SaveCallback onResourceSave() {
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mProgressBar.setVisibility(View.GONE);
                Snackbar snackbar;
                if(e == null) {
                    if(Strings.isNullOrEmpty(mResourceObjectId)){
                        postNewAdditionToWall();
                    }
                    snackbar = Snackbar.make(mSaveResourceForm, R.string.msg_data_success, Snackbar.LENGTH_LONG);
                } else {
                    snackbar = Snackbar.make(mSaveResourceForm, R.string.msg_data_failure, Snackbar.LENGTH_LONG);
                }
                snackbar.show();
            }
        };
    }

//    private void exitSuccess()
//    {
//        Bundle bundle = new Bundle();
//        bundle.putInt("Longitude", updatedGeoPoint.getLongitudeE6());
//        bundle.putInt("Latitude", updatedGeoPoint.getLatitudeE6());
//        Intent intent = new Intent();
//        intent.putExtras(bundle);
//        setResult(RESULT_OK, intent);
//        finish();
//    }

    private void postNewAdditionToWall() {

        if(mResourceType.equals(Constants.INTEREST_RESOURCE)
                || mResourceType.equals(Constants.SKILL_RESOURCE)
                || mResourceType.equals(Constants.RECOGNITION_RESOURCE)){

            Post resourceAddedPost = new Post();
            resourceAddedPost.setPostText(String.format(getString(R.string.resource_added_post),
                    mResourceType, mTitleText.getText().toString()));
            resourceAddedPost.setPostBy(ParseUser.getCurrentUser());
            resourceAddedPost.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        showToast(R.string.activity_post_success);
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        startManageResourceActivity(mResourceType, mUserOrganization);
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startManageResourceActivity(mResourceType, mUserOrganization);
            finish();
        }
        return true;
    }

}
