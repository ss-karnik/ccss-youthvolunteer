package com.ccss.youthvolunteer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.GenericListAdapter;
import com.ccss.youthvolunteer.handler.ItemActionHandler;
import com.ccss.youthvolunteer.model.PlaceInfo;
import com.ccss.youthvolunteer.model.PlacesResponse;
import com.ccss.youthvolunteer.model.UserRole;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.ListItem;
import com.ccss.youthvolunteer.util.PlaceItemConverter;
import com.ccss.youthvolunteer.util.RequestTask;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.parse.ParseUser;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListPlacesActivity extends BaseActivity implements AdapterView.OnItemClickListener  {

    public static int SEARCH_RADIUS = 1000;
    public static String PLACE_TYPES = "hospital|elderly";
    private static final String TAG = "ListActivity";
    private String locationAddress;
    private String locationLat;
    private String locationLng;
    private GenericListAdapter placesListAdapter;
    private ListView listView;
    private RequestTask<Void,PlacesResponse> searchTask = null;
    private List<PlaceInfo> placesList = Lists.newArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);
        listView = (ListView) findViewById(R.id.listView);
        ParseUser currentUser = ParseUser.getCurrentUser();
        String lastKnowLocation = (String) currentUser.get(Constants.LAST_KNOWN_LOCATION);
        if (lastKnowLocation !=null ) {
            String[] latLong = lastKnowLocation.split(",");
            locationLat = latLong[0];
            locationLng = latLong[1];
        }
        else {
            Toast.makeText(getBaseContext(),R.string.msg_no_current_location,Toast.LENGTH_LONG).show();
        }
        placesListAdapter = new GenericListAdapter(this, R.layout.place_list_item,
                Lists.transform(placesList, PlaceItemConverter.fromPlaceToListItem()), false);
        listView.setAdapter(placesListAdapter);
        listView.setOnItemClickListener(this);
        listPlaces();
    }

    private void listPlaces() {
        placesList.clear();
        placesListAdapter.notifyDataSetChanged();
        final String url = getURL(locationLat, locationLng);
        searchTask = getSearchTask(url);
        showProgress(true);
        try {
            PlacesResponse placesResponse = searchTask.execute(null, null).get();
            int pageCount=0;
            while(pageCount++<10 && placesResponse!=null && placesResponse.next_page_token!=null) {
                Toast.makeText(getBaseContext(),"Retrieving page"+pageCount,Toast.LENGTH_SHORT).show();
                searchTask = getSearchTask(getURL(placesResponse.next_page_token));
                placesResponse = searchTask.execute(null, null).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Places Activity", e.getMessage());
        }
        showProgress(false);
    }

    @NonNull
    private RequestTask<Void, PlacesResponse> getSearchTask(final String url) {
        return new RequestTask<Void, PlacesResponse>(url, PlacesResponse.class) {

            @Override
            protected void onSuccess(PlacesResponse response) {
                List<PlaceInfo> results = response.results;
                if ((response.results != null) && !response.results.isEmpty()) {
                    placesList.addAll(Lists.newArrayList(Iterables.filter(results, new Predicate<PlaceInfo>() {
                        @Override
                        public boolean apply(PlaceInfo input) {
                            return input.scope.equals("APP") || Iterables.tryFind(input.types, new Predicate<String>() {
                                @Override
                                public boolean apply(String input) {
                                    return PLACE_TYPES.contains(input);
                                }
                            }).isPresent();
                        }
                    })));
                    placesListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getBaseContext(), R.string.msg_no_results_found, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected void onError(Exception ex) {
                Log.e(TAG, "Could not fetch data from google " + url + "," + ex.getMessage(), ex);
                showError(ex.getLocalizedMessage());
            }

        };
    }

    private String getURL(String locationLat, String locationLng) {
        return String.format(Constants.GOOGLE_PLACE_API_URL + "nearbysearch/json?location=%s,%s&radius=%s&types=%s&key=%s&language=%s",
                locationLat,
                locationLng,
                SEARCH_RADIUS,
                PLACE_TYPES+"|other",
                Constants.GOOGLE_API_KEY,
                getBaseContext().getResources().getConfiguration().locale.getLanguage());
    }

    private String getURL(String nextPageToken) {
        return String.format(Constants.GOOGLE_PLACE_API_URL + "nearbysearch/json?pagetoken=%s&key=%s&language=%s",
                nextPageToken,
                Constants.GOOGLE_API_KEY,
                getBaseContext().getResources().getConfiguration().locale.getLanguage());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_places, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem addItem= menu.findItem(R.id.action_add);
        addItem.setVisible(UserRole.isUserModerator());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startPlaceSearch();
                break;
//            case R.id.action_add:
//                startManageActivityWithIntent(EditPlaceActivity.class);
//                break;

            default:
                break;
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                locationAddress  =  place.getAddress().toString();
                locationLat =  Double.toString(place.getLatLng().latitude);
                locationLng =  Double.toString(place.getLatLng().longitude);
                listPlaces();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position!=-1) {
            PlaceInfo placeInfo = placesList.get(position);
            Intent intent = new Intent(this, DisplayPlaceDetailsActivity.class);
            intent.putExtra(Constants.PLACE_ID, placeInfo.placeId);
            startActivity(intent);
        }
    }
}