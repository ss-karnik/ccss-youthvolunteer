package com.ccss.youthvolunteer.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.Toast;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectorHintAdapter;
import com.ccss.youthvolunteer.model.School;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DateUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class ProfileDetailsFragment extends Fragment implements ProfileActivity.ValidateFragment {

    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 20;
    private static final int ON_TOUCH = 1;
    private static final String PROFILE_IMAGE = "profileImage";
    private static final DateTimeFormatter DOB_FORMAT = DateTimeFormat.forPattern(Constants.DATE_FORMAT);
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String USER_TAG = "User";

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private Spinner mSchoolNameSpinner;
    public static EditText dateOfBirthField;

    private RadioGroup mRadioGenderGroup;
    private ParseImageView mProfileImageButton;
    private CheckBox mFbPublishPermission;
    private CropImageView mCropImageView;
    private TextView mOrganizationField;
    private TextView mMobileNumberField;
    private RelativeLayout mCropSection;
    private String mValidationMessage;

    private static VolunteerUser mVolunteerUser;
    private List<String> mSchools = Lists.newArrayList();

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static ProfileDetailsFragment newInstance(int sectionNumber, VolunteerUser user) {
        ProfileDetailsFragment fragment = new ProfileDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(USER_TAG, user);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileDetailsFragment() {
        loadSchools();
    }

    private void loadSchools(){
        if(mSchools.isEmpty()){
            mSchools = School.getAllActiveSchools();
        }
    }

    private void populateSchoolSpinner(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, mSchools);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSchoolNameSpinner.setPromptId(R.string.prompt_school_name);
        mSchoolNameSpinner.setAdapter(new SelectorHintAdapter(dataAdapter, R.layout.school_selector_hint, getActivity()));
    }

    private void populateExistingUserDetails() {
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
        dateOfBirthField.setText(DateFormat.format(Constants.DATE_FORMAT, mVolunteerUser.getDateOfBirth()));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                Uri imageUri = getPickImageResultUri(data);
                mCropSection.setVisibility(View.VISIBLE);
                mCropImageView.setFixedAspectRatio(true);
                mCropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
                mCropImageView.setGuidelines(ON_TOUCH);
                mCropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
                mCropImageView.setImageUri(imageUri);
        }
    }

    private Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getActivity().getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    private void rotateCropImage(){
        mCropImageView.rotateImage(ROTATE_NINETY_DEGREES);
    }

    private void processCroppedImage(){
        saveScaledPhoto(mCropImageView.getCroppedImage());
        mProfileImageButton.setImageBitmap(mCropImageView.getCroppedImage());
        mCropSection.setVisibility(View.GONE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
        else {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSchools();
        mVolunteerUser = (VolunteerUser) getArguments().getSerializable(USER_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_details_fragment, container, false);

        View mProgressBar = rootView.findViewById(R.id.profile_detail_progress);
        mSchoolNameSpinner = (Spinner) rootView.findViewById(R.id.detail_school_name);
        mProfileImageButton = (ParseImageView) rootView.findViewById(R.id.profile_detail_image);
        mFirstNameField = (EditText) rootView.findViewById(R.id.signup_detail_firstname_input);
        mLastNameField = (EditText) rootView.findViewById(R.id.signup_detail_lastname_input);
        mMobileNumberField = (EditText) rootView.findViewById(R.id.signup_detail_mobile_input);
        dateOfBirthField = (EditText) rootView.findViewById(R.id.signup_detail_dateofbirth_input);
        mFbPublishPermission = (CheckBox) rootView.findViewById(R.id.checkbox_fbPublish);
        mRadioGenderGroup = (RadioGroup) rootView.findViewById(R.id.detail_radioGroupGender);
        mCropImageView = (CropImageView) rootView.findViewById(R.id.detail_cropImageView);
        mOrganizationField = (TextView) rootView.findViewById(R.id.user_detail_organization);
        mCropSection = (RelativeLayout) rootView.findViewById(R.id.detail_crop_section);

        mProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), Constants.IMAGE_PICKER_REQUEST_CODE);
            }
        });

        rootView.findViewById(R.id.button_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateCropImage();
            }
        });

        rootView.findViewById(R.id.button_crop_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCroppedImage();
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);
        populateSchoolSpinner();

        TextView userNameField = (TextView) rootView.findViewById(R.id.signup_user_email);
        userNameField.setText(mVolunteerUser.getEmail());

        populateExistingUserDetails();
        mProgressBar.setVisibility(View.GONE);

        ImageButton btnDobCalendar = (ImageButton) rootView.findViewById(R.id.cal_button);
        btnDobCalendar.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });

        Button navigateSkills = (Button) rootView.findViewById(R.id.go_to_skills);
        navigateSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileActivity) getActivity()).onNavigateToSkillsClick(null);
            }
        });

        Button submitProfileData = (Button) rootView.findViewById(R.id.submit_profile);
        submitProfileData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    ((ProfileActivity) getActivity()).onSubmitDetailsClick(userWithDetailsProfile());
                } else {
                    Toast.makeText(getActivity(), mValidationMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    private VolunteerUser userWithDetailsProfile() {
        VolunteerUser volunteer = mVolunteerUser;
        volunteer.setFirstName(mFirstNameField.getText().toString());
        volunteer.setLastName(mLastNameField.getText().toString());
        volunteer.setSchoolName(String.valueOf(mSchoolNameSpinner.getSelectedItem()));
        volunteer.setDateOfBirth(DOB_FORMAT.parseDateTime(dateOfBirthField.getText().toString()).toDate());

        RadioButton radioGenderButton = (RadioButton) getView().findViewById(mRadioGenderGroup.getCheckedRadioButtonId());
        volunteer.setGender(radioGenderButton.getText().toString());

        volunteer.setMobileNumber(mMobileNumberField.getText().toString());
        volunteer.setPublishToFbPermission(mFbPublishPermission.isChecked());
        volunteer.setProfileComplete(true);
        volunteer.setIsActive(true);

        return volunteer;
    }

    private Intent getPickImageChooserIntent(){
        return new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    @Override
    public boolean validate() {
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

        StringBuilder validationMessage = new StringBuilder();

        if (firstName != null && firstName.length() == 0) {
            validationMessage.append(getResources().getText(com.parse.ui.R.string.com_parse_ui_no_first_name_toast));
            validationMessage.append("\n");
        } else if (lastName != null && lastName.length() == 0) {
            validationMessage.append(getResources().getText(com.parse.ui.R.string.com_parse_ui_no_last_name_toast));
            validationMessage.append("\n");
        } else if ((dateOfBirth != null && dateOfBirth.length() == 0) || !DateUtils.isValidDate(dateOfBirth, true)) {
            validationMessage.append(getResources().getText(com.parse.ui.R.string.com_parse_ui_no_dob_toast));
            validationMessage.append("\n");
        } else if (schoolName != null && schoolName.length() == 0) {
            validationMessage.append(getResources().getText(com.parse.ui.R.string.com_parse_ui_no_school_name_toast));
            validationMessage.append("\n");
        }

        if(validationMessage.length() > 0){
            mValidationMessage = validationMessage.toString();
            return false;
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private EditText mDateField;

        public EditText getDateField() {
            return mDateField;
        }

        public void setDateField(EditText dateField) {
            this.mDateField = dateField;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            LocalDate dateMax = new LocalDate().minusYears(Constants.MINIMUM_AGE);
            //LocalDate dateMin = new LocalDate().minusYears(20);

            LocalDate dobEntered = dateMax;

            String textDate = dateOfBirthField.getText().toString();
            if(DateUtils.isValidDate(textDate, true)) {
                dobEntered = DateUtils.stringToLocalDate(textDate);
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