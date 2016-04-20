package com.ccss.youthvolunteer.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectableResourceListAdapter;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.model.UserAction;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DividerItemDecoration;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

/**
 * Skill fragment containing a recycler view.
 */
public class OpportunityVolunteeredUsersFragment extends Fragment  {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String VOLUNTEERED_USERS_FRAG_TAG = "VolunteeredUsersViewFragment";
    private static final String OPPORTUNITY_ID = "OpportunityId";

    private SelectableResourceListAdapter mUsersAdapter;
    protected RecyclerView mUsersRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private List<ResourceModel> mParticipatedUsers = Lists.newArrayList();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OpportunityVolunteeredUsersFragment newInstance(int sectionNumber, String opportunityId) {
        OpportunityVolunteeredUsersFragment fragment = new OpportunityVolunteeredUsersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(OPPORTUNITY_ID, opportunityId);
        fragment.setArguments(args);
        return fragment;
    }

    public OpportunityVolunteeredUsersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadVolunteerUsers(getArguments().getString(OPPORTUNITY_ID));
        mUsersAdapter = new SelectableResourceListAdapter(mParticipatedUsers, true, Constants.GENERAL_ITEM);
    }

    private void loadVolunteerUsers(String opportunityId) {
        if(mParticipatedUsers.size() <= 0)
            UserAction.findUsersForAction(VolunteerOpportunity.createWithoutData(VolunteerOpportunity.class, opportunityId), new FindCallback<UserAction>() {
                @Override
                public void done(List<UserAction> objects, ParseException e) {
                    if (e == null) {
                        for (UserAction action : objects) {
                            mParticipatedUsers.add(VolunteerUser.convertToResourceModel(action.getActionBy()));
                        }
                    }
                    mUsersAdapter.notifyDataSetChanged();
                }
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.opportunity_volunteered_users_fragment, container, false);
        rootView.setTag(VOLUNTEERED_USERS_FRAG_TAG);
        mUsersRecyclerView = (RecyclerView) rootView.findViewById(R.id.volunteered_user_list);
        mUsersRecyclerView.setHasFixedSize(true);
        mUsersRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mLayoutManager = new LinearLayoutManager(getActivity());

        setRecyclerViewLayoutManager();

        mUsersRecyclerView.setAdapter(mUsersAdapter);

        ImageView navigateInterestedUsers = (ImageView) rootView.findViewById(R.id.go_to_interested_users_section);
        navigateInterestedUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OpportunityDetailActivity) getActivity()).onNavigateToInterestedUsersClick();
            }
        });

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
}
