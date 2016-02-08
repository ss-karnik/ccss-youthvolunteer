package com.ccss.youthvolunteer.activity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ccss.youthvolunteer.R;

public class VolunteerLogActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_action_log_view);

        Switch switchView = (Switch) findViewById(R.id.viewSwitch);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Show chart fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.log_fragment_container, VolunteerLogChartFragment.newInstance())
                            .commit();

                } else {
                    //Show list fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.log_fragment_container, VolunteerLogListFragment.newInstance())
                            .commit();
                }
            }
        });
    }
}