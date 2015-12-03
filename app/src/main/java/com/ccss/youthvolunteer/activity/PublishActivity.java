package com.ccss.youthvolunteer.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.util.Constants;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SendCallback;

import java.util.List;

public class PublishActivity extends BaseActivity {
    private View mMainView;
    private View mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        mMainView = findViewById(R.id.main_publish_view);
        mProgressBar =  findViewById(R.id.publish_progress);
        final Button button = (Button) findViewById(R.id.btnPublish);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                publishMessage();
            }
        });
        checkEntitlements();
    }

    private void checkEntitlements() {
        DialogInterface.OnClickListener dlgDismissListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
            }
        };
        ParseQuery<ParseRole> query = ParseRole.getQuery();
        query.whereEqualTo("name", "Admin");
        query.whereEqualTo("users", ParseUser.getCurrentUser());
        try {
            List<ParseRole> parseRoles = query.find();
            if (!parseRoles.isEmpty()) {
                return;
            }
        } catch (ParseException e) {
            showError(e.getLocalizedMessage());
        }
        showError(R.string.error_msg_user_not_entitled,dlgDismissListener);
    }


    public void publishMessage() {
        EditText et = (EditText) findViewById(R.id.txtMessage);
        String str = et.getText().toString();
        ParsePush push = new ParsePush();
        push.setChannel(Constants.OPPORTUNITY_BROADCAST_CHANNEL);
        push.setMessage(str);
        showProgress(true, mMainView, mProgressBar);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getBaseContext(), R.string.msg_publish_success, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), R.string.msg_publish_failed + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                showProgress(false, mMainView, mProgressBar);
            }
        });
    }
}