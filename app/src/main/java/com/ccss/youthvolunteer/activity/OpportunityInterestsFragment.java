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
import android.widget.Toast;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectableResourceListAdapter;
import com.ccss.youthvolunteer.model.Interest;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DividerItemDecoration;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

/**
 * Interest fragment containing a recycler view.
 */
public class OpportunityInterestsFragment extends Fragment implements RecyclerView.OnItemTouchListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String INTERESTS_FRAG_TAG = "InterestsViewFragment";
    private static final String INTERESTS_TAG = "OpportunityInterests";
    private static final String READ_ONLY_TAG = "ReadOnly";
    private SelectableResourceListAdapter mInterestsAdapter;
    private GestureDetectorCompat mGestureDetector;
    protected RecyclerView mInterestsRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private List<ResourceModel> mInterests = Lists.newArrayList();
    private List<ResourceModel> mOpportunityInterests = Lists.newArrayList();
    private List<Interest> mUserSelectedInterests = Lists.newArrayList();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OpportunityInterestsFragment newInstance(int sectionNumber, List<Interest> interests, boolean readOnly) {
        OpportunityInterestsFragment fragment = new OpportunityInterestsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(INTERESTS_TAG, Lists.newArrayList(interests));
        args.putBoolean(READ_ONLY_TAG, readOnly);

        fragment.setArguments(args);
        return fragment;
    }

    public OpportunityInterestsFragment() {
    }

    private void loadInterests() {
        if(mInterests.size() <= 0)
            Interest.findInBackground(new FindCallback<Interest>() {
                @Override
                public void done(List<Interest> objects, ParseException e) {
                    if (e == null) {
                        for (Interest interest : objects) {
                            ResourceModel converted = interest.convertToResourceModel();
                            converted.setSelected(mOpportunityInterests.contains(converted));
                            mInterests.add(converted);
                        }
                    }
                    mInterestsAdapter.notifyDataSetChanged();
                }
            });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean mReadOnly = getArguments().getBoolean(READ_ONLY_TAG);
        mInterestsAdapter = new SelectableResourceListAdapter(mInterests, !mReadOnly);
        List<Interest> oppInterests = (List<Interest>) getArguments().getSerializable(INTERESTS_TAG);
        if(oppInterests != null && !oppInterests.isEmpty()){
            for(Interest interest : oppInterests){
                mOpportunityInterests.add(interest.convertToResourceModel());
            }
        }
        loadInterests();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.opportunity_interests_fragment, container, false);
        rootView.setTag(INTERESTS_FRAG_TAG);
        mInterestsRecyclerView = (RecyclerView) rootView.findViewById(R.id.opportunity_interests_list);
        mInterestsRecyclerView.setHasFixedSize(true);
        mInterestsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mLayoutManager = new LinearLayoutManager(getActivity());

        for(ResourceModel interest : mInterests){
            interest.setSelected(mOpportunityInterests.contains(interest));
        }

        setInterestsRecyclerViewLayoutManager();

        mInterestsRecyclerView.addOnItemTouchListener(this);
        mGestureDetector = new GestureDetectorCompat(this.getActivity(), new InterestsGestureListener());
        mInterestsRecyclerView.setAdapter(mInterestsAdapter);

        ImageView navigateSkills = (ImageView) rootView.findViewById(R.id.interests_to_skills_section);
        navigateSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String objectId: mInterestsAdapter.getSelectedItems()) {
                    mUserSelectedInterests.add(ParseObject.createWithoutData(Interest.class, objectId));
                }

                ((OpportunityDetailActivity) getActivity()).onNavigateToSkillsClick(mUserSelectedInterests);
            }
        });

        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     */
    public void setInterestsRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mInterestsRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mInterestsRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mInterestsRecyclerView.setLayoutManager(mLayoutManager);
        mInterestsRecyclerView.scrollToPosition(scrollPosition);
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

    public boolean validate() {
        if (mInterestsAdapter.getSelectedItems().size() > 0){
            for (String objectId: mInterestsAdapter.getSelectedItems()) {
                mUserSelectedInterests.add(ParseObject.createWithoutData(Interest.class, objectId));
            }
            return true;
        } else {
            return false;
        }
    }

    // Container Activity must implement this interface
    public interface OnInterestSelectedListener {
        public void onInterestSelected(int position);
    }

    private class InterestsGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = mInterestsRecyclerView.findChildViewUnder(e.getX(), e.getY());
            //int idx = mInterestsRecyclerView.getChildAdapterPosition(view);
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            View view = mInterestsRecyclerView.findChildViewUnder(e.getX(), e.getY());
            //int idx = mInterestsRecyclerView.getChildAdapterPosition(view);
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
