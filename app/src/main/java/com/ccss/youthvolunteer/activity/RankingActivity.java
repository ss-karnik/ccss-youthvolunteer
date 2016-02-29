package com.ccss.youthvolunteer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.RankingViewAdapter;
import com.ccss.youthvolunteer.model.RankingModel;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public class RankingActivity extends BaseActivity {

    private List<RankingModel> mVolunteers = Lists.newArrayList();
    private RankingViewAdapter mAdapter;
    private ProgressBar mProgressBar;
    private boolean mRankedByVolunteer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        mProgressBar = (ProgressBar) findViewById(R.id.ranking_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        mRankedByVolunteer = true;

        initializeData();

        RecyclerView mVolunteerView = (RecyclerView) findViewById(R.id.ranking_list_view);
        mVolunteerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mVolunteerView.setLayoutManager(layoutManager);
        mAdapter = new RankingViewAdapter(this, mVolunteers);
        mVolunteerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ranking, menu);
        return true;
    }

    private void initializeData(){
        VolunteerUser.findUsersRanked(new FindCallback<VolunteerUser>() {
            @Override
            public void done(List<VolunteerUser> objects, ParseException e) {
                // for(int rank = 0; rank < objects.size(); rank++){
                for (VolunteerUser user : objects) {
                    String userPointsAndRank = String.format("# %s with %s points", objects.indexOf(user), user.getPointsAccrued());
                    mVolunteers.add(new RankingModel(user.getFullName(), user.getUserLevel().getTitle(),
                            user.getSchoolName(), userPointsAndRank, user.getProfileImageUri()));
                }
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_volunteer_rank:
                //Show the ranking by user
                return true;

            case R.id.action_school_rank:
                //Show the ranking by school
                return true;

            case R.id.nav_home:
                startActivity(MainActivity.class);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
