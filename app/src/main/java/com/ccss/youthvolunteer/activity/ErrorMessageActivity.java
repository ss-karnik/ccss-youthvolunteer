package com.ccss.youthvolunteer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.util.Constants;

public class ErrorMessageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent manageIntent = getIntent();
        final String errorMessage = manageIntent.getStringExtra(Constants.ERROR_ITEM_KEY);

        setContentView(R.layout.activity_error_message);
        TextView errorMessageText = (TextView) findViewById(R.id.error_message_text);
        errorMessageText.setText(errorMessage);

    }
}
