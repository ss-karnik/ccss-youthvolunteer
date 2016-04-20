package com.ccss.youthvolunteer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectableResourceListAdapter;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DividerItemDecoration;
import com.google.common.collect.Lists;

import java.util.List;

public class WallActivity extends BaseActivity
        implements RecyclerView.OnItemTouchListener, View.OnClickListener {

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    protected Constants.LayoutManagerType mCurrentLayoutManagerType;

    private FloatingActionButton mFabAddPost;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyListMessage;
    protected RecyclerView.LayoutManager mLayoutManager;
    private SelectableResourceListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    GestureDetectorCompat gestureDetector;
    private List<ResourceModel> mPosts = Lists.newArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        Toolbar toolbar = (Toolbar) findViewById(R.id.wall_toolbar);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.wall_posts_container);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataItems();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.wall_posts);
        mEmptyListMessage = (TextView) findViewById(R.id.empty_posts);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mFabAddPost = (FloatingActionButton) findViewById(R.id.fab_add_post);
        mFabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPostActivity("");
            }
        });

        mAdapter = new SelectableResourceListAdapter(mPosts);
        mAdapter.setOnItemClickListener(new ManageResourcesFragment.RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String resourceItemType, String resourceObjectId) {
                startPostActivity(resourceObjectId);
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });
        gestureDetector = new GestureDetectorCompat(this, new RecyclerViewGestureListener());

        loadPosts();
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (Constants.LayoutManagerType) savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnItemTouchListener(this);
    }

    private void refreshDataItems() {
        mPosts.clear();
        loadPosts();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void loadPosts() {

    }

    private void startPostActivity(String resourceObjectId) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra(Constants.OBJECT_ID_KEY, resourceObjectId);
        startActivity(intent);
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
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Constants.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onClick(View view) {

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
            //actionMode = this.startActionMode(this);
            int idx = mRecyclerView.getChildAdapterPosition(view);
            //myToggleSelection(idx);
            super.onLongPress(e);
        }
    }
}
