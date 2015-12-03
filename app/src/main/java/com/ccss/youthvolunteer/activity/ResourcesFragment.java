package com.ccss.youthvolunteer.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.ResourceListAdapter;
import com.ccss.youthvolunteer.model.Category;
import com.ccss.youthvolunteer.model.Interests;
import com.ccss.youthvolunteer.model.Organization;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.model.School;
import com.ccss.youthvolunteer.model.Skills;
import com.ccss.youthvolunteer.model.SpecialUser;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.util.Constants;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResourcesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResourcesFragment extends Fragment {

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final String TAG = "ResourcesListFragment";
    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    private List<ResourceModel> mResources = Lists.newArrayList();
    private RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private ResourceListAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ResourcesFragment.
     */
    public static ResourcesFragment newInstance(String resourceType) {
        ResourcesFragment fragment = new ResourcesFragment();
        Bundle args = new Bundle();
        args.putString(Constants.MANAGE_ITEM_KEY, resourceType);
        fragment.setArguments(args);

        return fragment;
    }

    public static ResourcesFragment newInstance(String resourceType, String organizationName) {
        ResourcesFragment fragment = new ResourcesFragment();
        Bundle args = new Bundle();
        args.putString(Constants.MANAGE_ITEM_KEY, resourceType);
        args.putString(Constants.USER_ORGANIZATION_KEY, organizationName);
        fragment.setArguments(args);

        return fragment;
    }

    public ResourcesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String resourceType = getArguments().getString(Constants.MANAGE_ITEM_KEY);
        String userOrganization = getArguments().getString(Constants.USER_ORGANIZATION_KEY);

        if(resourceType != null) {
            switch (resourceType) {
                case Constants.CATEGORY_RESOURCE:
                    Category.findInBackground(new FindCallback<Category>() {
                        @Override
                        public void done(List<Category> objects, ParseException e) {
                            if (e == null) {
                                for (Category category : objects) {
                                    mResources.add(category.convertToResourceModel());
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    break;

                case Constants.INTEREST_RESOURCE:
                    Interests.findInBackground(new FindCallback<Interests>() {
                        @Override
                        public void done(List<Interests> objects, ParseException e) {
                            if (e == null) {
                                for (Interests interest : objects) {
                                    mResources.add(interest.convertToResourceModel());
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    break;
                case Constants.SKILL_RESOURCE:
                    Skills.findInBackground(new FindCallback<Skills>() {
                        @Override
                        public void done(List<Skills> objects, ParseException e) {
                            if (e == null) {
                                for (Skills skill : objects) {
                                    mResources.add(skill.convertToResourceModel());
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    break;
                case Constants.USER_RESOURCE:
                    List<SpecialUser> specialUsers = SpecialUser.getAllSpecialUsers();
                    for (SpecialUser specialUser : specialUsers) {
                        mResources.add(specialUser.convertToResourceModel());
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
                case Constants.SCHOOL_RESOURCE:
                    School.findInBackground(new FindCallback<School>() {
                        @Override
                        public void done(List<School> objects, ParseException e) {
                            if (e == null) {
                                for (School school : objects) {
                                    mResources.add(school.convertToResourceModel());
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    break;
                case Constants.GROUP_RESOURCE:
//                    IG.findInBackground(new FindCallback<SpecialUser>() {
//                        @Override
//                        public void done(List<SpecialUser> objects, ParseException e) {
//                            if (e == null) {
//                                for (SpecialUser specialUser : objects) {
//                                    mResources.add(specialUser.convertToResourceModel());
//                                }
//                            }
//                        }
//                    });
                    break;
                case Constants.ORGANIZATION_RESOURCE:
                    Organization.findInBackground(new FindCallback<Organization>() {
                        @Override
                        public void done(List<Organization> objects, ParseException e) {
                            if (e == null) {
                                for (Organization org : objects) {
                                    mResources.add(org.convertToResourceModel());
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    break;
                case Constants.OPPORTUNITY_RESOURCE:
                    VolunteerOpportunity.getAllOpportunities(new FindCallback<VolunteerOpportunity>() {
                        @Override
                        public void done(List<VolunteerOpportunity> objects, ParseException e) {
                            if (e == null) {
                                for (VolunteerOpportunity opportunity : objects) {
                                    mResources.add(opportunity.convertToResourceModel());
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    break;
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.manage_resources_list_fragment, container, false);

        layout.setTag(TAG);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.resource_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        ProgressBar mProgressBar = (ProgressBar) layout.findViewById(R.id.resource_list_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager();

        mAdapter = new ResourceListAdapter(mResources);
        mRecyclerView.setAdapter(mAdapter);

        mProgressBar.setVisibility(View.GONE);
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

}
