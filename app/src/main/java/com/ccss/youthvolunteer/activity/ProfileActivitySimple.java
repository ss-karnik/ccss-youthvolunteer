package com.ccss.youthvolunteer.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.InterestsExpandableAdapter;
import com.ccss.youthvolunteer.adapter.SelectorHintAdapter;
import com.ccss.youthvolunteer.adapter.SkillsExpandableAdapter;
import com.ccss.youthvolunteer.model.Interests;
import com.ccss.youthvolunteer.model.ProfileVerticalParent;
import com.ccss.youthvolunteer.model.School;
import com.ccss.youthvolunteer.model.Skills;
import com.ccss.youthvolunteer.model.SpecialUser;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.adapter.ExpandableAdapter;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ProfileActivitySimple extends BaseActivity implements View.OnClickListener, ExpandableRecyclerAdapter.ExpandCollapseListener  {

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
    private List<SpecialUser> mSpecialUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_simple);

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
        //setupSkillsView();
        //setupInterestsView();

        ImageButton btnChangeDate = (ImageButton) findViewById(R.id.cal_button);
        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        TextView userNameField = (TextView) findViewById(R.id.signup_user_email);
        userNameField.setText(mVolunteerUser.getEmail());

        if(mVolunteerUser.isProfileComplete()){
            loadPredefinedUserValues();
        }

        Button completeAccountButton = (Button) findViewById(R.id.complete_account);
        completeAccountButton.setOnClickListener(this);
    }

    private void loadStaticData() {
        SpecialUser.findInBackground(new FindCallback<SpecialUser>() {
            @Override
            public void done(List<SpecialUser> objects, ParseException e) {
                if(e == null) {
                    mSpecialUsers = objects;
                }
            }
        });

        Interests.findInBackground(new FindCallback<Interests>() {
            @Override
            public void done(List<Interests> objects, ParseException e) {
                if (e == null) {
                    mInterests = objects;
                    setupInterestsView();
                    //mInterestsAdapter.notifyDataSetChanged();
                }
            }
        });

        Skills.findInBackground(new FindCallback<Skills>() {
            @Override
            public void done(List<Skills> objects, ParseException e) {
                if (e == null) {
                    mSkills = objects;
                    setupSkillsView();
                    //mSkillsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setupInterestsView() {
        ProfileVerticalParent interestParentItem = new ProfileVerticalParent();
        interestParentItem.setParentText(getResources().getString(R.string.interests_title));
        interestParentItem.setChildItemList(mInterests);

        List<ProfileVerticalParent> interestsListItems = Lists.newArrayList(interestParentItem);
        ExpandableAdapter.OnItemClickListener listener = new ExpandableAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String title) {
                Log.d("Interests", "Selected title : " + title);
            }
        };
        mInterestsAdapter = new InterestsExpandableAdapter(this, interestsListItems, mVolunteerUser.getUserInterests(), listener);
        mInterestsView.setHasFixedSize(true);
        mInterestsView.setAdapter(mInterestsAdapter);
        mInterestsView.setLayoutManager(getLayoutManager());
    }

    private RecyclerView.LayoutManager getLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        return linearLayoutManager;
    }

    private void setupSkillsView() {
        ProfileVerticalParent skillsParentItem = new ProfileVerticalParent();
        skillsParentItem.setParentText(getResources().getString(R.string.skills_title));
        skillsParentItem.setChildItemList(mSkills);

        List<ProfileVerticalParent> skillsListItems = Lists.newArrayList(skillsParentItem);
        ExpandableAdapter.OnItemClickListener listener = new ExpandableAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String title) {
                Log.d("Skills", "Selected title : " + title);
            }
        };
        mSkillsAdapter = new SkillsExpandableAdapter(this, skillsListItems, mVolunteerUser.getUserSkills(), listener);
        mSkillsView.setHasFixedSize(true);
        mSkillsView.setAdapter(mSkillsAdapter);
        mSkillsView.setLayoutManager(getLayoutManager());
    }

    /**
     * Save the instance state of the adapter to keep expanded/collapsed states when rotating or
     * pausing the activity.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //mInterestsAdapter.onSaveInstanceState(outState);
        //mSkillsAdapter.onSaveInstanceState(outState);
        // Make sure you save the current image resource
        outState.putParcelable(PROFILE_IMAGE, ((BitmapDrawable)mProfileImageButton.getDrawable()).getBitmap());
        super.onSaveInstanceState(outState);
    }

    /**
     * Load the expanded/collapsed states of the adapter back into the view when done rotating or
     * resuming the activity.
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //mInterestsAdapter.onRestoreInstanceState(savedInstanceState);
        //mSkillsAdapter.onRestoreInstanceState(savedInstanceState);

        Bitmap bmp = savedInstanceState.getParcelable(PROFILE_IMAGE);
        mProfileImageButton.setImageBitmap(bmp);
    }

    private void setupSchoolSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, School.getAllActiveSchools());

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSchoolNameSpinner.setPromptId(R.string.com_parse_ui_schoolname_input_hint);
        mSchoolNameSpinner.setAdapter(
                new SelectorHintAdapter(dataAdapter, R.layout.selector_hint_row, this));
    }

    private void loadPredefinedUserValues() {
        ParseFile imageFile = mVolunteerUser.getProfileImage();
        if (imageFile != null) {
            mProfileImageButton.setParseFile(imageFile);
            mProfileImageButton.loadInBackground();
        } else {
            mProfileImageButton.setImageResource(R.drawable.default_avatar_small_64);
        }

        mFirstNameField.setText(mVolunteerUser.getFirstName());
        mLastNameField.setText(mVolunteerUser.getLastName());
        if(!Strings.isNullOrEmpty(mVolunteerUser.getOrganizationName())) {
            mOrganizationField.setText(mVolunteerUser.getOrganizationName());
            mOrganizationField.setVisibility(View.VISIBLE);
        }

        for(int i = 1; i <= mSchoolNameSpinner.getCount(); i++){
            if(mSchoolNameSpinner.getItemAtPosition(i).toString().equals(mVolunteerUser.getSchoolName())){
                mSchoolNameSpinner.setSelection(i);
                break;
            }
        }
        dateOfBirthField.setText(DateFormat.format("dd/MM/yyyy", mVolunteerUser.getDateOfBirth()));
        if("Male".equalsIgnoreCase(mVolunteerUser.getGender())){
            mRadioGenderGroup.check(R.id.radioMale);
        } else {
            mRadioGenderGroup.check(R.id.radioFemale);
        }

        mMobileNumberField.setText(mVolunteerUser.getMobileNumber());
        mFbPublishPermission.setSelected(mVolunteerUser.isPublishToFbPermission());
    }

//region  Profile Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
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
                        mProfileImageButton.setImageBitmap(mCropImageView.getCroppedImage());
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

        Bitmap profileImageScaled = Bitmap.createScaledBitmap(data, Constants.PROFILE_IMAGE_SIZE,
                                                              Constants.PROFILE_IMAGE_SIZE, false);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        profileImageScaled.compress(Bitmap.CompressFormat.PNG, 100, bos);

        byte[] scaledData = bos.toByteArray();

        // Save the scaled image to Parse
        ParseFile profilePhotoFile = new ParseFile("profile_photo.png", scaledData);
        mVolunteerUser.setProfileImage(profilePhotoFile);
    }
//endregion

// region Save User
    @Override
    public void onClick(View v) {
        final VolunteerUser volunteer = mVolunteerUser;

        String firstName = null;
        if (mFirstNameField != null) {
            firstName = mFirstNameField.getText().toString();
        }

        String lastName = null;
        if (mLastNameField != null) {
            lastName = mLastNameField.getText().toString();
        }

        String schoolName = null;
        if (mSchoolNameSpinner != null) {
            schoolName = String.valueOf(mSchoolNameSpinner.getSelectedItem());
        }

        String dateOfBirth = null;
        if (dateOfBirthField != null) {
            dateOfBirth = dateOfBirthField.getText().toString();
        }

        RadioButton radioGenderButton = (RadioButton) findViewById(mRadioGenderGroup.getCheckedRadioButtonId());
        volunteer.setGender(radioGenderButton.getText().toString());

        StringBuilder validationMessage = new StringBuilder();

        if (firstName != null && firstName.length() == 0) {
            validationMessage.append(getResources().getText(com.parse.ui.R.string.com_parse_ui_no_first_name_toast));
            validationMessage.append("\n");
        } else if (lastName != null && lastName.length() == 0) {
            validationMessage.append(getResources().getText(com.parse.ui.R.string.com_parse_ui_no_last_name_toast));
            validationMessage.append("\n");
        } else if ((dateOfBirth != null && dateOfBirth.length() == 0) || !isValidDate(dateOfBirth)) {
            validationMessage.append(getResources().getText(com.parse.ui.R.string.com_parse_ui_no_dob_toast));
            validationMessage.append("\n");
        } else if (schoolName != null && schoolName.length() == 0) {
            validationMessage.append(getResources().getText(com.parse.ui.R.string.com_parse_ui_no_school_name_toast));
            validationMessage.append("\n");
        }

        if(validationMessage.length() > 0){
            showToast(validationMessage.toString());
            return;
        }

        // Set additional custom fields only if the user filled it out
        if (firstName.length() != 0) {
            volunteer.put(USER_FIRST_NAME_FIELD, firstName);
        }
        if (lastName.length() != 0) {
            volunteer.put(USER_LAST_NAME_FIELD, lastName);
        }
        if (schoolName.length() != 0) {
            volunteer.put(USER_SCHOOL_NAME_FIELD, schoolName);
        }
        if (dateOfBirth.length() != 0) {
            volunteer.put(USER_DOB_FIELD, DOB_FORMAT.parseDateTime(dateOfBirth).toDate());
        }

        //if (Strings.isNullOrEmpty(volunteer.getOrganizationName())) {
            final SpecialUser specialUser = Iterables.tryFind(mSpecialUsers, isSpecialUser(volunteer.getEmail())).orNull()  ;
            if (specialUser != null) {
                assignRole(specialUser);
                volunteer.setSpecialRole(specialUser.getRole());
                volunteer.setOrganizationName(specialUser.getOrganizationName());
            }
        //}

        volunteer.setMobileNumber(mMobileNumberField.getText().toString());
        volunteer.setPublishToFbPermission(mFbPublishPermission.isChecked());
        volunteer.setProfileComplete(true);
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

    private void assignRole(final SpecialUser specialUser) {
        final String roleToSaveAs = specialUser.getRole();
        ParseQuery<ParseRole> query = ParseRole.getQuery();
        query.whereEqualTo("name", roleToSaveAs);
        try {
            ParseRole role = query.getFirst();
            role.getUsers().add(mVolunteerUser);
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
        Snackbar snackbar = Snackbar.make(mProfileDetailsForm, R.string.msg_user_register_sucess, Snackbar.LENGTH_LONG);
        snackbar.show();

//        Toast.makeText(getBaseContext(), R.string.msg_user_register_sucess, Toast.LENGTH_LONG).show();
        // Execute some code after 2 seconds have passed
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                startManageActivityWithIntent(MainActivity.class);
//            }
//        }, 2000);
    }

    private static boolean isValidDate(String dateToValidate) {
        try {
            DateTime enteredDate = DOB_FORMAT.parseDateTime(dateToValidate);
            return DateTime.now().getYear() - enteredDate.getYear() >= Constants.MINIMUM_AGE;
        }
        catch (IllegalArgumentException ex){
            return false;
        }
    }
//endregion

    @Override
    public void onListItemExpanded(int position) {

    }

    @Override
    public void onListItemCollapsed(int position) {

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
     public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            LocalDate dateMax = new LocalDate().minusYears(Constants.MINIMUM_AGE);
            //LocalDate dateMin = new LocalDate().minusYears(20);

            LocalDate dobEntered = dateMax;

            String textDate = dateOfBirthField.getText().toString();
            if(isValidDate(textDate)) {
                DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
                dobEntered = dtf.parseDateTime(textDate).toLocalDate();
            }

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                    this, dobEntered.getYear(), dobEntered.getMonthOfYear()-1, dobEntered.getDayOfMonth());
            //dialog.getDatePicker().setMinDate(dateMin.toDateTimeAtStartOfDay().getMillis());
            dialog.getDatePicker().setMaxDate(dateMax.toDateTimeAtStartOfDay().getMillis());

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            int monthOfYear = month + 1;
            String formattedMonth = "" + monthOfYear;
            String formattedDayOfMonth = "" + dayOfMonth;

            if(monthOfYear < 10){
                formattedMonth = "0" + monthOfYear;
            }

            if(dayOfMonth < 10){
                formattedDayOfMonth = "0" + dayOfMonth;
            }

            dateOfBirthField.setText(new StringBuilder().append(formattedDayOfMonth).append("/")
                    .append(formattedMonth).append("/").append(year));
        }
    }
}
