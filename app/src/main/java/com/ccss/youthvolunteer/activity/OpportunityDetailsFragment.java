package com.ccss.youthvolunteer.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectorHintAdapter;
import com.ccss.youthvolunteer.model.Category;
import com.ccss.youthvolunteer.model.Organization;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DateUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class OpportunityDetailsFragment extends Fragment  {

    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 20;
    private static final int ON_TOUCH = 1;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(Constants.DATE_FORMAT);
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String USER_ORGANIZATION_TAG = "UserOrg";
    private static final String OPPORTUNITY_TAG = "OpportunityDetails";
    private static final String READ_ONLY_TAG = "ReadOnly";
    private static final String NEW_TAG = "IsNew";

    private EditText mTitleText;
    private EditText mDescriptionText;
    private EditText mActivityImpactText;
    private EditText mActivitySplFeatureText;
    private EditText mActivityLocationText;
    private CheckBox mVirtualCheck;
    private ImageButton mActivityDatePicker;
    private ImageButton mActivityTimePicker;
    private static EditText mActivityDateText;
    private static EditText mActivityTimeText;
    private EditText mActivityPoints;
    private ImageButton mActivityLocationPicker;
    private CheckBox mActiveStatus;
    private Button mSubmitButton;
    private Spinner mCategorySpinner;
    private TextView mCategoryText;
    private Spinner mActivityOrgSpinner;
    private TextView mActivityOrgText;

    boolean mReadOnly;
    boolean mIsNewAddition;
    private String mUserOrganization;
    private String mValidationMessage;
    private VolunteerOpportunity mVolunteerOpportunity;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static OpportunityDetailsFragment newInstance(int sectionNumber, VolunteerOpportunity opportunity, boolean readOnly, String userOrg, boolean isNew) {
        OpportunityDetailsFragment fragment = new OpportunityDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(OPPORTUNITY_TAG, opportunity);
        args.putBoolean(READ_ONLY_TAG, readOnly);
        args.putString(USER_ORGANIZATION_TAG, userOrg);
        args.putBoolean(NEW_TAG, isNew);
        fragment.setArguments(args);
        return fragment;
    }

    private void initializeOrganizationSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                Strings.isNullOrEmpty(mUserOrganization)
                        ? Organization.getAllActiveOrganizationNames()
                        : Lists.newArrayList(mUserOrganization));

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mActivityOrgSpinner.setPromptId(R.string.org_input_hint);
        mActivityOrgSpinner.setAdapter(new SelectorHintAdapter(dataAdapter, R.layout.org_selector_hint, getActivity()));
    }

    private void initializeCategorySpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, Category.getAllCategoryNames());

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setPromptId(R.string.category_prompt);
        mCategorySpinner.setAdapter(
                new SelectorHintAdapter(dataAdapter, R.layout.category_selector_hint_row, getActivity()));
    }

    private void populateOpportunityDetails() {

        if (!mIsNewAddition) {
            mTitleText.setText(mVolunteerOpportunity.getTitle());
            mDescriptionText.setText(mVolunteerOpportunity.getDescription());
            mActivityPoints.setText(mVolunteerOpportunity.getActionPoints());
            mActivityImpactText.setText(mVolunteerOpportunity.getImpact());
            mActivitySplFeatureText.setText(mVolunteerOpportunity.getSpecialFeature());
            mActivityLocationText.setText(mVolunteerOpportunity.getLocationName());
            mActivityDateText.setText(DateUtils.formattedDateString(mVolunteerOpportunity.getActionStartDate()));
            mActivityTimeText.setText(mVolunteerOpportunity.getActionDuration());
            mActiveStatus.setSelected(mVolunteerOpportunity.isActive());

            if(mReadOnly){
                mCategoryText.setText(mVolunteerOpportunity.getActionCategory().getCategoryName());
                mActivityOrgText.setText(mVolunteerOpportunity.getOrganizationName());
            } else {
                for (int i = 1; i <= mCategorySpinner.getCount(); i++) {
                    if (mCategorySpinner.getItemAtPosition(i).toString().equals(mVolunteerOpportunity.getActionCategory().getCategoryName())) {
                        mCategorySpinner.setSelection(i);
                        break;
                    }
                }

                for (int i = 1; i <= mActivityOrgSpinner.getCount(); i++) {
                    if (mActivityOrgSpinner.getItemAtPosition(i).toString().equals(mVolunteerOpportunity.getOrganizationName())) {
                        mActivityOrgSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReadOnly = getArguments().getBoolean(READ_ONLY_TAG);
        mUserOrganization = getArguments().getString(USER_ORGANIZATION_TAG);

        mIsNewAddition = getArguments().getBoolean(NEW_TAG);
        mVolunteerOpportunity = (VolunteerOpportunity) getArguments().getSerializable(OPPORTUNITY_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.opportunity_details_fragment, container, false);

        ImageView navUsers = (ImageView) rootView.findViewById(R.id.details_to_interested_users_section);
        mSubmitButton = (Button) rootView.findViewById(R.id.submit_opportunity);
        mTitleText = (EditText) rootView.findViewById(R.id.manage_activity_title);
        mDescriptionText = (EditText) rootView.findViewById(R.id.manage_activity_description);
        mActivityOrgSpinner = (Spinner) rootView.findViewById(R.id.manage_activity_org);
        mCategorySpinner = (Spinner) rootView.findViewById(R.id.manage_activity_category);
        mActivityImpactText = (EditText) rootView.findViewById(R.id.manage_activity_impact);
        mActivitySplFeatureText = (EditText) rootView.findViewById(R.id.manage_activity_spl);
        mActivityLocationText = (EditText) rootView.findViewById(R.id.manage_activity_location);
        mVirtualCheck = (CheckBox) rootView.findViewById(R.id.manage_activity_virtual);
        mActiveStatus = (CheckBox) rootView.findViewById(R.id.opportunity_manage_active);
        mActivityDateText = (EditText) rootView.findViewById(R.id.manage_activity_date);
        mActivityDatePicker = (ImageButton) rootView.findViewById(R.id.manage_activity_cal_button);
        mActivityDatePicker.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        mActivityTimeText = (EditText) rootView.findViewById(R.id.manage_activity_duration);
        mActivityTimePicker = (ImageButton) rootView.findViewById(R.id.manage_activity_time_button);
        mActivityTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DurationPickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        mActivityPoints = (EditText) rootView.findViewById(R.id.manage_activity_points);
        mActivityLocationPicker = (ImageButton) rootView.findViewById(R.id.manage_activity_map_button);
        mActivityLocationPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPlacePicker();
            }
        });
        mActivityOrgText = (TextView) rootView.findViewById(R.id.activity_organization_text);
        mCategoryText = (TextView) rootView.findViewById(R.id.activity_category_text);

        if(!mReadOnly){
            initializeCategorySpinner();
            initializeOrganizationSpinner();
        } else {
            setControlsInReadOnlyMode();
        }

        if(mIsNewAddition) {
            navUsers.setVisibility(View.INVISIBLE);
        } else {
            setHasOptionsMenu(true);
            populateOpportunityDetails();
        }

        mActivityDatePicker.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });

        ImageView navigateSkills = (ImageView) rootView.findViewById(R.id.details_to_skills_section);
        navigateSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OpportunityDetailActivity) getActivity()).onNavigateToSkillsClick(null);
            }
        });

        navUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OpportunityDetailActivity) getActivity()).onNavigateToInterestedUsersClick();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    mSubmitButton.setEnabled(false);
                    setOpportunityDataToSave();
                    ((OpportunityDetailActivity) getActivity()).onSubmitDetailsClick(mVolunteerOpportunity);
                } else {
                    Toast.makeText(getActivity(), mValidationMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    public void enableSubmitButton(){
        mSubmitButton.setEnabled(true);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.express_interest:
//                mVolunteerOpportunity.setInterestedUsers();
//                return true;
//            case R.id.expressed_interest:
//                // do s.th.
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private void setControlsInReadOnlyMode() {
        mSubmitButton.setVisibility(View.INVISIBLE);
        mTitleText.setEnabled(false);
        mDescriptionText.setEnabled(false);
        mActivityOrgSpinner.setVisibility(View.INVISIBLE);
        mActivityOrgText.setVisibility(View.VISIBLE);
        mCategorySpinner.setVisibility(View.INVISIBLE);
        mCategoryText.setVisibility(View.VISIBLE);
        mActivityImpactText.setEnabled(false);
        mActivitySplFeatureText.setEnabled(false);
        mActivityLocationText.setEnabled(false);
        mVirtualCheck.setEnabled(false);
        mActiveStatus.setEnabled(false);
        mActivityDateText.setEnabled(false);
        mActivityDatePicker.setVisibility(View.INVISIBLE);
        mActivityTimeText.setEnabled(false);
        mActivityTimePicker.setVisibility(View.INVISIBLE);
        mActivityPoints.setEnabled(false);
        mActivityLocationPicker.setVisibility(View.INVISIBLE);
    }

    private void setOpportunityDataToSave() {
        mVolunteerOpportunity.setTitle(mTitleText.getText().toString());
        mVolunteerOpportunity.setDescription(mDescriptionText.getText().toString());
        mVolunteerOpportunity.setActionPoints(Integer.parseInt(mActivityPoints.getText().toString()));
        mVolunteerOpportunity.setImpact(mActivityImpactText.getText().toString());
        mVolunteerOpportunity.setSpecialFeature(mActivitySplFeatureText.getText().toString());
        mVolunteerOpportunity.setLocationName(mActivityLocationText.getText().toString());
        mVolunteerOpportunity.setIsVirtual(mVirtualCheck.isChecked());
        if(!mVirtualCheck.isChecked()){
            mVolunteerOpportunity.setActionStartDate(DATE_TIME_FORMATTER.parseDateTime(mActivityDateText.getText().toString()).toDate());
        }
        mVolunteerOpportunity.setActionDuration(Integer.parseInt(mActivityTimeText.getText().toString()));
        mVolunteerOpportunity.setIsActive(mActiveStatus.isChecked());
        mVolunteerOpportunity.setActionCategory(Category.getFromName(mCategorySpinner.getSelectedItem().toString()));
        mVolunteerOpportunity.setOrganizationName((String) mActivityOrgSpinner.getSelectedItem());

    }

    public boolean validate() {
        StringBuilder validationMessage = new StringBuilder();

        if(mTitleText.getText().toString().isEmpty()){
            validationMessage.append(getString(R.string.no_title));
            validationMessage.append("\n");
        }
        if(mDescriptionText.getText().toString().isEmpty()){
            validationMessage.append(getString(R.string.no_description));
        }
        if(mActivityPoints.getText().toString().isEmpty()){
            validationMessage.append(getString(R.string.no_points_hours));
        }
        if(!mVirtualCheck.isChecked() && mActivityDateText.getText().toString().isEmpty()){
            validationMessage.append(getString(R.string.no_start_date));
        }

        if(validationMessage.length() > 0){
            mValidationMessage = validationMessage.toString();
            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            //Location selection
            if (requestCode == Constants.PLACE_PICKER_REQUEST_CODE) {
                Place place = PlacePicker.getPlace(data, getActivity());
                mActivityLocationText.setText(place.getName());
            }
        }
    }

    private void launchPlacePicker() {
        PlacePicker.IntentBuilder placePickerBuilder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(placePickerBuilder.build(getActivity()), Constants.PLACE_PICKER_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e("Location Picker", e.getLocalizedMessage(), e);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            LocalDate dateToday = new LocalDate();

            String textDate = mActivityDateText.getText().toString();
            if(DateUtils.isValidDate(textDate, false)) {
                dateToday = DateUtils.stringToLocalDate(textDate);
            }

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                    this, dateToday.getYear(), dateToday.getMonthOfYear(), dateToday.getDayOfMonth());
            dialog.getDatePicker().setMinDate(dateToday.plusMonths(-1).toDateTimeAtStartOfDay().getMillis());
            dialog.getDatePicker().setMaxDate(dateToday.plusMonths(3).toDateTimeAtStartOfDay().getMillis());

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mActivityDateText.setText(DateUtils.convertedDate(year, month, dayOfMonth));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DurationPickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, 0, 0, true );
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // set current time into textview
//            mActivityTimeText.setText(new StringBuilder().append(pad(hourOfDay))
//                    .append(":").append(pad(minute)));

            mActivityTimeText.setText(hourOfDay * 60 + minute);
        }

        private static String pad(int c) {
            if (c >= 10)
                return String.valueOf(c);
            else
                return "0" + String.valueOf(c);
        }
    }
}