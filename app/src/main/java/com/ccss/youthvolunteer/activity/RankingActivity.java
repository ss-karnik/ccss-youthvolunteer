package com.ccss.youthvolunteer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        mProgressBar = (ProgressBar) findViewById(R.id.ranking_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        initializeData();

        RecyclerView mVolunteerView = (RecyclerView) findViewById(R.id.ranking_list_view);
        mVolunteerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mVolunteerView.setLayoutManager(layoutManager);
        mAdapter = new RankingViewAdapter(this, mVolunteers);
        mVolunteerView.setAdapter(mAdapter);
    }

    private void initializeData(){
        VolunteerUser.findUsersRanked(new FindCallback<VolunteerUser>() {
            @Override
            public void done(List<VolunteerUser> objects, ParseException e) {
               // for(int rank = 0; rank < objects.size(); rank++){
                for(VolunteerUser user : objects){
                    String userPointsAndRank = String.format("# %s with %s points", objects.indexOf(user), user.getPointsAccrued());
                    mVolunteers.add(new RankingModel(user.getFullName(), user.getUserLevel().getTitle(),
                            user.getSchoolName(), userPointsAndRank, user.getProfileImageUri()));
                }
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
