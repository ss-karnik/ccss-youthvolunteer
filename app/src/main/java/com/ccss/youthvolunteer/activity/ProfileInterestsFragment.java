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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectableResourceListAdapter;
import com.ccss.youthvolunteer.model.Interest;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.DividerItemDecoration;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

/**
 * Interest fragment containing a recycler view.
 */
public class ProfileInterestsFragment extends Fragment implements ProfileActivity.ValidateFragment, RecyclerView.OnItemTouchListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String INTERESTS_FRAG_TAG = "InterestsViewFragment";
    private static final String INTERESTS_TAG = "UserInterests";
    private SelectableResourceListAdapter mInterestsAdapter;
    private GestureDetectorCompat mGestureDetector;
    protected RecyclerView mInterestsRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private List<ResourceModel> mInterests = Lists.newArrayList();
    private List<ResourceModel> mUserInterests = Lists.newArrayList();
    private List<Interest> mUserSelectedInterests = Lists.newArrayList();

    OnInterestSelectedListener mCallback;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileInterestsFragment newInstance(int sectionNumber, VolunteerUser user) {
        ProfileInterestsFragment fragment = new ProfileInterestsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(INTERESTS_TAG, Lists.newArrayList(user.getUserInterests()));

        fragment.setArguments(args);
        return fragment;
    }

    public ProfileInterestsFragment() {
    }

    private void loadInterests() {
        if(mInterests.size() <= 0)
            Interest.findInBackground(new FindCallback<Interest>() {
                @Override
                public void done(List<Interest> objects, ParseException e) {
                    if (e == null) {
                        for (Interest interest : objects) {
                            ResourceModel converted = interest.convertToResourceModel();
                            converted.setSelected(mUserInterests.contains(converted));
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
        loadInterests();
        List<Interest> userInterests = (List<Interest>) getArguments().getSerializable(INTERESTS_TAG);
        if(userInterests != null){
            for(Interest userInterest : userInterests){
                mUserInterests.add(userInterest.convertToResourceModel());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.profile_interests_fragment, container, false);
        rootView.setTag(INTERESTS_FRAG_TAG);
        mInterestsRecyclerView = (RecyclerView) rootView.findViewById(R.id.interests_list);
        mInterestsRecyclerView.setHasFixedSize(true);
        mInterestsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mLayoutManager = new LinearLayoutManager(getActivity());
        //loadInterests();

        for(ResourceModel interest : mInterests){
            interest.setSelected(mUserInterests.contains(interest));
        }
        mInterestsAdapter = new SelectableResourceListAdapter(mInterests);
        setInterestsRecyclerViewLayoutManager();

        mInterestsRecyclerView.addOnItemTouchListener(this);
        mGestureDetector = new GestureDetectorCompat(this.getActivity(), new InterestsGestureListener());
        mInterestsRecyclerView.setAdapter(mInterestsAdapter);

        ImageView navigateSkills = (ImageView) rootView.findViewById(R.id.go_to_skills_section);
        navigateSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    ((ProfileActivity) getActivity()).onNavigateToSkillsClick(mUserSelectedInterests);
                } else {
                    Toast.makeText(getActivity(), R.string.select_interest_message, Toast.LENGTH_LONG).show();
                }
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

    @Override
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
