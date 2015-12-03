package com.ccss.youthvolunteer.activity;

import android.content.Intent;

import com.ccss.youthvolunteer.R;
import com.parse.ui.ParseLoginBuilder;
import com.parse.ui.ParseLoginDispatchActivity;

import java.util.Arrays;

public class DispatchActivity extends ParseLoginDispatchActivity {

    @Override
    protected Class<?> getTargetClass() {
        return MainActivity.class;
    }

    @Override
    protected Intent getParseLoginIntent() {
        ParseLoginBuilder builder = new ParseLoginBuilder(this);
        return builder.setParseLoginEnabled(true)
                .setParseLoginButtonText(R.string.action_sign_in_short)
                .setParseSignupButtonText(R.string.action_register_user)
                .setParseLoginHelpText(R.string.prompt_forgot_password)
                .setParseLoginInvalidCredentialsToastText(R.string.error_invalid_credentials)
                .setParseLoginEmailAsUsername(true)
                .setParseSignupMinPasswordLength(6)
                .setParseSignupSubmitButtonText(R.string.action_submit_register_user)
                .setFacebookLoginEnabled(true)
                .setFacebookLoginButtonText(R.string.action_fb_sign_in)
                .setFacebookLoginPermissions(Arrays.asList("email", "public_profile"))
                .setTwitterLoginEnabled(false)
                .setAppLogo(R.drawable.ccss_logo_trans)
                .build();
    }
}
