package com.ccss.youthvolunteer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.VolunteerLogAdapter;
import com.ccss.youthvolunteer.model.UserAction;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * https://developer.android.com/samples/RecyclerView/src/com.example.android.recyclerview/RecyclerViewFragment.html
 */
public class VolunteerLogListFragment extends Fragment {

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final String TAG = "VolunteerLogRecyclerViewFragment";
    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    private List<UserAction> mUserActions = Lists.newArrayList();
    private RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;

    public static VolunteerLogListFragment newInstance() {
        return new VolunteerLogListFragment();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VolunteerLogListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserAction.findAllUserActionsInBackground(ParseUser.getCurrentUser(), new FindCallback<UserAction>() {
            @Override
            public void done(List<UserAction> objects, ParseException e) {
                if (e == null) {
                    mUserActions = objects;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.volunteer_log_list_fragment, container, false);

        layout.setTag(TAG);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.volunteer_action_log_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        ProgressBar mProgressBar = (ProgressBar) layout.findViewById(R.id.volunteer_action_log_list_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager();

        VolunteerLogAdapter mAdapter = new VolunteerLogAdapter(mUserActions);
        mRecyclerView.setAdapter(mAdapter);

        mProgressBar.setVisibility(View.GONE);

        TextView summaryText = (TextView) layout.findViewById(R.id.log_action_summary);
        summaryText.setText(String.format("Actions performed: %s. Total points: %s", mUserActions.size(), calculateTotalPoints()));

        return layout;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     */
    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    private int calculateTotalPoints() {
        int totalPoints = 0;
        for(UserAction action : mUserActions){
            totalPoints += action.getAction().getActionPoints();
        }
        return totalPoints;
    }

}
