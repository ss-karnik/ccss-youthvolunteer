package com.ccss.youthvolunteer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectableResourceListAdapter;
import com.ccss.youthvolunteer.model.Organization;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DividerItemDecoration;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.w3c.dom.Text;

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
        implements RecyclerView.OnItemTouchListener, View.OnClickListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    protected Constants.LayoutManagerType mCurrentLayoutManagerType;

    private List<ResourceModel> mResources = Lists.newArrayList();
    private RecyclerView mRecyclerView;
    private TextView mEmptyListMessage;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView.LayoutManager mLayoutManager;
    private SelectableResourceListAdapter mAdapter;
    private ProgressBar mProgressBar;
    GestureDetectorCompat gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        mRecyclerView = (RecyclerView) findViewById(R.id.opportunity_list);
        mEmptyListMessage = (TextView) findViewById(R.id.empty_opportunity_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mLayoutManager = new LinearLayoutManager(this);
        mProgressBar = (ProgressBar) findViewById(R.id.opportunity_list_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mAdapter = new SelectableResourceListAdapter(mResources);
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (Constants.LayoutManagerType) savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnItemTouchListener(this);
        VolunteerOpportunity.getAllOpportunities(findOpportunitiesCallback());
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

        // TODO: If exposing deep links into your app, handle intents here.
    }

    private void refreshDataItems() {
        mResources.clear();
        VolunteerOpportunity.getAllOpportunities(findOpportunitiesCallback());
        mSwipeRefreshLayout.setRefreshing(false);
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
        return new FindCallback<VolunteerOpportunity>() {
            @Override
            public void done(List<VolunteerOpportunity> objects, ParseException e) {
                if (e == null) {
                    if(objects.isEmpty()){
                        mEmptyListMessage.setText(R.string.no_opportunities_available);
                        mEmptyListMessage.setVisibility(View.VISIBLE);
                    } else {
                        for (VolunteerOpportunity opportunity : objects) {
                            mResources.add(opportunity.convertToResourceModel());
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        };
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
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

    private void startDetailActivity(String objectId) {
        Intent intent = new Intent(this, OpportunityDetailActivity.class);
        intent.putExtra(Constants.MANAGE_ITEM_KEY, Constants.OPPORTUNITY_RESOURCE);
        intent.putExtra(Constants.OBJECT_ID_KEY, objectId);
        intent.putExtra(Constants.USER_ORGANIZATION_KEY, "");
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
