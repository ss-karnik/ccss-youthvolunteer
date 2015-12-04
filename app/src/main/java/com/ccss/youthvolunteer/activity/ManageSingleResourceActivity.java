package com.ccss.youthvolunteer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectorHintAdapter;
import com.ccss.youthvolunteer.model.Category;
import com.ccss.youthvolunteer.model.Interests;
import com.ccss.youthvolunteer.model.Organization;
import com.ccss.youthvolunteer.model.School;
import com.ccss.youthvolunteer.model.Skills;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.util.Constants;
import com.google.common.base.Strings;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ManageSingleResourceActivity extends BaseActivity implements View.OnClickListener {

    private static final int SELECT_PICTURE = 300;
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

    private TextView mColorHexRep;
    private EditText mColorHexText;

    private CropImageView mCropImageView;
    private ParseImageView mLogoImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent manageIntent = getIntent();
        mResourceType= manageIntent.getStringExtra(Constants.MANAGE_ITEM_KEY);
        setTitle(getResources().getText(R.string.manage_title) + " " + mResourceType);

        final String resourceObjectId = manageIntent.getStringExtra(Constants.OBJECT_ID_KEY);
        boolean isNewAddition = Strings.isNullOrEmpty(resourceObjectId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource);

        mProgressBar = (ProgressBar) findViewById(R.id.manage_res_progress);
        mSaveResourceForm = findViewById(R.id.save_resource_form);
        mTitleText = (EditText) findViewById(R.id.manage_res_title);
        mDescriptionText = (EditText) findViewById(R.id.manage_res_description);
        mActiveStatus = (CheckBox) findViewById(R.id.manage_active);

        //Category controls
        mColorHexText = (EditText) findViewById(R.id.manage_category_indicator);
        mColorHexRep = (TextView) findViewById(R.id.manage_category_color_indicator);
        ImageButton colorPicker = (ImageButton) findViewById(R.id.manage_category_color_picker_button);
        RelativeLayout categoryLayout = (RelativeLayout) findViewById(R.id.manage_color_picker);

        mLogoImageButton = (ParseImageView) findViewById(R.id.resource_image);
        mLogoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), SELECT_PICTURE);
        mCropImageView = (CropImageView) findViewById(R.id.res_cropImageView);
            }
        });


        //Set control visibility
        mProgressBar.setVisibility(View.VISIBLE);

        switch (mResourceType){
            case Constants.CATEGORY_RESOURCE:
                categoryLayout.setVisibility(View.VISIBLE);
                final int[] currentColor = new int[1];

                ParseQuery<Category> categoryQuery = ParseQuery.getQuery(Category.class);
                categoryQuery.whereEqualTo(Constants.OBJECT_ID_KEY, "");
                categoryQuery.getFirstInBackground(new GetCallback<Category>() {
                    @Override
                    public void done(Category object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Category();
                        }
                        mTitleText.setText(((Category) mResourceObject).getCategoryName());
                        mDescriptionText.setText(((Category) mResourceObject).getDescription());
                        String categoryColorHex = ((Category) mResourceObject).getCategoryColor();
                        mColorHexText.setText(categoryColorHex);
                        mColorHexRep.setBackgroundColor(Color.parseColor(categoryColorHex));
                        currentColor[0] = Color.parseColor(categoryColorHex);

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

            case Constants.INTEREST_RESOURCE:
                mLogoImageButton.setVisibility(View.VISIBLE);
                mCropImageView.setVisibility(View.VISIBLE);

                ParseQuery<Interests> interestQuery = ParseQuery.getQuery(Interests.class);
                interestQuery.whereEqualTo(Constants.OBJECT_ID_KEY, "");
                interestQuery.getFirstInBackground(new GetCallback<Interests>() {
                    @Override
                    public void done(Interests object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Category();
                        }
                        mTitleText.setText(((Interests) mResourceObject).getInterestTitle());
                        mDescriptionText.setText(((Interests) mResourceObject).getDescription());
                        ParseFile interestImage = ((Interests) mResourceObject).getImage();
                        mProgressBar.setVisibility(View.GONE);
                    }
                });

                break;

            case Constants.SKILL_RESOURCE:
                mLogoImageButton.setVisibility(View.VISIBLE);
                mCropImageView.setVisibility(View.VISIBLE);

                ParseQuery<Skills> skillsQuery = ParseQuery.getQuery(Skills.class);
                skillsQuery.whereEqualTo(Constants.OBJECT_ID_KEY, "");
                skillsQuery.getFirstInBackground(new GetCallback<Skills>() {
                    @Override
                    public void done(Skills object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Category();
                        }
                        mTitleText.setText(((Skills) mResourceObject).getSkillTitle());
                        mDescriptionText.setText(((Skills) mResourceObject).getDescription());
                        ParseFile skillsImage = ((Skills) mResourceObject).getImage();
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                break;

            case Constants.USER_RESOURCE:

                mProgressBar.setVisibility(View.VISIBLE);
                break;

            case Constants.SCHOOL_RESOURCE:
                mLogoImageButton.setVisibility(View.VISIBLE);
                mCropImageView.setVisibility(View.VISIBLE);

                ParseQuery<School> schoolQuery = ParseQuery.getQuery(School.class);
                schoolQuery.whereEqualTo(Constants.OBJECT_ID_KEY, "");
                schoolQuery.getFirstInBackground(new GetCallback<School>() {
                    @Override
                    public void done(School object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Category();
                        }
                        mTitleText.setText(((School) mResourceObject).getSchoolName());
                        ParseFile schoolLogo = ((School) mResourceObject).getImage();
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                break;

            case Constants.GROUP_RESOURCE:

                mProgressBar.setVisibility(View.VISIBLE);
                break;

            case Constants.ORGANIZATION_RESOURCE:
                mLogoImageButton.setVisibility(View.VISIBLE);
                mCropImageView.setVisibility(View.VISIBLE);

                ParseQuery<Organization> orgQuery = ParseQuery.getQuery(Organization.class);
                orgQuery.whereEqualTo(Constants.OBJECT_ID_KEY, "");
                orgQuery.getFirstInBackground(new GetCallback<Organization>() {
                    @Override
                    public void done(Organization object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Category();
                        }
                        mTitleText.setText(((Organization) mResourceObject).getOrganizationName());
                        ParseFile orgLogo = ((Organization) mResourceObject).getImage();
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                break;

            case Constants.OPPORTUNITY_RESOURCE:

                ParseQuery<VolunteerOpportunity> actionQuery = ParseQuery.getQuery(VolunteerOpportunity.class);
                actionQuery.whereEqualTo(Constants.OBJECT_ID_KEY, "");
                actionQuery.getFirstInBackground(new GetCallback<VolunteerOpportunity>() {
                    @Override
                    public void done(VolunteerOpportunity object, ParseException e) {
                        if (e == null) {
                            mResourceObject = object;
                        } else {
                            mResourceObject = new Category();
                        }
                        mTitleText.setText(((VolunteerOpportunity) mResourceObject).getTitle());
                        mDescriptionText.setText(((VolunteerOpportunity) mResourceObject).getDescription());

                        initializeOrganizationSpinner();
                        initializeCategorySpinner();

                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                break;
        }
    }

    private void initializeOrganizationSpinner() {
        //TODO: populate the Organization spinner control
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_spinner_item, Organization.getAllActiveSchools());
//
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSchoolNameSpinner.setPromptId(R.string.com_parse_ui_schoolname_input_hint);
//        mSchoolNameSpinner.setAdapter(
//                new SelectorHintAdapter(dataAdapter, R.layout.selector_hint_row, this));
    }

    private void initializeCategorySpinner() {
        //TODO: populate the category spinner control
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_spinner_item, Category.getAllActiveSchools());
//
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSchoolNameSpinner.setPromptId(R.string.com_parse_ui_schoolname_input_hint);
//        mSchoolNameSpinner.setAdapter(
//                new SelectorHintAdapter(dataAdapter, R.layout.selector_hint_row, this));
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
        ParseFile profilePhotoFile = new ParseFile("logo.png", scaledData);
        if(Constants.ORGANIZATION_RESOURCE.equalsIgnoreCase(mResourceType)
                || Constants.SCHOOL_RESOURCE.equalsIgnoreCase(mResourceType)){
            //mResourceObject.setProfileImage(profilePhotoFile);
        }

    }
//endregion

    @Override
    public void onClick(View v) {
        //Validate and Save the resource

        //TODO: get data from all the controls here
        mResourceObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Snackbar snackbar = Snackbar.make(mSaveResourceForm, R.string.msg_data_sucess, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }
}
