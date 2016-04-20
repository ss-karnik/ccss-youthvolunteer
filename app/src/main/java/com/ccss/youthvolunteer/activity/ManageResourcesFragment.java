package com.ccss.youthvolunteer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.ccss.youthvolunteer.model.Announcement;
import com.ccss.youthvolunteer.model.Category;
import com.ccss.youthvolunteer.model.Interest;
import com.ccss.youthvolunteer.model.InterestGroup;
import com.ccss.youthvolunteer.model.Organization;
import com.ccss.youthvolunteer.model.Recognition;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.model.School;
import com.ccss.youthvolunteer.model.Skill;
import com.ccss.youthvolunteer.model.SpecialUser;
import com.ccss.youthvolunteer.model.UserAction;
import com.ccss.youthvolunteer.model.VolunteerOpportunity;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DividerItemDecoration;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * This lists the Resource for management screen
 */
public class ManageResourcesFragment extends Fragment
        implements RecyclerView.OnItemTouchListener, View.OnClickListener, ActionMode.Callback{

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final String TAG = "ResourcesListFragment";
    protected Constants.LayoutManagerType mCurrentLayoutManagerType;
    String mResourceType;
    String mUserOrganization;

    private List<ResourceModel> mResources = Lists.newArrayList();
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
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
        List<String> selectedItemPositions = mAdapter.getSelectedItems();
        String currItem;
        switch (menuItem.getItemId()) {
            case R.id.manage_action_delete:
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    currItem = selectedItemPositions.get(i);
                    //TODO
//                    removeItemFromList(currItem);
//                    mAdapter.removeData(currItem);
                }
                actionMode.finish();
                return true;
            case R.id.manage_action_approve:
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    currItem = selectedItemPositions.get(i);
                    //TODO
//                    removeItemFromList(currItem);
//                    mAdapter.removeData(currItem);
                }
                actionMode.finish();
                return true;
            case R.id.manage_action_reject:
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    currItem = selectedItemPositions.get(i);
                    //TODO
//                    removeItemFromList(currItem);
//                    mAdapter.removeData(currItem);
                    //saveAllInBackground()
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ManageResourcesFragment.
     */
    public static ManageResourcesFragment newInstance(String resourceType) {
        ManageResourcesFragment fragment = new ManageResourcesFragment();
        Bundle args = new Bundle();
        args.putString(Constants.MANAGE_ITEM_KEY, resourceType);
        fragment.setArguments(args);

        return fragment;
    }

    public static ManageResourcesFragment newInstance(String resourceType, String organizationName) {
        ManageResourcesFragment fragment = new ManageResourcesFragment();
        Bundle args = new Bundle();
        args.putString(Constants.MANAGE_ITEM_KEY, resourceType);
        args.putString(Constants.USER_ORGANIZATION_KEY, organizationName);
        fragment.setArguments(args);

        return fragment;
    }

    public ManageResourcesFragment() {
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
            loadDataItems();
        }
    }

    private void loadDataItems(){
        switch (mResourceType) {
            case Constants.ANNOUNCEMENT_RESOURCE:
                Announcement.findInBackground(new FindCallback<Announcement>() {
                    @Override
                    public void done(List<Announcement> objects, ParseException e) {
                        if (e == null) {
                            if(objects.isEmpty()){
                                mEmptyListMessage.setText(R.string.no_announcements_available);
                                mEmptyListMessage.setVisibility(View.VISIBLE);
                                //mRecyclerView.setVisibility(View.GONE);
                            } else {
                                //mRecyclerView.setVisibility(View.VISIBLE);
                                mEmptyListMessage.setVisibility(View.INVISIBLE);
                                for (Announcement announcement : objects) {
                                    mResources.add(announcement.convertToResourceModel());
                                }
                            }
                        }
                        onDataLoadComplete();
                    }
                });
                break;

            case Constants.CATEGORY_RESOURCE:
                Category.findInBackground(false, false, new FindCallback<Category>() {
                    @Override
                    public void done(List<Category> objects, ParseException e) {
                        if (e == null) {
                            if(objects.isEmpty()){
                                mEmptyListMessage.setText(R.string.no_categories_available);
                                mEmptyListMessage.setVisibility(View.VISIBLE);
                                //mRecyclerView.setVisibility(View.GONE);
                            } else {
                                //mRecyclerView.setVisibility(View.VISIBLE);
                                mEmptyListMessage.setVisibility(View.INVISIBLE);
                                for (Category category : objects) {
                                    mResources.add(category.convertToResourceModel());
                                }
                            }
                        }
                        onDataLoadComplete();
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
                                //mRecyclerView.setVisibility(View.GONE);
                            } else {
                                //mRecyclerView.setVisibility(View.VISIBLE);
                                mEmptyListMessage.setVisibility(View.INVISIBLE);
                                for (Recognition recognition : objects) {
                                    mResources.add(recognition.convertToResourceModel());
                                }
                            }
                        }
                        onDataLoadComplete();
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
                                //mRecyclerView.setVisibility(View.GONE);
                            } else {
                                //mRecyclerView.setVisibility(View.VISIBLE);
                                mEmptyListMessage.setVisibility(View.INVISIBLE);
                                for (Interest interest : objects) {
                                    mResources.add(interest.convertToResourceModel());
                                }
                            }
                        }
                        onDataLoadComplete();
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
                                //mRecyclerView.setVisibility(View.GONE);
                            } else {
                                //mRecyclerView.setVisibility(View.VISIBLE);
                                mEmptyListMessage.setVisibility(View.INVISIBLE);
                                for (Skill skill : objects) {
                                    mResources.add(skill.convertToResourceModel());
                                }
                            }
                        }
                        onDataLoadComplete();
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
                                //mRecyclerView.setVisibility(View.GONE);
                            } else {
                                //mRecyclerView.setVisibility(View.VISIBLE);
                                mEmptyListMessage.setVisibility(View.INVISIBLE);
                                for (SpecialUser specialUser : objects) {
                                    mResources.add(specialUser.convertToResourceModel());
                                }
                            }
                        }
                        onDataLoadComplete();
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
                                //mRecyclerView.setVisibility(View.GONE);
                            } else {
                                //mRecyclerView.setVisibility(View.VISIBLE);
                                mEmptyListMessage.setVisibility(View.INVISIBLE);
                                for (School school : objects) {
                                    mResources.add(school.convertToResourceModel());
                                }
                            }
                        }
                        onDataLoadComplete();
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
                                //mRecyclerView.setVisibility(View.GONE);
                            } else {
                                //mRecyclerView.setVisibility(View.VISIBLE);
                                mEmptyListMessage.setVisibility(View.INVISIBLE);
                                for (InterestGroup groupMember : objects) {
                                    mResources.add(groupMember.convertToResourceModel());
                                }
                            }
                        }
                        onDataLoadComplete();
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
                                //mRecyclerView.setVisibility(View.GONE);
                            } else {
                                //mRecyclerView.setVisibility(View.VISIBLE);
                                mEmptyListMessage.setVisibility(View.INVISIBLE);
                                for (Organization org : objects) {
                                    mResources.add(org.convertToResourceModel());
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        mProgressBar.setVisibility(View.INVISIBLE);
                        if(mSwipeRefreshLayout.isRefreshing()){
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
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

            case Constants.USER_ACTION_RESOURCE:
                if(Strings.isNullOrEmpty(mUserOrganization)) {
                    UserAction.findUnverifiedUserActions("", findUserActionsCallback());
                } else {
                    UserAction.findUnverifiedUserActions(mUserOrganization, findUserActionsCallback());
                }
                break;
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
                        //mRecyclerView.setVisibility(View.GONE);
                    } else {
                        //mRecyclerView.setVisibility(View.VISIBLE);
                        mEmptyListMessage.setVisibility(View.INVISIBLE);
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

    @NonNull
    private FindCallback<UserAction> findUserActionsCallback() {
        return new FindCallback<UserAction>() {
            @Override
            public void done(List<UserAction> objects, ParseException e) {
                if (e == null) {
                    if(objects.isEmpty()){
                        mEmptyListMessage.setText(R.string.no_pending_actions_available);
                        mEmptyListMessage.setVisibility(View.VISIBLE);
                        //mRecyclerView.setVisibility(View.GONE);
                    } else {
                        //mRecyclerView.setVisibility(View.VISIBLE);
                        mEmptyListMessage.setVisibility(View.INVISIBLE);
                        for (UserAction userAction : objects) {
                            VolunteerUser actionPerformer = VolunteerUser.getVolunteerUser(userAction.getActionBy());
                            SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT);

                            mResources.add(new ResourceModel(Constants.USER_ACTION_RESOURCE, actionPerformer.getFullName(),
                                    userAction.getAction().getTitle(), dateFormatter.format(userAction.getActionDate()), "",
                                    userAction.getAction().getOrganizationName(), "", userAction.getObjectId(),
                                    actionPerformer.getProfileImageUri(), true, false));
                        }
                    }
                }
                getActivity().invalidateOptionsMenu();
                onDataLoadComplete();
            }
        };
    }

    private void onDataLoadComplete() {
        mAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
        if(mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.manage_resources_list_fragment, container, false);

        layout.setTag(TAG);
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.resources_container);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
//        mSwipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_light,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataItems();
            }
        });

        mRecyclerView = (RecyclerView) layout.findViewById(R.id.resource_list);
        mEmptyListMessage = (TextView) layout.findViewById(R.id.empty_resource_list);
        mRecyclerView.setHasFixedSize(true);
        //TODO: Decide whether this Divider is needed
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));


//        HeaderDecoration headerDecoration = new HeaderDecoration(container, true, 10f, 1f, 1);
//        mRecyclerView.addItemDecoration(headerDecoration);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mProgressBar = (ProgressBar) layout.findViewById(R.id.resource_list_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (Constants.LayoutManagerType) savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.MANAGE_RESOURCE_REQUEST_CODE) {
                mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
            }
        }
    }

    private void refreshDataItems() {
        mResources.clear();
        mAdapter.notifyDataSetChanged();
        loadDataItems();
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
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        if(Constants.USER_ACTION_RESOURCE.equals(mResourceType)){
            menu.findItem(R.id.manage_action_approve).setVisible(true).setEnabled(true);
            menu.findItem(R.id.manage_action_reject).setVisible(true).setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.manage_action_approve) {
            actionSelections(true);
            return true;
        }

        if (id == R.id.manage_action_reject) {
            actionSelections(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void actionSelections(boolean isApproved) {
        List<String> selectedItemPositions = mAdapter.getSelectedItems();
        String currItem;
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            currItem = selectedItemPositions.get(i);
            //TODO
//                    removeItemFromList(currItem);
//                    mAdapter.removeData(currItem);
        }
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
            actionMode = getActivity().startActionMode(ManageResourcesFragment.this);
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
