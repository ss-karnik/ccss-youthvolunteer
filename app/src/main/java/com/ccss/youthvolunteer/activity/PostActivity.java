package com.ccss.youthvolunteer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.SelectableResourceListAdapter;
import com.ccss.youthvolunteer.model.Announcement;
import com.ccss.youthvolunteer.model.Post;
import com.ccss.youthvolunteer.model.ResourceModel;
import com.ccss.youthvolunteer.util.Constants;
import com.google.common.collect.Lists;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class PostActivity extends BaseActivity  {

    private ProgressBar mProgressBar;
    private EditText mPostText;
    private TextView mEmptyListMessage;

    private Post mPostItem;
    private String mPostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mEmptyListMessage = (TextView) findViewById(R.id.empty_posts);
        mPostText = (EditText) findViewById(R.id.post_content);
        mProgressBar = (ProgressBar) findViewById(R.id.post_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(Constants.OBJECT_ID_KEY)){
            mPostId = bundle.getString(Constants.OBJECT_ID_KEY);
        }

        if(mPostId != null && !mPostId.isEmpty()){
            ParseQuery<Post> postQuery = ParseQuery.getQuery(Post.class);
            postQuery.include("comments");
            postQuery.include("postBy");
            postQuery.whereEqualTo(Constants.OBJECT_ID_KEY, mPostId);
            postQuery.getFirstInBackground(new GetCallback<Post>() {
                @Override
                public void done(Post object, ParseException e) {
                    if (e == null) {
                        mPostItem = object;
                    } else {
                        mPostItem = new Post();
                    }

                    if(!mPostId.isEmpty()) {
                        mPostText.setText(mPostItem.getPostText());
                    }

                    mPostText.setEnabled(ParseUser.getCurrentUser().equals(mPostItem.getPostBy()));

                    //TODO: Load comments

                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }


    }

    private void loadPostData() {

    }
}
