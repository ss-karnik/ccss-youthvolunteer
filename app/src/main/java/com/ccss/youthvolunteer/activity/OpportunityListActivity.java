package com.ccss.youthvolunteer.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectableResourceListAdapter;
import com.ccss.youthvolunteer.adapter.SelectorHintAdapter;
import com.ccss.youthvolunteer.model.Category;
import com.ccss.youthvolunteer.model.Organization;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DateUtils;
import com.ccss.youthvolunteer.util.DividerItemDecoration;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An activity representing a list of Opportunities. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link OpportunityDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link OpportunityListFragment} and the item details
 * (if present) is a {@link OpportunityDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link OpportunityListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class OpportunityListActivity extends BaseActivity
        implements RecyclerView.OnItemTouchListener, View.OnClickListener, SearchView.OnQueryTextListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    protected Constants.LayoutManagerType mCurrentLayoutManagerType;

    private List<ResourceModel> mUnfilteredOpportunities = Lists.newArrayList();
    private List<ResourceModel> mDisplayedOpportunities = Lists.newArrayList();
    private RecyclerView mRecyclerView;
    private TextView mEmptyListMessage;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView.LayoutManager mLayoutManager;
    private SelectableResourceListAdapter mAdapter;
    private ProgressBar mProgressBar;
    GestureDetectorCompat gestureDetector;
    private Spinner mOrganizationSpinner;
    private Spinner mCategorySpinner;
    private RelativeLayout mSwitchIndicatorLayout;

    private String mUserOrganization;
    private List<String> mOrganizations;
    private List<String> mCategories;
    private String mAccessMode;
    private boolean mReadOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        setTitle(getResources().getText(R.string.title_opportunity_list));

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mDisplayedOpportunities = filterOpportunities(query);
            mAdapter.notifyDataSetChanged();
        } else {
            mUserOrganization = intent.getStringExtra(Constants.USER_ORGANIZATION_KEY);
            mAccessMode = intent.getStringExtra(Constants.ACCESS_MODE_KEY);
        }

        mReadOnly = Constants.READ_MODE.equals(mAccessMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.opportunity_list_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_opportunity_list);

        // Show the Up button in the action bar.
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.opportunities_container);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataItems();
            }
        });

        mSwitchIndicatorLayout = (RelativeLayout) findViewById(R.id.opportunities_type_switch);
        mRecyclerView = (RecyclerView) findViewById(R.id.opportunity_list);
        mEmptyListMessage = (TextView) findViewById(R.id.empty_opportunity_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mLayoutManager = new LinearLayoutManager(this);
        mProgressBar = (ProgressBar) findViewById(R.id.opportunity_list_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mAdapter = new SelectableResourceListAdapter(mDisplayedOpportunities, !mReadOnly);
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (Constants.LayoutManagerType) savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnItemTouchListener(this);

        if(mReadOnly){
            VolunteerOpportunity.getActiveOpportunities(findOpportunitiesCallback());
        } else if(mUserOrganization.isEmpty()) {
            VolunteerOpportunity.getAllOpportunities(findOpportunitiesCallback());
        } else {
            VolunteerOpportunity.getOpportunitiesForOrganization(mUserOrganization, false, findOpportunitiesCallback());
        }

        mAdapter.setOnItemClickListener(new ResourcesFragment.RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String resourceItemType, String resourceObjectId) {
                startDetailActivity(resourceObjectId);
            }

            @Override
            public void onItemLongClick(int position, View view) {
                int idx = mRecyclerView.getChildAdapterPosition(view);
                ResourceModel model = mAdapter.getItem(position);
                startDetailActivity(model.getObjectId());
            }
        });
        gestureDetector = new GestureDetectorCompat(this, new RecyclerViewGestureListener());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.opportunity_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDetailActivity("");
            }
        });
        fab.setVisibility(mReadOnly ? View.INVISIBLE : View.VISIBLE);

//        if (findViewById(R.id.opportunity_detail_container) != null) {
//            // The detail container view will be present only in the
//            // large-screen layouts (res/values-large and
//            // res/values-sw600dp). If this view is present, then the
//            // activity should be in two-pane mode.
//            mTwoPane = true;
//
//            // In two-pane mode, list items should be given the
//            // 'activated' state when touched.
//            ((OpportunityListFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.opportunity_list))
//                    .setActivateOnItemClick(true);
//        }

    }

    private void enableSwitchView(){
        Switch switchView = (Switch) findViewById(R.id.viewSwitch);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Show virtual
                    mDisplayedOpportunities = Lists.newArrayList(Iterables.filter(mUnfilteredOpportunities, new Predicate<ResourceModel>() {
                        @Override
                        public boolean apply(ResourceModel input) {
                            //ExtraInfoTopRight is the date for the VolunteerOpportunity
                            return Constants.VIRTUAL.equals(input.getExtraInformationTopRight());
                        }
                    }));

                } else {
                    //Show physical
                    mDisplayedOpportunities = Lists.newArrayList(Iterables.filter(mUnfilteredOpportunities, new Predicate<ResourceModel>() {
                        @Override
                        public boolean apply(ResourceModel input) {
                            //ExtraInfoTopRight is the date for the VolunteerOpportunity
                            if(mReadOnly){
                                return !Constants.VIRTUAL.equals(input.getExtraInformationTopRight())
                                        && DateUtils.stringToLocalDate(input.getExtraInformationTopRight()).isAfter(LocalDate.now().minusDays(1));
                            }
                            return !Constants.VIRTUAL.equals(input.getExtraInformationTopRight());
                        }
                    }));
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private boolean isVirtualOrUpcoming(ResourceModel model){
        return Constants.VIRTUAL.equals(model.getExtraInformationTopRight())
                || DateUtils.stringToLocalDate(model.getExtraInformationTopRight()).isAfter(LocalDate.now().minusDays(1));
    }

    private boolean isVirtualOrPast(ResourceModel input) {
        return Constants.VIRTUAL.equals(input.getExtraInformationTopRight())
                || DateUtils.stringToLocalDate(input.getExtraInformationTopRight()).isBefore(LocalDate.now().minusDays(1));
    }


    private void filterByOrganization(final String organizationName) {
        mDisplayedOpportunities.clear();
        mDisplayedOpportunities = Lists.newArrayList(Iterables.filter(mUnfilteredOpportunities, new Predicate<ResourceModel>() {
            @Override
            public boolean apply(ResourceModel input) {
                if(mReadOnly){
                    return ("By: " + organizationName).equals(input.getExtraInformationBelowDesc()) && isVirtualOrUpcoming(input);
                }
                return ("By: " + organizationName).equals(input.getExtraInformationBelowDesc());
            }
        }));
    }

    private void filterByCategory(final String categoryName) {
        mDisplayedOpportunities.clear();
        mDisplayedOpportunities = Lists.newArrayList(Iterables.filter(mUnfilteredOpportunities, new Predicate<ResourceModel>() {
            @Override
            public boolean apply(ResourceModel input) {
                if(mReadOnly){
                    return (Constants.OPPORTUNITY_RESOURCE.concat("|" + categoryName)).equals(input.getResourceType())
                            && isVirtualOrUpcoming(input);
                }
                return (Constants.OPPORTUNITY_RESOURCE.concat("|" + categoryName)).equals(input.getResourceType());
            }
        }));
    }

    private void refreshDataItems() {
        mUnfilteredOpportunities.clear();
        VolunteerOpportunity.getAllOpportunities(findOpportunitiesCallback());

    }

    private void populateOrganizationsSpinner(){
        mOrganizationSpinner = (Spinner) findViewById(R.id.opportunities_org);
        mOrganizationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterByOrganization(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(mOrganizations.isEmpty()){
            mOrganizations = Organization.getAllActiveOrganizationNames();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mOrganizations);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOrganizationSpinner.setPromptId(R.string.org_input_hint);
        mOrganizationSpinner.setAdapter(new SelectorHintAdapter(dataAdapter, R.layout.org_selector_hint, this));
    }

    private void populateCategoriesSpinner(){
        mCategorySpinner = (Spinner) findViewById(R.id.opportunities_category);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterByCategory(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(mCategories.isEmpty()){
            mCategories = Category.getAllCategoryNames();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mCategories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setPromptId(R.string.category_input_hint);
        mCategorySpinner.setAdapter(new SelectorHintAdapter(dataAdapter, R.layout.category_selector_hint_row, this));
    }

    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @NonNull
    private FindCallback<VolunteerOpportunity> findOpportunitiesCallback() {
        final boolean readOnly = Constants.READ_MODE.equals(mAccessMode);

        final List<String> interestedOpportunityIds = Lists.newArrayList();
        if(readOnly) {
            VolunteerOpportunity.getInterestedOpportunitiesForUser(ParseUser.getCurrentUser(), new FindCallback<VolunteerOpportunity>() {
                @Override
                public void done(List<VolunteerOpportunity> objects, ParseException e) {
                    if (e == null) {
                        for (VolunteerOpportunity opportunity : objects) {
                            interestedOpportunityIds.add(opportunity.getObjectId());
                        }
                    }
                }
            });
        }

        return new FindCallback<VolunteerOpportunity>() {
            @Override
            public void done(List<VolunteerOpportunity> objects, ParseException e) {
                if (e == null) {
                    if(objects.isEmpty()){
                        mEmptyListMessage.setText(R.string.no_opportunities_available);
                        mEmptyListMessage.setVisibility(View.VISIBLE);
                    } else {
                        for (VolunteerOpportunity opportunity : objects) {
                            ResourceModel convertedModel = opportunity.convertToResourceModel();
                            convertedModel.setStarred(interestedOpportunityIds.contains(opportunity.getObjectId()));
                            mUnfilteredOpportunities.add(convertedModel);

                            mDisplayedOpportunities.clear();
                            if(readOnly){
                                //READ mode show all active upcoming opportunities
                                if(opportunity.isVirtual()  || opportunity.getActionStartDate().after(LocalDate.now().minusDays(1).toDate())){
                                    mDisplayedOpportunities.add(convertedModel);
                                }
                            } else {
                                //Write mode show only userorg opportunities
                                if(mUserOrganization.isEmpty()){
                                    mDisplayedOpportunities.add(convertedModel);
                                } else if(("By: ".concat(mUserOrganization)).equals(convertedModel.getExtraInformationBelowDesc())) {
                                    mDisplayedOpportunities.add(convertedModel);
                                }
                            }
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_by_category:
                mOrganizationSpinner.setVisibility(View.GONE);
                mCategorySpinner.setVisibility(View.VISIBLE);
                mSwitchIndicatorLayout.setVisibility(View.GONE);
                populateCategoriesSpinner();
                break;

            case R.id.activity_by_org:
                mOrganizationSpinner.setVisibility(View.VISIBLE);
                mCategorySpinner.setVisibility(View.GONE);
                mSwitchIndicatorLayout.setVisibility(View.GONE);
                populateOrganizationsSpinner();
                break;

            case R.id.activity_by_type:
                mOrganizationSpinner.setVisibility(View.GONE);
                mCategorySpinner.setVisibility(View.GONE);
                mSwitchIndicatorLayout.setVisibility(View.VISIBLE);
                enableSwitchView();
                mDisplayedOpportunities.clear();
                mDisplayedOpportunities = Lists.newArrayList(Iterables.filter(mUnfilteredOpportunities, new Predicate<ResourceModel>() {
                    @Override
                    public boolean apply(ResourceModel input) {
                        return !Constants.VIRTUAL.equals(input.getExtraInformationTopRight());
                    }
                }));
                mAdapter.notifyDataSetChanged();
                break;

            case R.id.past_activities:
                mOrganizationSpinner.setVisibility(View.GONE);
                mCategorySpinner.setVisibility(View.GONE);
                mSwitchIndicatorLayout.setVisibility(View.GONE);
                mDisplayedOpportunities.clear();
                mDisplayedOpportunities = Lists.newArrayList(Iterables.filter(mUnfilteredOpportunities, new Predicate<ResourceModel>() {
                    @Override
                    public boolean apply(ResourceModel input) {
                        return isVirtualOrPast(input);
                    }
                }));
                break;

            case R.id.upcoming_activities:
                mOrganizationSpinner.setVisibility(View.GONE);
                mCategorySpinner.setVisibility(View.GONE);
                mSwitchIndicatorLayout.setVisibility(View.GONE);
                mDisplayedOpportunities.clear();
                mDisplayedOpportunities = Lists.newArrayList(Iterables.filter(mUnfilteredOpportunities, new Predicate<ResourceModel>() {
                    @Override
                    public boolean apply(ResourceModel input) {
                        return isVirtualOrUpcoming(input);
                    }
                }));
                break;
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.resource_view) {
            // item click
            int idx = mRecyclerView.getChildAdapterPosition(view);
            ResourceModel model = mAdapter.getItem(idx);
            startDetailActivity( model.getObjectId());
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opportunities, menu);

        final MenuItem item = menu.findItem(R.id.activity_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement our filter logic
        final List<ResourceModel> filteredModelList = filterOpportunities(query);
        mAdapter.animateTo(filteredModelList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mDisplayedOpportunities = filterOpportunities(query);
        mAdapter.notifyDataSetChanged();
        return true;
    }

    private List<ResourceModel> filterOpportunities(final String query) {
        final String queryString = query.toLowerCase();

        return Lists.newArrayList(Iterables.filter(mUnfilteredOpportunities, new Predicate<ResourceModel>() {
            @Override
            public boolean apply(ResourceModel input) {
                return input.getTitle().toLowerCase().contains(queryString);
            }
        }));
    }

    private void startDetailActivity(String objectId) {
        Intent intent = new Intent(this, OpportunityDetailActivity.class);
        intent.putExtra(Constants.MANAGE_ITEM_KEY, Constants.OPPORTUNITY_RESOURCE);
        intent.putExtra(Constants.OBJECT_ID_KEY, objectId);
        intent.putExtra(Constants.USER_ORGANIZATION_KEY, mUserOrganization);
        intent.putExtra(Constants.ACCESS_MODE_KEY, mAccessMode);
        startActivity(intent);
    }

    private class RecyclerViewGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            onClick(view);
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
//            if (actionMode != null) {
//                return;
//            }
            // Start the CAB using the ActionMode.Callback defined above
//            actionMode = getActivity().startActionMode(ResourcesFragment.this);
//            int idx = mRecyclerView.getChildAdapterPosition(view);
//            myToggleSelection(idx);
            super.onLongPress(e);
        }
    }

//    /**
//     * Callback method from {@link OpportunityListFragment.Callbacks}
//     * indicating that the item with the given ID was selected.
//     */
//    @Override
//    public void onItemSelected(String id) {
//        if (mTwoPane) {
//            // In two-pane mode, show the detail view in this activity by
//            // adding or replacing the detail fragment using a
//            // fragment transaction.
//            Bundle arguments = new Bundle();
//            arguments.putString(OpportunityDetailFragment.ARG_ITEM_ID, id);
//            OpportunityDetailFragment fragment = new OpportunityDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.opportunity_detail_container, fragment)
//                    .commit();
//
//        } else {
//            // In single-pane mode, simply start the detail activity
//            // for the selected item ID.
//            Intent detailIntent = new Intent(this, OpportunityDetailActivity.class);
//            detailIntent.putExtra(OpportunityDetailFragment.ARG_ITEM_ID, id);
//            startActivity(detailIntent);
//        }
//    }


}
