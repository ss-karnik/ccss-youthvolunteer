package com.ccss.youthvolunteer;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.ccss.youthvolunteer.model.*;
import com.ccss.youthvolunteer.util.Constants;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;

public class YouthVolunteerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        ParseUser.registerSubclass(VolunteerUser.class);

        ParseObject.registerSubclass(Announcement.class);
        ParseObject.registerSubclass(VolunteerOpportunity.class);
        ParseObject.registerSubclass(Category.class);
        ParseObject.registerSubclass(Level.class);
        ParseObject.registerSubclass(Recognition.class);
        ParseObject.registerSubclass(UserAction.class);
        ParseObject.registerSubclass(UserCategoryPoints.class);
        ParseObject.registerSubclass(Theme.class);
        ParseObject.registerSubclass(Interest.class);
        ParseObject.registerSubclass(Skill.class);
        ParseObject.registerSubclass(SpecialUser.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(School.class);
        ParseObject.registerSubclass(Organization.class);
        ParseObject.registerSubclass(UserRecognition.class);

        // Add your initialization code here
        Parse.initialize(this, getString(com.ccss.youthvolunteer.R.string.parse_app_id), getString(R.string.parse_client_key));
        //need this minimally
        ParseFacebookUtils.initialize(this);

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseUser.enableRevocableSessionInBackground();

        //FacebookSdk.sdkInitialize(getApplicationContext());

        //VolunteerUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground(Constants.OPPORTUNITY_BROADCAST_CHANNEL);
        subscribeToLocationUpdates();
    }

    public void subscribeToLocationUpdates() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, locationListener);
    }

    private void makeUseOfNewLocation(Location location) {
       // Toast.makeText(getBaseContext(), R.string.msg_current_location + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null)) {
            currentUser.put(Constants.LAST_KNOWN_LOCATION, location.getLatitude() + "," + location.getLongitude());
            currentUser.saveInBackground();
        }
    }
}