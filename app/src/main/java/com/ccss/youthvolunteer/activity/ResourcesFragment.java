package com.ccss.youthvolunteer.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectableResourceListAdapter;
import com.ccss.youthvolunteer.model.Category;
import com.ccss.youthvolunteer.model.Interest;
import com.ccss.youthvolunteer.model.InterestGroup;
import com.ccss.youthvolunteer.model.Organization;
import com.ccss.youthvolunteer.model.Recognition;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.model.School;
import com.ccss.youthvolunteer.model.Skill;
import com.ccss.youthvolunteer.model.SpecialUser;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DividerItemDecoration;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.Date;
import java.util.List;

/**
 * This lists the Resource for management screen
 */
public class ResourcesFragment extends Fragment
        implements RecyclerView.OnItemTouchListener, View.OnClickListener, ActionMode.Callback{

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final String TAG = "ResourcesListFragment";
    protected LayoutManagerType mCurrentLayoutManagerType;
    String mResourceType;
    String mUserOrganization;

    private List<ResourceModel> mResources = Lists.newArrayList();
    private RecyclerView mRecyclerView;
    private TextView mEmptyListMessage;
    protected RecyclerView.LayoutManager mLayoutManager;
    private SelectableResourceListAdapter mAdapter;
    private ProgressBar mProgressBar;
    int itemCount;
    GestureDetectorCompat gestureDetector;
    ActionMode actionMode;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.menu_manage_resource, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.manage_action_delete:
                List<String> selectedItemPositions = mAdapter.getSelectedItems();
                String currItem;
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    currItem = selectedItemPositions.get(i);
                    //TODO
//                    removeItemFromList(currItem);
//                    mAdapter.removeData(currItem);
                }
                actionMode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.actionMode = null;
        mAdapter.clearSelections();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.resource_view) {
            // item click
            int idx = mRecyclerView.getChildAdapterPosition(view);
            ResourceModel model = mAdapter.getItem(idx);
            Intent intent = new Intent(getActivity(), ManageSingleResourceActivity.class);
            intent.putExtra(Constants.MANAGE_ITEM_KEY, model.getResourceType());
            intent.putExtra(Constants.OBJECT_ID_KEY, model.getObjectId());
            intent.putExtra(Constants.USER_ORGANIZATION_KEY, mUserOrganization);
            startActivityForResult(intent, Constants.MANAGE_RESOURCE_REQUEST_CODE);
            return;
            //mAdapter.toggleSelection(idx);
//            String title = getString(R.string.selected_count, mAdapter.getSelectedItems().size());
//            actionMode.setTitle(title);

//            if (actionMode != null) {
//                myToggleSelection(idx);
//                return;
//            }
//            ResourceModel data = mAdapter.getItem(idx);
//            View innerContainer = view.findViewById(R.id.container_inner_item);
//            innerContainer.setTransitionName(Constants.NAME_INNER_CONTAINER + "_" + data.getObjectId());
//            Intent startIntent = new Intent(getActivity(), ManageSingleResourceActivity.class);
//            startIntent.putExtra(Constants.MANAGE_ITEM_KEY, resourceType);
//            startIntent.putExtra(Constants.OBJECT_ID_KEY, resourceObjectId);
//            startIntent.putExtra(Constants.USER_ORGANIZATION_KEY, userOrganization);
//            ActivityOptions options = ActivityOptions
//                    .makeSceneTransitionAnimation(this, innerContainer, Constants.NAME_INNER_CONTAINER);
//            this.startActivity(startIntent);
        }
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

    private void myToggleSelection(int idx) {
        ResourceModel model = mAdapter.getItem(idx);
        Intent intent = new Intent(getActivity(), ManageSingleResourceActivity.class);
                intent.putExtra(Constants.MANAGE_ITEM_KEY, model.getResourceType());
                intent.putExtra(Constants.OBJECT_ID_KEY, model.getObjectId());
                intent.putExtra(Constants.USER_ORGANIZATION_KEY, mUserOrganization);
                startActivityForResult(intent, Constants.MANAGE_RESOURCE_REQUEST_CODE);
        //mAdapter.toggleSelection(idx);
        String title = getString(R.string.selected_count, mAdapter.getSelectedItems().size());
        actionMode.setTitle(title);
    }

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

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

    public void onItemClicked(String objectId){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mResourceType = getArguments().getString(Constants.MANAGE_ITEM_KEY);
        mUserOrganization = getArguments().getString(Constants.USER_ORGANIZATION_KEY);

        mAdapter = new SelectableResourceListAdapter(mResources);
        mAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String resourceItemType, String resourceObjectId) {
                Intent intent = new Intent(getActivity(), ManageSingleResourceActivity.class);
                intent.putExtra(Constants.MANAGE_ITEM_KEY, mResourceType);
                intent.putExtra(Constants.OBJECT_ID_KEY, resourceObjectId);
                intent.putExtra(Constants.USER_ORGANIZATION_KEY, mUserOrganization);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });
        gestureDetector = new GestureDetectorCompat(getActivity(), new RecyclerViewGestureListener());

        if(mResourceType != null) {
            switch (mResourceType) {
                case Constants.CATEGORY_RESOURCE:
                    Category.findInBackground(new FindCallback<Category>() {
                        @Override
                        public void done(List<Category> objects, ParseException e) {
                            if (e == null) {
                                if(objects.isEmpty()){
                                    mEmptyListMessage.setText(R.string.no_categories_available);
                                    mEmptyListMessage.setVisibility(View.VISIBLE);
                                } else {
                                    for (Category category : objects) {
                                        mResources.add(category.convertToResourceModel());
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                    break;

                case Constants.RECOGNITION_RESOURCE:
                    Recognition.findInBackground(new FindCallback<Recognition>() {
                        @Override
                        public void done(List<Recognition> objects, ParseException e) {
                            if (e == null) {
                                if(objects.isEmpty()){
                                    mEmptyListMessage.setText(R.string.no_recog_available);
                                    mEmptyListMessage.setVisibility(View.VISIBLE);
                                } else {
                                    for (Recognition recognition : objects) {
                                        mResources.add(recognition.convertToResourceModel());
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                    break;

                case Constants.INTEREST_RESOURCE:
                    Interest.findInBackground(new FindCallback<Interest>() {
                        @Override
                        public void done(List<Interest> objects, ParseException e) {
                            if (e == null) {
                                if(objects.isEmpty()){
                                    mEmptyListMessage.setText(R.string.no_interests_available);
                                    mEmptyListMessage.setVisibility(View.VISIBLE);
                                } else {
                                    for (Interest interest : objects) {
                                        mResources.add(interest.convertToResourceModel());
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                    break;
                case Constants.SKILL_RESOURCE:
                    Skill.findInBackground(new FindCallback<Skill>() {
                        @Override
                        public void done(List<Skill> objects, ParseException e) {
                            if (e == null) {
                                if(objects.isEmpty()){
                                    mEmptyListMessage.setText(R.string.no_skills_available);
                                    mEmptyListMessage.setVisibility(View.VISIBLE);
                                } else {
                                    for (Skill skill : objects) {
                                        mResources.add(skill.convertToResourceModel());
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                    break;
                case Constants.USER_RESOURCE:
                    SpecialUser.findInBackground(new FindCallback<SpecialUser>() {
                        @Override
                        public void done(List<SpecialUser> objects, ParseException e) {
                            if (e == null) {
                                if(objects.isEmpty()){
                                    mEmptyListMessage.setText(R.string.no_sp_available);
                                    mEmptyListMessage.setVisibility(View.VISIBLE);
                                } else {
                                    for (SpecialUser specialUser : objects) {
                                        mResources.add(specialUser.convertToResourceModel());
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                    break;
                case Constants.SCHOOL_RESOURCE:
                    School.findInBackground(new FindCallback<School>() {
                        @Override
                        public void done(List<School> objects, ParseException e) {
                            if (e == null) {
                                if(objects.isEmpty()){
                                    mEmptyListMessage.setText(R.string.no_schools_available);
                                    mEmptyListMessage.setVisibility(View.VISIBLE);
                                } else {
                                    for (School school : objects) {
                                        mResources.add(school.convertToResourceModel());
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                    break;
                case Constants.GROUP_RESOURCE:
                    InterestGroup.findInBackground(new FindCallback<InterestGroup>() {
                        @Override
                        public void done(List<InterestGroup> objects, ParseException e) {
                            if (e == null) {
                                if(objects.isEmpty()){
                                    mEmptyListMessage.setText(R.string.no_groups_available);
                                    mEmptyListMessage.setVisibility(View.VISIBLE);
                                } else {
                                    for (InterestGroup groupMember : objects) {
                                        mResources.add(groupMember.convertToResourceModel());
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                    break;
                case Constants.ORGANIZATION_RESOURCE:
                    Organization.findInBackground(new FindCallback<Organization>() {
                        @Override
                        public void done(List<Organization> objects, ParseException e) {
                            if (e == null) {
                                if(objects.isEmpty()){
                                    mEmptyListMessage.setText(R.string.no_orgs_available);
                                    mEmptyListMessage.setVisibility(View.VISIBLE);
                                } else {
                                    for (Organization org : objects) {
                                        mResources.add(org.convertToResourceModel());
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                    break;
                case Constants.OPPORTUNITY_RESOURCE:
                    if(Strings.isNullOrEmpty(mUserOrganization)) {
                        VolunteerOpportunity.getAllOpportunities(findOpportunitiesCallback());
                    } else {
                        VolunteerOpportunity.getOpportunitiesForOrganization(mUserOrganization, false, findOpportunitiesCallback());
                    }
                    break;
            }
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.manage_resources_list_fragment, container, false);

        layout.setTag(TAG);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.resource_list);
        mEmptyListMessage = (TextView) layout.findViewById(R.id.empty_resource_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
//        HeaderDecoration headerDecoration = new HeaderDecoration(container, true, 10f, 1f, 1);
//        mRecyclerView.addItemDecoration(headerDecoration);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mProgressBar = (ProgressBar) layout.findViewById(R.id.resource_list_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager();

        mRecyclerView.setAdapter(mAdapter);
//        RecyclerItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new RecyclerItemClickSupport.OnItemClickListener() {
//            @Override
//            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                Intent intent = new Intent(getActivity(), ManageSingleResourceActivity.class);
////                intent.putExtra(Constants.MANAGE_ITEM_KEY, );
////                intent.putExtra(Constants.OBJECT_ID_KEY, );
////                intent.putExtra(Constants.USER_ORGANIZATION_KEY, );
//                startActivity(intent);
//            }
//        });
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnItemTouchListener(this);

//        mAdapter.setOnItemClickListener(new RecyclerViewClickListener() {
//            @Override
//            public void onItemClick(int position, View v) {
//                Log.d(TAG, "onItemClick position: " + position);
//            }
//
//            @Override
//            public void onItemLongClick(int position, View v) {
//                Log.d(TAG, "onItemLongClick pos = " +
//            }
//        });

       //mAdapter.setOnClickListener(new View.OnItemClickListener())

//        mRecyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        Bundle bundle = new Bundle();
//                        bundle.putInt("id", position);
//
//                        Intent i = new Intent(getActivity(), MainViewPager.class);
//                        i.putExtras(bundle);
//                        startActivity(i);
//                    }
//                })
//        );
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


    public interface RecyclerItemClickListener{
        void onItemClick(View view, String resourceType, String objectId);
        void onItemLongClick(int position, View v);
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
            actionMode = getActivity().startActionMode(ResourcesFragment.this);
            int idx = mRecyclerView.getChildAdapterPosition(view);
            myToggleSelection(idx);
            super.onLongPress(e);
        }
    }

//    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
//        private OnItemClickListener mListener;
//
//        public interface OnItemClickListener {
//            void onItemClick(View view, int position);
//        }
//
//        GestureDetector mGestureDetector;
//
//        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
//            mListener = listener;
//            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//                @Override public boolean onSingleTapUp(MotionEvent e) {
//                    return true;
//                }
//            });
//        }
//
//        @Override
//        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//        }
//
//        @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
//            View childView = view.findChildViewUnder(e.getX(), e.getY());
//            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
//                mListener.onItemClick(childView, view.getChildPosition(childView));
//                return true;
//            }
//            return false;
//        }
//
//        @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }
//    }

}
