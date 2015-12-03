package com.ccss.youthvolunteer.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.RequestTask;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.common.base.Strings;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    protected void startActivity(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    protected void startActivity(Class<? extends Activity> targetActivity, String extraIntent) {
        Intent intent = new Intent(this, targetActivity);
        intent.putExtra(Constants.MANAGE_ITEM_KEY, extraIntent);
        startActivity(intent);
    }

    protected void startManageResourceActivity(String resourceName, String organizationName){
        Intent manageIntent = new Intent(this, ManageResourceActivity.class);
        manageIntent.putExtra(Constants.MANAGE_ITEM_KEY, resourceName);
        manageIntent.putExtra(Constants.USER_ORGANIZATION_KEY, organizationName);
        startActivity(manageIntent);
    }

    protected void replaceActivity(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void addOnClickListener(int btnId, View.OnClickListener listener) {
        Button mEmailSignInButton = (Button) findViewById(btnId);
        mEmailSignInButton.setOnClickListener(listener);
    }

    protected String getValue(int controlId) {
        View control = findViewById(controlId);
        if (control instanceof EditText)
            return ((EditText)control).getText().toString();
        else if (control instanceof TextView)
            ((TextView)control).getText().toString();
        return null;
    }

    protected void setText(int controlId, String text) {
        View control = findViewById(controlId);
        if (control instanceof EditText)
            ((EditText)control).setText(text);
        else if (control instanceof TextView)
            ((TextView)control).setText(text);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        if ((progressDialog !=null) && show) {
            progressDialog.dismiss();
        }
        if (show) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getApplicationContext().getText(R.string.msg_progress_wait));
            progressDialog.show();
        }
        else {
            progressDialog.dismiss();
        }
    }

    protected void showError(int errorMsgId) {
        showError(getApplicationContext().getText(errorMsgId));
    }

    protected void showError(int errorMsgId,DialogInterface.OnClickListener listener) {
        showError(getApplicationContext().getText(errorMsgId), listener);
    }

    protected void showError(CharSequence errorMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(errorMsg)
                .setTitle(R.string.dialog_title_error);
        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }


    protected void showError(CharSequence errorMsg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(errorMsg)
                .setTitle(R.string.dialog_title_error);
        builder.setNeutralButton(R.string.ok, listener);
        builder.setCancelable(false);
        builder.create().show();
    }

    protected void startCallActivity(String phoneNumber) {
        if (!Strings.isNullOrEmpty(phoneNumber)) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            startActivity(callIntent);
        }
    }

    protected void saveInBackground(ParseObject parseObject, final boolean shouldClose) {
        showProgress(true);
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                showProgress(false);
                if (e == null) {
                    Toast.makeText(getBaseContext(), R.string.msg_save_sucess, Toast.LENGTH_LONG).show();
                    if (shouldClose) {
                        finish();
                    }
                } else {
                    showError(e.getLocalizedMessage());
                }
            }
        });
    }

    protected void deleteInBackground(ParseObject parseObject, final boolean shouldClose) {
        showProgress(true);
        parseObject.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                showProgress(false);
                if (e == null) {
                    Toast.makeText(getBaseContext(), R.string.msg_delete_sucess, Toast.LENGTH_LONG).show();
                    if (shouldClose) {
                        finish();
                    }
                } else {
                    showError(e.getMessage());
                }
            }
        });
    }

    protected void startPlaceSearch() {
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            showError(e.getLocalizedMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            showError(e.getLocalizedMessage());
        }
    }

    protected void executeAndWait(Object request, RequestTask requestTask) {
        showProgress(true);
        try {
            requestTask.execute(request).get();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getLocalizedMessage(), e);
        }
        showProgress(false);
    }

    protected void validate(int controlId) throws Exception {
        EditText view = (EditText) findViewById(controlId);
        if (TextUtils.isEmpty(view.getText().toString())) {
            view.setError(getString(R.string.error_field_required));
            view.requestFocus();
            throw new Exception("Input is invalid");
        }
    }

    protected void showToast(int id) {
        showToast(getString(id));
    }

    protected void showToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected void showToastLong(int id) {
        showToastLong(getString(id));
    }

    protected void showToastLong(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows the progress UI and hides the form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show, final View mainView, final View progressView) {
        // The ViewPropertyAnimator APIs are not available, so simply show
        // and hide the relevant UI components.
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mainView.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_view_home: {
                if (!(this instanceof MainActivity)) {
                    startActivity(MainActivity.class);
                    finish();
                }
                break;
            }

            case R.id.nav_view_volunteer: {
                if (!(this instanceof VolunteerOpportunityActivity)) {
                    startActivity(VolunteerOpportunityActivity.class);
                    finish();
                }
                break;
            }

            case R.id.nav_places: {
                if (!(this instanceof MapActivity)) {
                    Intent i = new Intent(this, MapActivity.class);
                    startActivityForResult(i, 0);
                }
                break;
            }

            case R.id.nav_view_ranking: {
                if (!(this instanceof RankingActivity)) {
                    startActivity(RankingActivity.class);
                }
                break;
            }
            case R.id.nav_view_achievement: {
                if (!(this instanceof VolunteerLogActivity)) {
                    startActivity(VolunteerLogActivity.class);
                }
                break;
            }
            case R.id.nav_edit_profile: {
                if (!(this instanceof ProfileActivitySimple)) {
                    startActivity(ProfileActivitySimple.class);
                }
                break;
            }

            case R.id.nav_publish: {
                if (!(this instanceof PublishActivity)) {
                    startActivity(PublishActivity.class);
                }
                break;
            }

            case R.id.action_logout: {
                ParseUser.logOut();
                finish();
                break;
            }

            case R.id.nav_about: {
                if (!(this instanceof AboutActivity)) {
                    startActivity(AboutActivity.class);
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    //region Image Selection section

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent,  getResources().getString(R.string.prompt_select_source));

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }


    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }
    //endregion
}