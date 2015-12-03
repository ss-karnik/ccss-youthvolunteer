package com.ccss.youthvolunteer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.model.PlaceInfo;
import com.ccss.youthvolunteer.model.PlacesResponse;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.RequestTask;
import com.google.common.base.Strings;

public class DisplayPlaceDetailsActivity extends BaseActivity {
    private String placeId;
    private TextView mNameView;
    private TextView mAddressView;
    private TextView mPhoneView;
    private View mPlaceDetailsForm;
    private View mProgressBar;

    private RequestTask<Void, PlacesResponse> requestTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_place_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlaceDetailsForm = findViewById(R.id.place_details_form);
        mProgressBar = findViewById(R.id.place_details_progress);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_place_details);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = mPhoneView.getText().toString();
                if (!Strings.isNullOrEmpty(phoneNumber)) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    startActivity(callIntent);
                }
            }
        });
        mNameView = (TextView) findViewById(R.id.txtName);
        mPhoneView = (TextView) findViewById(R.id.txtPhone);
        mAddressView = (TextView) findViewById(R.id.txtAddress);

        placeId = getIntent().getStringExtra(Constants.PLACE_ID);
        String url = getUrl(placeId);
        requestTask = new RequestTask<Void, PlacesResponse>(url, PlacesResponse.class) {
            @Override
            protected void onSuccess(PlacesResponse response) {
                if ("OK".equals(response.status) && response.result != null) {
                    PlaceInfo t = response.result;
                    mNameView.setText(t.name);
                    mAddressView.setText(t.formattedAddress);
                    mPhoneView.setText(t.internationalPhoneNumber);
                } else {
                    showError(response.error_message);
                }
                showProgress(false, mPlaceDetailsForm, mProgressBar);
            }

            @Override
            protected void onError(Exception ex) {
                showError(ex.getLocalizedMessage());
                showProgress(false, mPlaceDetailsForm, mProgressBar);
            }
        };
        showProgress(true, mPlaceDetailsForm, mProgressBar);
        requestTask.execute(null, null);

    }

    private String getUrl(String placeId) {
        return String.format(Constants.GOOGLE_PLACE_API_URL + "details/json?placeid=%s&key=%s&language=%s",
                placeId,
                Constants.GOOGLE_API_KEY,
                getBaseContext().getResources().getConfiguration().locale.getLanguage());

    }

}
