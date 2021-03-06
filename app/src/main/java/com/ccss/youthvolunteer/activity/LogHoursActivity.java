package com.ccss.youthvolunteer.activity;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectorHintAdapter;
import com.ccss.youthvolunteer.model.Post;
import com.ccss.youthvolunteer.model.UserAction;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class LogHoursActivity extends BaseActivity implements View.OnClickListener {

    private RatingBar ratingBar;
    private Spinner actionSpinner;
    private static VolunteerOpportunity volunteerOpportunity;
    private static EditText activityDate;
    private static EditText activityDuration;
    private static EditText activityComments;
    private static TextView activityPoints;
    private static Button submit;
    private List<VolunteerOpportunity> volunteerOpportunities;
    //Save these in db
    private static int mActivityDurationMinutes;
    public static String mActivityRating;
    private VolunteerUser mVolunteerUser;
    private VolunteerOpportunity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_hours);

        activityDate = (EditText) findViewById(R.id.activity_log_date);
        activityDuration = (EditText) findViewById(R.id.activity_log_duration);
        activityComments = (EditText) findViewById(R.id.activity_log_comments);
        actionSpinner = (Spinner) findViewById(R.id.activity_log_action);
        ratingBar = (RatingBar) findViewById(R.id.activity_log_ratingBar);
        activityPoints = (TextView) findViewById(R.id.activity_log_points_allocated);
        ImageButton btnChangeDate = (ImageButton) findViewById(R.id.activity_log_cal_button);
        ImageButton btnSetDuration = (ImageButton) findViewById(R.id.activity_log_time_button);
        Button btnSubmit = (Button) findViewById(R.id.activity_log_submit);
        addListenerOnRatingBar();

        mVolunteerUser = VolunteerUser.getVolunteerUser(ParseUser.getCurrentUser());
        VolunteerOpportunity.getAllLogEligibleOpportunities(new FindCallback<VolunteerOpportunity>() {
            @Override
            public void done(List<VolunteerOpportunity> objects, ParseException e) {
                volunteerOpportunities = objects;
            }
        });

        ArrayAdapter<VolunteerOpportunity> dataAdapter = new ArrayAdapter<VolunteerOpportunity>(this,
                android.R.layout.simple_spinner_item, volunteerOpportunities);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setPromptId(R.string.action_prompt);
        actionSpinner.setAdapter(new SelectorHintAdapter(dataAdapter, R.layout.action_selector_hint_row, this));

        //TODO: on change activity- Fill relevant category, date, duration. Enable Submit
        addListenerOnActionItemSelection();

        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                DialogFragment dateFragment = new LogHoursActivity.DatePickerFragment();
                dateFragment.show(getFragmentManager(), "datePicker");
            }
        });

        btnSetDuration.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                DialogFragment timeFragment = new LogHoursActivity.DurationPickerFragment();
                timeFragment.show(getFragmentManager(), "durationPicker");
            }
        });
    }

    public void addListenerOnActionItemSelection() {
        actionSpinner.setOnItemSelectedListener(new ActionItemSelectedListener());
    }

    private void addListenerOnRatingBar() {
        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mActivityRating = String.valueOf(rating);
            }
        });
    }

    @Override
    public void onClick(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.okay_cancel_dialog);
        dialog.setTitle(R.string.log_activity_to_wall_title);

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
        text.setText(getResources().getText(R.string.log_activity_to_wall_prompt));

        Button dialogOkayButton = (Button) dialog.findViewById(R.id.dialog_okay);
        dialogOkayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post volunteerPost = new Post();
                volunteerPost.setPostText(String.format(getResources().getString(R.string.activity_post_comment),
                        mVolunteerUser.getFullName(), mActivityDurationMinutes, mActivity.getTitle()));
                volunteerPost.setPostBy(mVolunteerUser);
                volunteerPost.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            showToast(R.string.activity_post_success);
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        Button dialogCancelButton = (Button) dialog.findViewById(R.id.dialog_cancel);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        saveUserActivity();
    }

    private void saveUserActivity() {
        //Save UserAction
        UserAction actionToSave = new UserAction();
        actionToSave.setAction(mActivity.getObjectId());
        actionToSave.setActionDuration(mActivityDurationMinutes);
        DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.MONTH_YEAR_FORMAT);
        actionToSave.setActionMonthYear(dtf.parseDateTime(activityDate.toString()).toString());
        actionToSave.setActionDate(mActivity.getActionEndDate());
        actionToSave.setActionBy(ParseUser.getCurrentUser());

        if(mActivity.isVirtual()){
            actionToSave.setPointsAllocated(true);
            actionToSave.setVerifiedBy("SYSTEM");
            actionToSave.setVerifiedDate(DateTime.now().toDate());
            actionToSave.setVerificationResult(Constants.APPROVE);
        } else {
            actionToSave.setPointsAllocated(false);
            actionToSave.setVerificationResult(Constants.PENDING);
        }

        View form = findViewById(R.id.log_hours_form);
        Snackbar snackbar = Snackbar.make(form, R.string.msg_data_success, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            LocalDate dateToday = new LocalDate();
            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                    this, dateToday.getYear(), dateToday.getMonthOfYear(), dateToday.getDayOfMonth());
            dialog.getDatePicker().setMinDate(dateToday.plusMonths(-1).toDateTimeAtStartOfDay().getMillis());
            dialog.getDatePicker().setMaxDate(dateToday.toDateTimeAtStartOfDay().getMillis());

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
            activityDate.setText(new StringBuilder().append(formattedDayOfMonth).append("/")
                    .append(formattedMonth).append("/").append(year));
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
            activityDuration.setText(new StringBuilder().append(pad(hourOfDay))
                    .append(":").append(pad(minute)));

            mActivityDurationMinutes = hourOfDay * 60 + minute;
        }

        private static String pad(int c) {
            if (c >= 10)
                return String.valueOf(c);
            else
                return "0" + String.valueOf(c);
        }
    }

    private static class ActionItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //TODO: if activity has date, disable the date field and cal button and duration controls.
            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                    Toast.LENGTH_SHORT).show();

            //volunteerOpportunity = VolunteerOpportunity.getOpportunityDetails()
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
