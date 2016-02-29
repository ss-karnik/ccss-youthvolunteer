package com.ccss.youthvolunteer.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectableResourceListAdapter;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.model.Skill;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.DividerItemDecoration;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Skill fragment containing a recycler view.
 */
public class OpportunityInterestedUsersFragment extends Fragment implements RecyclerView.OnItemTouchListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String INTERESTED_USERS_FRAG_TAG = "InterestedUsersViewFragment";
    private static final String INTERESTED_USERS_TAG = "OpportunityInterestedUsers";
    private static final String READ_ONLY_TAG = "ReadOnly";

    private SelectableResourceListAdapter mUsersAdapter;
    private GestureDetectorCompat mGestureDetector;
    protected RecyclerView mUsersRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private List<ResourceModel> mInterestedUsers = Lists.newArrayList();
    private List<String> mSelectedEmails = Lists.newArrayList();

    OnInterestSelectedListener mCallback;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OpportunityInterestedUsersFragment newInstance(int sectionNumber, List<ParseUser> users, boolean readOnly) {
        OpportunityInterestedUsersFragment fragment = new OpportunityInterestedUsersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(INTERESTED_USERS_TAG, Lists.newArrayList(users));
        args.putBoolean(READ_ONLY_TAG, readOnly);
        fragment.setArguments(args);
        return fragment;
    }

    public OpportunityInterestedUsersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean mReadOnly = getArguments().getBoolean(READ_ONLY_TAG);
        mUsersAdapter = new SelectableResourceListAdapter(mInterestedUsers, !mReadOnly);
        List<ParseUser> users = (List<ParseUser>) getArguments().getSerializable(INTERESTED_USERS_TAG);
        if(users != null && !users.isEmpty()){
            for(ParseUser user : users){
                mInterestedUsers.add(VolunteerUser.convertToResourceModel(user));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.opportunity_interested_users_fragment, container, false);
        rootView.setTag(INTERESTED_USERS_FRAG_TAG);
        mUsersRecyclerView = (RecyclerView) rootView.findViewById(R.id.interested_user_list);
        mUsersRecyclerView.setHasFixedSize(true);
        mUsersRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mLayoutManager = new LinearLayoutManager(getActivity());

        setRecyclerViewLayoutManager();

        mUsersRecyclerView.setAdapter(mUsersAdapter);
        mUsersRecyclerView.addOnItemTouchListener(this);
        mGestureDetector = new GestureDetectorCompat(this.getActivity(), new SkillsGestureListener());

        ImageView navigateDetails = (ImageView) rootView.findViewById(R.id.users_to_details_section);
        navigateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OpportunityDetailActivity) getActivity()).onNavigateToDetailsClick(null);
            }
        });

        ImageView navigateUsers = (ImageView) rootView.findViewById(R.id.go_to_participated_section);
        navigateUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OpportunityDetailActivity) getActivity()).onNavigateToVolunteerUsersClick();
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     */
    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mUsersRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mUsersRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mUsersRecyclerView.setLayoutManager(mLayoutManager);
        mUsersRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    // Container Activity must implement this interface
    public interface OnInterestSelectedListener {
        public void onInterestSelected(int position);
    }

    private class SkillsGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = mUsersRecyclerView.findChildViewUnder(e.getX(), e.getY());
            //onClick(view);
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            View view = mUsersRecyclerView.findChildViewUnder(e.getX(), e.getY());
//            if (actionMode != null) {
//                return;
//            }
//            // Start the CAB using the ActionMode.Callback defined above
//            actionMode = startActionMode(RecyclerViewDemoActivity.this);
//            int idx = recyclerView.getChildPosition(view);
//            myToggleSelection(idx);
            super.onLongPress(e);
        }
    }
}
