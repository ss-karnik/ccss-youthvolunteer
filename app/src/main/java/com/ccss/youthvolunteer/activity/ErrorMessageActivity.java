package com.ccss.youthvolunteer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        Button retry = (Button) findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.class);
            }
        });
    }
}
