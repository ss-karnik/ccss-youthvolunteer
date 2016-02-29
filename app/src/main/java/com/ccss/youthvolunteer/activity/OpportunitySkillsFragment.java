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
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.model.Skill;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.DividerItemDecoration;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

/**
 * Skill fragment containing a recycler view.
 */
public class OpportunitySkillsFragment extends Fragment implements RecyclerView.OnItemTouchListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String SKILLS_FRAG_TAG = "SkillsViewFragment";
    private static final String SKILLS_TAG = "OpportunitySkills";
    private static final String READ_ONLY_TAG = "ReadOnly";
    private SelectableResourceListAdapter mSkillsAdapter;
    private GestureDetectorCompat mGestureDetector;
    protected RecyclerView mSkillsRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private List<ResourceModel> mSkills = Lists.newArrayList();
    private List<ResourceModel> mOpportunitySkills = Lists.newArrayList();
    private List<Skill> mUserSelectedSkills = Lists.newArrayList();

    OnInterestSelectedListener mCallback;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OpportunitySkillsFragment newInstance(int sectionNumber, List<Skill> skills, boolean readOnly) {
        OpportunitySkillsFragment fragment = new OpportunitySkillsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(SKILLS_TAG, Lists.newArrayList(skills));
        args.putBoolean(READ_ONLY_TAG, readOnly);
        fragment.setArguments(args);
        return fragment;
    }

    public OpportunitySkillsFragment() {
    }

    private void loadSkills() {
        if(mSkills.size() <= 0)
            Skill.findInBackground(new FindCallback<Skill>() {
                @Override
                public void done(List<Skill> objects, ParseException e) {
                    if (e == null) {
                        for (Skill skill : objects) {
                            ResourceModel converted = skill.convertToResourceModel();
                            converted.setSelected(mOpportunitySkills.contains(converted));
                            mSkills.add(converted);
                        }
                    }
                    mSkillsAdapter.notifyDataSetChanged();
                }
            });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean mReadOnly = getArguments().getBoolean(READ_ONLY_TAG);
        mSkillsAdapter = new SelectableResourceListAdapter(mSkills, !mReadOnly);
        List<Skill> userSkills = (List<Skill>) getArguments().getSerializable(SKILLS_TAG);
        if(userSkills != null && !userSkills.isEmpty()){
            for(Skill userSkill : userSkills){
                mOpportunitySkills.add(userSkill.convertToResourceModel());
            }
        }
        loadSkills();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.opportunity_skills_fragment, container, false);
        rootView.setTag(SKILLS_FRAG_TAG);
        mSkillsRecyclerView = (RecyclerView) rootView.findViewById(R.id.opportunity_skills_list);
        mSkillsRecyclerView.setHasFixedSize(true);
        mSkillsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mLayoutManager = new LinearLayoutManager(getActivity());
        for(ResourceModel skill : mSkills){
            skill.setSelected(mOpportunitySkills.contains(skill));
        }

        setSkillsRecyclerViewLayoutManager();

        mSkillsRecyclerView.setAdapter(mSkillsAdapter);
        mSkillsRecyclerView.addOnItemTouchListener(this);
        mGestureDetector = new GestureDetectorCompat(this.getActivity(), new SkillsGestureListener());

        ImageView navigateInterests = (ImageView) rootView.findViewById(R.id.skills_to_interests_section);
        navigateInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateSelection();
                ((OpportunityDetailActivity) getActivity()).onNavigateToInterestsClick(mUserSelectedSkills);
            }
        });

        ImageView navigateDetails = (ImageView) rootView.findViewById(R.id.skills_to_details_section);
        navigateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateSelection();
                ((OpportunityDetailActivity) getActivity()).onNavigateToDetailsClick(mUserSelectedSkills);
            }
        });

        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     */
    public void setSkillsRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mSkillsRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mSkillsRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mSkillsRecyclerView.setLayoutManager(mLayoutManager);
        mSkillsRecyclerView.scrollToPosition(scrollPosition);
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

    public boolean populateSelection() {
        if (mSkillsAdapter.getSelectedItems().size() > 0){
            for (String objectId: mSkillsAdapter.getSelectedItems()) {
                mUserSelectedSkills.add(ParseObject.createWithoutData(Skill.class, objectId));
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

    private class SkillsGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = mSkillsRecyclerView.findChildViewUnder(e.getX(), e.getY());
            //onClick(view);
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            View view = mSkillsRecyclerView.findChildViewUnder(e.getX(), e.getY());
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
